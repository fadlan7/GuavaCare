package com.fadlan.guavacare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.fadlan.guavacare.GuavaDiseaseDetailFragment.Companion.EXTRA_DISEASE_DETAIL
import com.fadlan.guavacare.GuavaDiseaseDetailFragment.Companion.EXTRA_DISEASE_IMAGE
import com.fadlan.guavacare.GuavaDiseaseDetailFragment.Companion.EXTRA_DISEASE_NAME
import com.fadlan.guavacare.adapter.GuavaDiseaseAdapter
import com.fadlan.guavacare.adapter.LeafGuavaDiseaseAdapter
import com.fadlan.guavacare.databinding.FragmentHomeBinding
import com.fadlan.guavacare.model.GuavaDisease
import com.fadlan.guavacare.model.LeafGuavaDisease
import com.fadlan.guavacare.model.factory.GuavaDiseaseLists.addDisease
import com.fadlan.guavacare.model.factory.GuavaDiseaseLists.guavaDiseases
import com.fadlan.guavacare.model.factory.LeafGuavaDiseaseLists
import com.fadlan.guavacare.model.factory.LeafGuavaDiseaseLists.addLeafDisease
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var guavaDiseaseAdapter = GuavaDiseaseAdapter(guavaDiseases)
    private var leafGuavaDiseaseAdapter = LeafGuavaDiseaseAdapter(LeafGuavaDiseaseLists.leafGuavaDiseases)

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
        addDisease(resources)
        addLeafDisease(resources)
        recyclerView()
        leafRecyclerView()

        binding.btnDeteksi.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_cameraFragment)
        )

        binding.btnDeteksiDaun.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_cameraLeafFragment)
        )

        binding.cekDaunButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_cameraLeafFragment)
        )

        binding.cekJambuButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_cameraFragment)
        )

//        val sectionsPagerAdapter = SectionsPagerAdapter(this)
//        val viewPager: ViewPager2 = binding.viewPager
//        viewPager.adapter = sectionsPagerAdapter
//        val tabs: TabLayout = binding.tabs
//        TabLayoutMediator(tabs, viewPager) { tab, position ->
//            tab.text = resources.getString(TAB_TITLES[position])
//        }.attach()
    }


    override fun onClick(v: View?) {
        when(v){
            binding.btnDeteksi -> NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_homeFragment_to_cameraFragment)

            binding.btnDeteksiDaun -> NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_homeFragment_to_cameraLeafFragment)

            binding.cekJambuButton -> NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_homeFragment_to_cameraFragment)

            binding.cekDaunButton -> NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_homeFragment_to_cameraLeafFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    fun recyclerView(){
        binding.apply {
            rvListDiseases.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL ,false)


            guavaDiseaseAdapter= GuavaDiseaseAdapter(guavaDiseases)
            rvListDiseases.adapter=guavaDiseaseAdapter

            guavaDiseaseAdapter.notifyDataSetChanged()
            rvListDiseases.setHasFixedSize(true)

            guavaDiseaseAdapter.setOnItemClickCallback(object :
                GuavaDiseaseAdapter.OnItemClickCallBack{
                override fun onItemClicked(data: GuavaDisease) {
                    selectedGuavaDisease(data)
                }
                }
            )
        }
    }

    private fun leafRecyclerView(){
        binding.apply {
            rvListLeafDiseases.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL ,false)

            leafGuavaDiseaseAdapter= LeafGuavaDiseaseAdapter(LeafGuavaDiseaseLists.leafGuavaDiseases)
            rvListLeafDiseases.adapter=leafGuavaDiseaseAdapter

            leafGuavaDiseaseAdapter.notifyDataSetChanged()
            rvListLeafDiseases.setHasFixedSize(true)

            leafGuavaDiseaseAdapter.setOnItemClickCallback(object :
                LeafGuavaDiseaseAdapter.OnItemClickCallBack{
                override fun onItemClicked(data: LeafGuavaDisease) {
                    selectedLeafGuavaDisease(data)
                }
            }
            )
        }
    }

    private fun selectedGuavaDisease(data:GuavaDisease){
        val bundle = Bundle().apply {
            putString(EXTRA_DISEASE_NAME, data.diseaseName)
            data.diseaseImage?.let { putInt(EXTRA_DISEASE_IMAGE, it) }
            data.diseaseDetail?.let { putInt(EXTRA_DISEASE_DETAIL, it) }
        }
        NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_guavaDiseaseDetailFragment, bundle)
    }

    private fun selectedLeafGuavaDisease(data: LeafGuavaDisease){
        val bundle = Bundle().apply {
            putString(EXTRA_DISEASE_NAME, data.diseaseName)
            data.diseaseImage?.let { putInt(EXTRA_DISEASE_IMAGE, it) }
            data.diseaseDetail?.let { putInt(EXTRA_DISEASE_DETAIL, it) }
        }
        NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_guavaDiseaseDetailFragment, bundle)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        addDisease(resources).removeAll(guavaDiseases.toSet())
        addLeafDisease(resources)
            .removeAll(LeafGuavaDiseaseLists.leafGuavaDiseases.toSet())
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }
}