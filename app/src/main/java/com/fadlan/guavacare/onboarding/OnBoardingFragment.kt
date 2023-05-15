package com.fadlan.guavacare.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.fadlan.guavacare.R
import com.fadlan.guavacare.onboarding.screens.FirstScreenFragment
import com.fadlan.guavacare.onboarding.screens.SecondScreenFragment
import com.fadlan.guavacare.onboarding.screens.ThirdScreenFragment
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class OnBoardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)

        val fragmentList = arrayListOf<Fragment>(
            FirstScreenFragment(),
            SecondScreenFragment(),
            ThirdScreenFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter

        val indicator = view.findViewById<DotsIndicator>(R.id.dots_indicator)
        indicator.attachTo(viewPager)

        return view
    }

}