package com.fadlan.guavacare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
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

        }
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    private fun setActionBar() {
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowCustomEnabled(false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Guava Care"
    }

}