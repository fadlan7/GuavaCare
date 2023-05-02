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
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fadlan.guavacare.adapter.PredictAdapter
import com.fadlan.guavacare.adapter.PredictLeafAdapter
import com.fadlan.guavacare.databinding.FragmentCameraBinding
import com.fadlan.guavacare.databinding.FragmentCameraLeafBinding
import com.fadlan.guavacare.manager.PermissionManager
import com.fadlan.guavacare.model.Detection
import com.fadlan.guavacare.preference.CameraPreference
import com.fadlan.guavacare.viewModel.CameraLeafViewModel
import com.fadlan.guavacare.viewModel.CameraViewModel
import com.priyankvasa.android.cameraviewex.Modes


class CameraLeafFragment : Fragment(), View.OnClickListener {

    companion object {
        private const val REQUEST_CAMERA_CODE = 100
        private const val REQUEST_FINE_LOCATION_CODE = 110
        private const val REQUEST_COARSE_LOCATION_CODE = 120
        var flashModes: Int = 1
    }

    private var _binding: FragmentCameraLeafBinding? = null
    private val binding get() = _binding!!
    //    private lateinit var resultAdapter: ResultAdapter
    private lateinit var resultAdapter: PredictLeafAdapter
    private lateinit var cameraViewModel: CameraLeafViewModel
    //    private lateinit var mLocationManager: com.android.farmdoctor.manager.LocationManager
    private lateinit var cameraPreference: CameraPreference
    //    private lateinit var detectionHistoryHelper: DetectionHistoryHelper
    private var dataImage: ByteArray? = null
    private var startTime = 0L
    private var endTime = 0L
    private var latency = 0L
    //    private var latitude: String? = null
//    private var longitude: String? = null
    private val list = ArrayList<Detection>()
    private var isButtonClicked = false
    private var canUseCamera = true
//    private var canAccessFineLocation = true
//    private var canAccessCoarseLocation = true
//    private var isDetectionHistoryEnabled = false

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

////        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
////        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity?.window?.navigationBarColor = ContextCompat.getColor(requireActivity(), R.color.white)
            activity?.window?.statusBarColor = Color.BLACK
        }

//        activity?.window?.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.black)

        val btnBack = binding.backBtn

        btnBack.setOnClickListener { requireActivity().onBackPressed() }

//        setActionBar()
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
//        if (requestCode == REQUEST_FINE_LOCATION_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) canAccessFineLocation = true
//            else {
//                canAccessFineLocation = false
//                Toast.makeText(activity, "Can't Access Fine Location", Toast.LENGTH_LONG).show()
//                checkLocationPermissions()
//            }
//        }
//        if (requestCode == REQUEST_COARSE_LOCATION_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) canAccessCoarseLocation = true
//            else {
//                canAccessCoarseLocation = false
//                Toast.makeText(activity, "Can't Access Coarse Location", Toast.LENGTH_LONG).show()
//                checkLocationPermissions()
//            }
//        }
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
//            cb_enable_history -> {
//                if (cb_enable_history.isChecked) {
//                    isDetectionHistoryEnabled = true
//                    cameraPreference.setDetectionHistory(requireContext())
//                }
//                else {
//                    isDetectionHistoryEnabled = false
//                    cameraPreference.unsetDetectionHistory(requireContext())
//                }
//                enableDetectionHistory()
//            }
        }
    }

    private fun setActionBar() {
//        (activity as AppCompatActivity?)?.supportActionBar?.hide()
        val drawable = ResourcesCompat.getDrawable(resources,
            R.drawable.ic_baseline_close_24, null)
        val bitmap = drawable?.toBitmap()
        val scaledDrawable = BitmapDrawable(resources,
            bitmap?.let { Bitmap.createScaledBitmap(it, 100, 100, true) })
        (activity as AppCompatActivity?)?.supportActionBar?.setHomeAsUpIndicator(scaledDrawable)
        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = "Diagnose Plant Diseases"
        }
    }

    private fun checkCameraPermission() =
        PermissionManager.check(requireContext(),
            requireActivity(),
            Manifest.permission.CAMERA, CameraLeafFragment.REQUEST_CAMERA_CODE
        )

//    private fun checkLocationPermissions() {
//        PermissionManager.check(requireContext(),
//            requireActivity(),
//            Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION_CODE)
//        PermissionManager.check(requireContext(),
//            requireActivity(),
//            Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_COARSE_LOCATION_CODE)
//    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setCamera() = if (!canUseCamera) checkCameraPermission()
    else {
        cameraPreference = CameraPreference()
        binding.btnDetect.setOnClickListener(this)
        binding.ivFlashLightMode.setOnClickListener(this)
//        cb_enable_history.setOnClickListener(this)
        setFlashModes()
        setDetectionMode()
        loadDetectionMode()
//        enableLocation()
//        loadLocationPrefs()
//        enableDetectionHistory()
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
//            detectionHistoryHelper = DetectionHistoryHelper.getInstance(requireContext())
//            detectionHistoryHelper.open()
            cameraViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(CameraLeafViewModel::class.java)
            detectPicture(requireContext())
            cameraViewModel.getDetectionPicture().observe(viewLifecycleOwner, Observer {
                if (it != null) {
//                    endTime = SystemClock.uptimeMillis()
//                    latency = endTime - startTime
                    showDetectionItems(it)
                    Toast.makeText(context, "Sukses!", Toast.LENGTH_SHORT).show()
                    stateLoadingProgress(false)
                    isButtonClicked = false
                    val detectionItems = it
                    detectionItems.sortByDescending { detection -> detection.accuracy }
//                    val checkAcc = detectionHistoryHelper.checkAccuracy(detectionItems[0].accuracy.toString())
//                    //val checkName = detectionHistoryHelper.checkName(detectionItems[0].name.toString())
//                    if (isDetectionHistoryEnabled && !checkAcc) addDetectionHistory()
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
//            binding.group2.visibility = View.GONE
//            tv_enable_location.visibility = View.GONE
//            sw_enable_location.visibility = View.GONE
//            tv_enable_history.visibility = View.GONE
//            cb_enable_history.visibility = View.GONE
        } else {
            binding.camera.disableCameraMode(Modes.CameraMode.CONTINUOUS_FRAME)
            binding.camera.setCameraMode(Modes.CameraMode.SINGLE_CAPTURE)
            binding.btnDetect.visibility = View.VISIBLE
//            binding.group2.visibility = View.VISIBLE
//            tv_enable_location.visibility = View.VISIBLE
//            sw_enable_location.visibility = View.VISIBLE
//            tv_enable_history.visibility = View.VISIBLE
//            cb_enable_history.visibility = View.VISIBLE
        }
    }

//    private fun loadLocationPrefs() {
//        sw_enable_location.isChecked = cameraPreference.getLocation(requireContext())
//        if (sw_enable_location.isChecked) {
//            locationManager =
//                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            val fineLocationEnabled =
//                locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
//            val coarseLocationEnabled =
//                locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//            if (fineLocationEnabled == true || coarseLocationEnabled == true) {
//                mLocationManager = com.android.farmdoctor.manager.LocationManager()
//                checkLocationPermissions()
//                tv_process.text = "Getting Location..."
//                stateLoadingProgress(true)
//                if (canAccessFineLocation || canAccessCoarseLocation) {
//                    GlobalScope.launch(Dispatchers.Default) {
//                        mLocationManager.getLastLocation(requireContext())
//                        delay(1000L)
//                        latitude = latitudeData
//                        longitude = longitudeData
//                        withContext(Dispatchers.Main) {
//                            tv_process.text = "Processing..."
//                            stateLoadingProgress(false)
//                        }
//                    }
//                } else Toast.makeText(activity, "Can't Access Location", Toast.LENGTH_LONG).show()
//            } else {
//                requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//                if (locationManager?.isProviderEnabled(LocationManager.MODE_CHANGED_ACTION) == true)
//                    loadLocationPrefs()
//            }
//        } else {
//            latitude = "Location disabled"
//            longitude = "Location disabled"
//        }
//    }

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

//    private fun enableLocation() {
//        if (!sw_detection_mode.isChecked) {
//            sw_enable_location.apply {
//                cameraPreference.getLocation(requireContext())
//                setOnCheckedChangeListener { _, isChecked ->
//                    if (isChecked) {
//                        checkLocationPermissions()
//                        cameraPreference.setLocation(requireContext())
//                    } else cameraPreference.unsetLocation(requireContext())
//                    loadLocationPrefs()
//                }
//            }
//        }
//    }

//    private fun enableDetectionHistory() {
//        if (!sw_detection_mode.isChecked) {
//            cb_enable_history.isChecked = cameraPreference.getDetectionHistory(requireContext())
//            isDetectionHistoryEnabled = cb_enable_history.isChecked
//        }
//    }

    private fun showRecyclerView() {
        binding.rvDetectionResult.layoutManager = LinearLayoutManager(activity)
        resultAdapter = PredictLeafAdapter(list)
        binding.rvDetectionResult.adapter = resultAdapter
        resultAdapter.notifyDataSetChanged()
        binding.rvDetectionResult.setHasFixedSize(true)
//        resultAdapter.setOnItemClickCallback(object : PredictAdapter.OnItemClickCallBack {
//            override fun onItemClicked(data: Detection) = setSelectedPlantDisease(data)
//        })
    }

    @SuppressLint("MissingPermission")
    private fun detectPicture(context: Context) {
        binding.camera.addPictureTakenListener {
            stateLoadingProgress(true)
            startTime = SystemClock.uptimeMillis()
            cameraViewModel.setDetectionPicture(it, context)
            latency = endTime - startTime
            dataImage = it.data
            //val bitmap = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.size)
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

//    private fun setSelectedPlantDisease(data: Detection) {
//        val mBundle = Bundle().apply {
//            putString(EXTRA_NAME, data.name)
//            data.picture?.let { putInt(EXTRA_PICTURE, it) }
//            data.detail?.let { putInt(EXTRA_DETAIL, it) }
//        }
//        NavHostFragment
//            .findNavController(this)
//            .navigate(R.id.action_cameraFragment_to_listDiseasesDetailFragment, mBundle)
//    }

//    private fun addDetectionHistory() {
//        val items = cameraViewModel.getDetectionPicture().value
//        items?.sortByDescending { it.accuracy }
//        val values = ContentValues().apply {
//            put(COLUMN_IMAGE, dataImage)
//            put(COLUMN_NAME, items?.get(0)?.name)
//            put(COLUMN_ACCURACY, items?.get(0)?.accuracy.toString())
//            put(COLUMN_DATE, getCurrentDate())
//            put(COLUMN_LATENCY, latency.toString())
//            put(COLUMN_LATITUDE, latitude.toString())
//            put(COLUMN_LONGITUDE, longitude.toString())
//            put(COLUMN_DETECTION_BASED, "Camera")
//        }
//        detectionHistoryHelper = DetectionHistoryHelper.getInstance(requireContext())
//        detectionHistoryHelper.insert(values)
//    }

//    private fun getCurrentDate(): String {
//        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
//        val date = Date()
//        return dateFormat.format(date)
//    }

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