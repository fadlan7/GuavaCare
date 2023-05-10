package com.fadlan.guavacare.onboarding.screens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.fadlan.guavacare.R
import com.fadlan.guavacare.databinding.FragmentFirstScreenBinding
import com.fadlan.guavacare.databinding.FragmentSecondScreenBinding
import com.fadlan.guavacare.databinding.FragmentThirdScreenBinding

class ThirdScreenFragment : Fragment() {

    private var _binding: FragmentThirdScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentThirdScreenBinding.inflate(inflater, container, false)

        binding.finish.setOnClickListener{
            findNavController().navigate(R.id.action_viewPagerFragment_to_homeFragment)
            onBoardingFinished()
        }

        return binding.root
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()

//        val context = activity
//        val sharedPref = context?.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
//        val editor = sharedPref?.edit()
//        editor?.putBoolean("Finished", true)
//        editor?.apply()
    }

}