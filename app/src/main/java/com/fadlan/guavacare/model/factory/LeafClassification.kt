package com.fadlan.guavacare.model.factory

import android.content.Context
import android.content.res.AssetManager
import android.content.res.TypedArray
import android.graphics.*
import android.util.Log
import com.fadlan.guavacare.R
import com.fadlan.guavacare.model.Detection
import com.priyankvasa.android.cameraviewex.Image
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class LeafClassification(context: Context) {

    companion object {
        private const val BATCH_SIZE = 1
        private const val MODEL_INPUT_SIZE = 224
        private const val BYTES_PER_CHANNEL = 4
        private const val CHANNEL_SIZE = 3
        private const val IMAGE_STD = 255f
    }

    private lateinit var dataPicture: TypedArray
    private lateinit var dataDetail: TypedArray
    private val tfLiteModel = Interpreter(getModelByteBuffer(context.assets), Interpreter.Options())
    private val dataName = context.resources.getStringArray(R.array.leafDiseaseName)

    private fun getModelByteBuffer(assetManager: AssetManager): MappedByteBuffer {
        val modelPath = "leaf_guava_disease_0drop.tflite"
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun prepare(context: Context) {
        dataPicture = context.resources.obtainTypedArray(R.array.leafDiseaseImage)
        dataDetail = context.resources.obtainTypedArray(R.array.detail_leaf_disease_layout)
    }

    fun recognizeTakenPicture(data: ByteArray, context: Context): ArrayList<Detection> {
        val unscaledBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        val bitmap = Bitmap.createScaledBitmap(
            unscaledBitmap,
            MODEL_INPUT_SIZE,
            MODEL_INPUT_SIZE,
            false)
        val byteBuffer = convertBitmapToByteBuffer(bitmap)
        Log.d("Classification", "ByteBuffer: $byteBuffer")
        val result = Array(BATCH_SIZE) { FloatArray(dataName.size) }
        tfLiteModel.run(byteBuffer, result)
        return parseResults(result, context)
    }

    fun recognizePreviewFrames(image: Image, context: Context): ArrayList<Detection> {
        val data: ByteArray
        val yuvImage = YuvImage(
            image.data,
            ImageFormat.NV21,
            image.width,
            image.height,
            null
        )
        val jpegDataStream = ByteArrayOutputStream()
        val previewFrameScale = 0.4f
        yuvImage.compressToJpeg(
            Rect(0, 0, image.width, image.height),
            (100 * previewFrameScale).toInt(),
            jpegDataStream
        )
        data = jpegDataStream.toByteArray()
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val unscaledBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        val bitmap = Bitmap.createScaledBitmap(
            unscaledBitmap,
            MODEL_INPUT_SIZE,
            MODEL_INPUT_SIZE,
            false)
        val byteBuffer = convertBitmapToByteBuffer(bitmap)
        val result = Array(BATCH_SIZE) { FloatArray(dataName.size) }
        tfLiteModel.run(byteBuffer, result)
        return parseResults(result, context)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap?): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(
            BATCH_SIZE *
                    MODEL_INPUT_SIZE *
                    MODEL_INPUT_SIZE *
                    BYTES_PER_CHANNEL *
                    CHANNEL_SIZE
        ).order(ByteOrder.nativeOrder())
        val pixelValues = IntArray(MODEL_INPUT_SIZE * MODEL_INPUT_SIZE)
        bitmap?.getPixels(pixelValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until MODEL_INPUT_SIZE) {
            for (j in 0 until MODEL_INPUT_SIZE) {
                val pixelValue = pixelValues[pixel++]
                byteBuffer.putFloat((pixelValue shr 16 and 0xFF) / IMAGE_STD)
                byteBuffer.putFloat((pixelValue shr 8 and 0xFF) / IMAGE_STD)
                byteBuffer.putFloat((pixelValue and 0xFF) / IMAGE_STD)
            }
        }
        return byteBuffer
    }

    private fun parseResults(result: Array<FloatArray>, context: Context): ArrayList<Detection> {
        prepare(context)
        val recognitions = ArrayList<Detection>()
        val dataAcc = result[0]
        for (position in dataName.indices) {
            val detection = Detection(
                dataName[position],
                dataAcc[position] * 100,
                dataPicture.getResourceId(position, -1),
                dataDetail.getResourceId(position, -1)
            )
            recognitions.add(detection)
        }
        dataPicture.recycle()
        dataDetail.recycle()
        return recognitions
    }
}