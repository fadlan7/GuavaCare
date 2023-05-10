package com.fadlan.guavacare.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fadlan.guavacare.R
import com.fadlan.guavacare.databinding.FragmentGuavaDiseaseDetailBinding
import com.fadlan.guavacare.databinding.FragmentViewPagerBinding
import com.fadlan.guavacare.onboarding.screens.FirstScreenFragment
import com.fadlan.guavacare.onboarding.screens.SecondScreenFragment
import com.fadlan.guavacare.onboarding.screens.ThirdScreenFragment

class ViewPagerFragment : Fragment() {

    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)


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

        binding.viewPager.adapter = adapter

        return binding.root
    }

}