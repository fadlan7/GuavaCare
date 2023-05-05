package com.fadlan.guavacare

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.fadlan.guavacare.databinding.FragmentCameraBinding
import com.fadlan.guavacare.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        setActionBar()

        binding.btnDeteksi.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_cameraFragment)
        )

        binding.btnDeteksiDaun.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_cameraLeafFragment)
        )

        binding.btnJenisPenyakit.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_listGuavaDiseaseFragment)
        )

        binding.btnJenisCustom.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_guava_List_Custom)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when(v){

            binding.btnDeteksi -> NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_homeFragment_to_cameraFragment)

            binding.btnDeteksiDaun -> NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_homeFragment_to_cameraLeafFragment)

            binding.btnJenisPenyakit -> NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_homeFragment_to_listGuavaDiseaseFragment)

            binding.btnJenisCustom -> NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_homeFragment_to_guava_List_Custom)
        }
    }

    private fun setActionBar() {
//        val drawable = ResourcesCompat.getDrawable(resources,
//            R.drawable.ic_launcher_foreground, null)
//        val bitmap = drawable?.toBitmap()
//        val scaledDrawable = BitmapDrawable(resources,
//            bitmap?.let { Bitmap.createScaledBitmap(it, 60, 60, true) })
//        (activity as AppCompatActivity?)?.supportActionBar?.setHomeAsUpIndicator(false)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Guava Care"
    }
}