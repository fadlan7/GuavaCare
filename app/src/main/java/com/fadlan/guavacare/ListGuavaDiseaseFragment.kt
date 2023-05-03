package com.fadlan.guavacare

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.fadlan.guavacare.GuavaDiseaseDetailFragment.Companion.EXTRA_DISEASE_DETAIL
import com.fadlan.guavacare.GuavaDiseaseDetailFragment.Companion.EXTRA_DISEASE_IMAGE
import com.fadlan.guavacare.GuavaDiseaseDetailFragment.Companion.EXTRA_DISEASE_NAME
import com.fadlan.guavacare.adapter.GuavaDiseaseAdapter
import com.fadlan.guavacare.adapter.LeafGuavaDiseaseAdapter
import com.fadlan.guavacare.model.factory.GuavaDiseaseLists.addDisease
import com.fadlan.guavacare.databinding.FragmentListGuavaDiseaseBinding
import com.fadlan.guavacare.model.GuavaDisease
import com.fadlan.guavacare.model.LeafGuavaDisease
import com.fadlan.guavacare.model.factory.GuavaDiseaseLists.guavaDiseases
import com.fadlan.guavacare.model.factory.LeafGuavaDiseaseLists.addLeafDisease
import com.fadlan.guavacare.model.factory.LeafGuavaDiseaseLists.leafGuavaDiseases

class ListGuavaDiseaseFragment : Fragment() {

    private var _binding: FragmentListGuavaDiseaseBinding? = null
    private val binding get() = _binding!!
    private var guavaDiseaseAdapter = GuavaDiseaseAdapter(guavaDiseases)
    private var leafGuavaDiseaseAdapter = LeafGuavaDiseaseAdapter(leafGuavaDiseases)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListGuavaDiseaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionBar()
        setHasOptionsMenu(true)
        addDisease(resources)
        addLeafDisease(resources)
        recyclerView()
        leafRecyclerView()
    }

    fun recyclerView(){
        binding.apply {
            rvListDiseases.layoutManager = GridLayoutManager(activity, 2)

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
            rvListLeafDiseases.layoutManager = GridLayoutManager(activity, 2)

            leafGuavaDiseaseAdapter= LeafGuavaDiseaseAdapter(leafGuavaDiseases)
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

    override fun onStop() {
        super.onStop()
        addDisease(resources).removeAll(guavaDiseases.toSet())
        addLeafDisease(resources).removeAll(leafGuavaDiseases.toSet())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onNavigateUp(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun onNavigateUp(itemId: Int) {
        if (itemId == android.R.id.home) requireActivity().onBackPressed()
    }

    private fun selectedGuavaDisease(data:GuavaDisease){
        val bundle = Bundle().apply {
            putString(EXTRA_DISEASE_NAME, data.diseaseName)
            data.diseaseImage?.let { putInt(EXTRA_DISEASE_IMAGE, it) }
            data.diseaseDetail?.let { putInt(EXTRA_DISEASE_DETAIL, it) }
        }
        NavHostFragment.findNavController(this).navigate(R.id.action_listGuavaDiseaseFragment_to_guavaDiseaseDetailFragment, bundle)
    }

    private fun selectedLeafGuavaDisease(data:LeafGuavaDisease){
        val bundle = Bundle().apply {
            putString(EXTRA_DISEASE_NAME, data.diseaseName)
            data.diseaseImage?.let { putInt(EXTRA_DISEASE_IMAGE, it) }
            data.diseaseDetail?.let { putInt(EXTRA_DISEASE_DETAIL, it) }
        }
        NavHostFragment.findNavController(this).navigate(R.id.action_listGuavaDiseaseFragment_to_guavaDiseaseDetailFragment, bundle)
    }

    private fun actionBar() {
        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = "Daftar Penyakit Jambu"
        }
    }
}