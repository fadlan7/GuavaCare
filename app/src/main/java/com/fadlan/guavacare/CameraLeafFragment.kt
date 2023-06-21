package com.fadlan.guavacare

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fadlan.guavacare.GuavaDiseaseDetailFragment.Companion.EXTRA_DISEASE_DETAIL
import com.fadlan.guavacare.GuavaDiseaseDetailFragment.Companion.EXTRA_DISEASE_IMAGE
import com.fadlan.guavacare.GuavaDiseaseDetailFragment.Companion.EXTRA_DISEASE_NAME
import com.fadlan.guavacare.adapter.PredictAdapter
import com.fadlan.guavacare.adapter.PredictLeafAdapter
import com.fadlan.guavacare.databinding.FragmentCameraLeafBinding
import com.fadlan.guavacare.manager.PermissionManager
import com.fadlan.guavacare.model.Detection
import com.fadlan.guavacare.preference.CameraPreference
import com.fadlan.guavacare.viewModel.CameraLeafViewModel
import com.priyankvasa.android.cameraviewex.Modes


class CameraLeafFragment : Fragment(), View.OnClickListener {

    companion object {
        private const val REQUEST_CAMERA_CODE = 100
        var flashModes: Int = 1
    }

    private var _binding: FragmentCameraLeafBinding? = null
    private val binding get() = _binding!!
    private lateinit var resultAdapter: PredictLeafAdapter
    private lateinit var cameraViewModel: CameraLeafViewModel
    private lateinit var cameraPreference: CameraPreference
    private var dataImage: ByteArray? = null
    private var startTime = 0L
    private var endTime = 0L
    private var latency = 0L
    private val list = ArrayList<Detection>()
    private var isButtonClicked = false
    private var canUseCamera = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraLeafBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity?.window?.navigationBarColor = ContextCompat.getColor(requireActivity(), R.color.white)
            activity?.window?.statusBarColor = Color.BLACK
        }

        val btnBack = binding.backBtn
        btnBack.setOnClickListener { requireActivity().onBackPressed() }

        setHasOptionsMenu(true)
        checkCameraPermission()
        setCamera()
        showRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CameraLeafFragment.REQUEST_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) canUseCamera = true
            else {
                canUseCamera = false
                Toast.makeText(activity, "Can't Use Camera", Toast.LENGTH_LONG).show()
                checkCameraPermission()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onNavigateUp(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun onNavigateUp(itemId: Int) {
        if (itemId == android.R.id.home) requireActivity().onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }


    @SuppressLint("MissingPermission")
    override fun onResume() {
        try {
            super.onResume()
            binding.camera.start()
            canUseCamera = true
        } catch (e: Exception) {
            canUseCamera = false
            Toast.makeText(activity, "Camera can't be accessed", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        binding.camera.stop()
        if (isButtonClicked) {
            isButtonClicked = false
            stateLoadingProgress(false)
        }
        super.onPause()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    @SuppressLint("MissingPermission")
    override fun onClick(v: View?) {
        when (v) {

            binding.btnDetect -> if (!isButtonClicked && canUseCamera) {
                isButtonClicked = true
                binding.camera.capture()
            } else if (isButtonClicked) {
                isButtonClicked = false
                stateLoadingProgress(false)
            }
            binding.ivFlashLightMode -> {
                CameraFragment.flashModes++
                cameraPreference.setFlashLightMode(requireContext())
                setFlashModes()
            }
        }
    }

    private fun checkCameraPermission() =
        PermissionManager.check(requireContext(),
            requireActivity(),
            Manifest.permission.CAMERA, CameraLeafFragment.REQUEST_CAMERA_CODE
        )

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setCamera() = if (!canUseCamera) checkCameraPermission()
    else {
        cameraPreference = CameraPreference()
        binding.btnDetect.setOnClickListener(this)
        binding.ivFlashLightMode.setOnClickListener(this)
        setFlashModes()
        setDetectionMode()
        loadDetectionMode()
        getViewModelLiveData()
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getViewModelLiveData() {
        binding.swDetectionMode.isChecked = cameraPreference.getAutoDetect(requireContext())
        if (binding.swDetectionMode.isChecked) {
            cameraViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(CameraLeafViewModel::class.java)
            detectPreviewFrames(requireContext())
            cameraViewModel.getDetectionPreviewFrames()
                .observe(viewLifecycleOwner, Observer { if (it != null) showDetectionItems(it) })
        } else {
            cameraViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(CameraLeafViewModel::class.java)
            detectPicture(requireContext())
            cameraViewModel.getDetectionPicture().observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    showDetectionItems(it)
                    Toast.makeText(context, "Sukses!", Toast.LENGTH_SHORT).show()
                    stateLoadingProgress(false)
                    isButtonClicked = false
                    val detectionItems = it
                    detectionItems.sortByDescending { detection -> detection.accuracy }
                }
            })
        }
    }

    private fun loadDetectionMode() {
        binding.swDetectionMode.isChecked = cameraPreference.getAutoDetect(requireContext())
        if (binding.swDetectionMode.isChecked) {
            binding.camera.disableCameraMode(Modes.CameraMode.SINGLE_CAPTURE)
            binding.camera.setCameraMode(Modes.CameraMode.CONTINUOUS_FRAME)
            binding.btnDetect.visibility = View.GONE
        } else {
            binding.camera.disableCameraMode(Modes.CameraMode.CONTINUOUS_FRAME)
            binding.camera.setCameraMode(Modes.CameraMode.SINGLE_CAPTURE)
            binding.btnDetect.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setDetectionMode() {
        binding.swDetectionMode.apply {
            isChecked = cameraPreference.getAutoDetect(requireContext())
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) cameraPreference.setAutoDetect(requireContext())
                else cameraPreference.unsetAutoDetect(requireContext())
                loadDetectionMode()
                getViewModelLiveData()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setFlashModes() {
        CameraFragment.flashModes = cameraPreference.getFlashLightMode(requireContext())
        when (CameraFragment.flashModes) {
            1 -> {
                binding.ivFlashLightMode.setImageResource(R.drawable.ic_baseline_flash_auto_24)
                binding.camera.flash = Modes.Flash.FLASH_AUTO
            }
            2 -> {
                binding.ivFlashLightMode.setImageResource(R.drawable.ic_baseline_flash_on_24)
                binding.camera.flash = Modes.Flash.FLASH_ON
            }
            3 -> {
                binding.ivFlashLightMode.setImageResource(R.drawable.ic_baseline_flash_off_24)
                binding.camera.flash = Modes.Flash.FLASH_OFF
            }
            4 -> {
                binding.ivFlashLightMode.setImageResource(R.drawable.ic_baseline_flare_24)
                binding.camera.flash = Modes.Flash.FLASH_TORCH
                CameraFragment.flashModes = 0
            }
        }
    }

    private fun showRecyclerView() {
        binding.rvDetectionResult.layoutManager = LinearLayoutManager(activity)
        resultAdapter = PredictLeafAdapter(list)
        binding.rvDetectionResult.adapter = resultAdapter
        resultAdapter.notifyDataSetChanged()
        binding.rvDetectionResult.setHasFixedSize(true)
        resultAdapter.setOnItemClickCallback(object : PredictAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: Detection) = selectedLeafDisease(data)
        })
    }

    @SuppressLint("MissingPermission")
    private fun detectPicture(context: Context) {
        binding.camera.addPictureTakenListener {
            stateLoadingProgress(true)
            startTime = SystemClock.uptimeMillis()
            cameraViewModel.setDetectionPicture(it, context)
            latency = endTime - startTime
            dataImage = it.data
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    private fun detectPreviewFrames(context: Context) {
        binding.camera.setContinuousFrameListener(10f) {
            cameraViewModel.setDetectionPreviewFrames(it, context)
        }
    }

    private fun showDetectionItems(detectionItems: ArrayList<Detection>) =
        resultAdapter.setResultDetectionData(detectionItems)

    private fun selectedLeafDisease(data: Detection) {
        val mBundle = Bundle().apply {
            putString(EXTRA_DISEASE_NAME, data.name)
            data.picture?.let { putInt(EXTRA_DISEASE_IMAGE, it) }
            data.detail?.let { putInt(EXTRA_DISEASE_DETAIL, it) }
        }
        NavHostFragment
            .findNavController(this)
            .navigate(R.id.action_cameraLeafFragment_to_guavaDiseaseDetailFragment, mBundle)
    }

    private fun stateLoadingProgress(state: Boolean) {
        if (state) {
            binding.groupProgress.visibility = View.VISIBLE
            binding.pbProcess.visibility = View.VISIBLE
            binding.tvProcess.visibility = View.VISIBLE
        } else {
            binding.groupProgress.visibility = View.GONE
            binding.pbProcess.visibility = View.GONE
            binding.tvProcess.visibility = View.GONE
        }
    }

}