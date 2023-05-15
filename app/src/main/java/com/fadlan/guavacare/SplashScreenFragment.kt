package com.fadlan.guavacare

import android.annotation.SuppressLint
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


@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fun onBoardingIsFinished(): Boolean{
            val sharedPreferences = context.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("finished",false)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (onBoardingIsFinished()){
                lifecycleScope.launchWhenResumed {
                    findNavController().navigate(R.id.navigate_splashScreenFragment_to_homeFragment)
                }
            }else{
                lifecycleScope.launchWhenResumed {
                    findNavController().navigate(R.id.action_splashScreenFragment_to_onboardingFragment)
                }
            }

        }, 2000)
    }
}