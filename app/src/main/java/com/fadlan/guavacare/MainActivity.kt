package com.fadlan.guavacare

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fadlan.guavacare.adapter.PredictAdapter
import com.fadlan.guavacare.model.Detection
import com.fadlan.guavacare.preference.CameraPreference
import com.fadlan.guavacare.viewModel.CameraViewModel
import com.priyankvasa.android.cameraviewex.Modes
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        val detectionHistoryHelper = DetectionHistoryHelper.getInstance(applicationContext)
//        if (detectionHistoryHelper.open().equals(true)) detectionHistoryHelper.close()
//    }
}