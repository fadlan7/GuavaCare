package com.fadlan.guavacare.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fadlan.guavacare.model.Detection
import com.fadlan.guavacare.model.factory.Classification
import com.priyankvasa.android.cameraviewex.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraViewModel : ViewModel() {

    companion object {
        private val TAG = CameraViewModel::class.java.simpleName
    }
    private lateinit var classifier: Classification
    //private var job: Job? = null
    private var detectionTask: AsyncTask<*, *, *>? = null
    private val listDetectionPicture = MutableLiveData<ArrayList<Detection>>()
    private val listDetectionPreviewFrames = MutableLiveData<ArrayList<Detection>>()

    fun setDetectionPicture(image: Image, context: Context) {
        classifier = Classification(context)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val recognitions = classifier.recognizeTakenPicture(image.data, context)
                Log.d(TAG, "$recognitions")
                withContext(Dispatchers.Main) {
                    listDetectionPicture.postValue(recognitions)
                    Log.d(TAG, "$listDetectionPicture")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
                    Log.d(TAG, "${e.message}")
                }
            }
        }
    }

    fun setDetectionPreviewFrames(image: Image, context: Context) {
        classifier = Classification(context)
        if (detectionTask != null && detectionTask?.status == AsyncTask.Status.RUNNING) {
            detectionTask?.cancel(true)
            detectionTask = null
        }
        @SuppressLint("StaticFieldLeak")
        detectionTask = object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                try {
                    val recognitions = classifier.recognizePreviewFrames(image, context)
                    listDetectionPreviewFrames.postValue(recognitions)
                } catch (e: Exception) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(context, "${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
                return null
            }
        }.execute()
    }

    fun getDetectionPicture(): LiveData<ArrayList<Detection>> = listDetectionPicture
    fun getDetectionPreviewFrames(): LiveData<ArrayList<Detection>> = listDetectionPreviewFrames
}