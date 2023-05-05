package com.fadlan.guavacare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fadlan.guavacare.databinding.FragmentGuavaDetailCustomBinding
import com.fadlan.guavacare.databinding.FragmentGuavaDiseaseDetailBinding

class GuavaDetailFragmentCustom : Fragment() {
    private var _binding: FragmentGuavaDetailCustomBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGuavaDetailCustomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        actionBar()
        selectedGuavaDisease()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onNavigateUp(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun onNavigateUp(itemId: Int) {
        if (itemId == android.R.id.home) requireActivity().onBackPressed()
    }

    private fun selectedGuavaDisease(){
        if (arguments != null){
            val diseaseName = arguments?.getString(EXTRA_DISEASE_NAME).toString()
            val diseaseImage = arguments?.getInt(EXTRA_DISEASE_IMAGE, 0)
            val diseaseDetail = arguments?.getInt(EXTRA_DISEASE_DETAIL, 0)

//            Glide.with(this).load(diseaseImage).into(binding.rivPictureReceived)
//            binding.tvNameReceived.text = diseaseName
//            if (diseaseDetail != null){
//                binding.apply {
//                    vsDetailReceived.layoutResource = diseaseDetail
//                    vsDetailReceived.inflate()
//                }
//            }
        }
    }

    private fun actionBar() {
        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = arguments?.getString(EXTRA_DISEASE_NAME).toString()
        }
    }

    companion object {
        const val EXTRA_DISEASE_NAME = "extra_disease_name"
        const val EXTRA_DISEASE_IMAGE = "extra_disease_image"
        const val EXTRA_DISEASE_DETAIL = "extra_disease_detail"
    }
}