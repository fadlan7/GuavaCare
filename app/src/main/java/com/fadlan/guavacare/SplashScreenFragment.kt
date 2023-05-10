package com.fadlan.guavacare

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fadlan.guavacare.databinding.FragmentFirstScreenBinding
import com.fadlan.guavacare.databinding.FragmentSplashScreenBinding


class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()


        Handler(Looper.getMainLooper()).postDelayed({
//            if(onBoardingFinished()){
//                lifecycleScope.launchWhenResumed {
//                    findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
//                }
//            }else{
//                lifecycleScope.launchWhenResumed {
//                    findNavController().navigate(R.id.action_splashScreenFragment_to_viewPagerFragment)
//                }
//            }
            lifecycleScope.launchWhenResumed {
                    findNavController().navigate(R.id.action_splashScreenFragment_to_viewPagerFragment)
            }
        }, 3000)

       return binding.root
    }

    private fun onBoardingFinished(): Boolean{
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)

//        val context = activity
//        val sharedPref = context!!.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
//        return sharedPref.getBoolean("Finished", false)
    }

}