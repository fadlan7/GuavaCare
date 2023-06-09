package com.fadlan.guavacare.onboarding.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.fadlan.guavacare.R
import com.google.android.material.button.MaterialButton

class FirstScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_first_screen, container, false)

        val next = view.findViewById<TextView>(R.id.next)
        val skip = view.findViewById<MaterialButton>(R.id.skip_button)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        next.setOnClickListener {
            viewPager?.currentItem = 1
        }

        skip.setOnClickListener{
            findNavController().navigate(R.id.action_onboardingFragment_to_homeFragment)
            onBoardingIsFinished()
        }
        return view
    }


    private fun onBoardingIsFinished(){
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("finished",true)
        editor.apply()
    }
}