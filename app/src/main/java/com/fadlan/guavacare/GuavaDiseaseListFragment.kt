package com.fadlan.guavacare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.fadlan.guavacare.databinding.FragmentGuavaDiseaseListBinding
import com.fadlan.guavacare.databinding.FragmentHomeBinding
import com.fadlan.guavacare.model.factory.GuavaDiseaseLists
import com.fadlan.guavacare.GuavaDiseaseDetailFragment.Companion.EXTRA_DISEASE_DETAIL
import com.fadlan.guavacare.GuavaDiseaseDetailFragment.Companion.EXTRA_DISEASE_IMAGE
import com.fadlan.guavacare.GuavaDiseaseDetailFragment.Companion.EXTRA_DISEASE_NAME
import com.fadlan.guavacare.adapter.GuavaDiseaseAdapter

import com.fadlan.guavacare.model.factory.GuavaDiseaseLists.addDisease
import com.fadlan.guavacare.model.GuavaDisease
import com.fadlan.guavacare.model.LeafGuavaDisease
import com.fadlan.guavacare.model.factory.GuavaDiseaseLists.guavaDiseases
import com.fadlan.guavacare.model.factory.LeafGuavaDiseaseLists.addLeafDisease
import com.fadlan.guavacare.model.factory.LeafGuavaDiseaseLists.leafGuavaDiseases

class GuavaDiseaseListFragment : Fragment() {

    private var _binding: FragmentGuavaDiseaseListBinding? = null
    private val binding get() = _binding!!
    private var guavaDiseaseAdapter = GuavaDiseaseAdapter(guavaDiseases)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGuavaDiseaseListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        addDisease(resources)
        recyclerView()

    }

    fun recyclerView(){
        binding.apply {
            rvListDiseases.layoutManager = GridLayoutManager(activity, 1)


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

    private fun selectedGuavaDisease(data: GuavaDisease){
        val bundle = Bundle().apply {
            putString(EXTRA_DISEASE_NAME, data.diseaseName)
            data.diseaseImage?.let { putInt(EXTRA_DISEASE_IMAGE, it) }
            data.diseaseDetail?.let { putInt(EXTRA_DISEASE_DETAIL, it) }
        }
        NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_guavaDiseaseDetailFragment, bundle)
    }

    override fun onStop() {
        super.onStop()
        addDisease(resources).removeAll(guavaDiseases.toSet())
        addLeafDisease(resources).removeAll(leafGuavaDiseases.toSet())
    }
}