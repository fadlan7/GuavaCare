package com.fadlan.guavacare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.fadlan.guavacare.databinding.FragmentGuavaDiseaseDetailBinding
import com.fadlan.guavacare.model.GuavaDisease

class GuavaDiseaseDetailFragment : Fragment() {

    private var _binding: FragmentGuavaDiseaseDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGuavaDiseaseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        actionBar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onNavigateUp(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun onNavigateUp(itemId: Int) {
        if (itemId == android.R.id.home) requireActivity().onBackPressed()
    }

    private fun selectedGuavaDisease(data: GuavaDisease){
        if (arguments != null){
            val diseaseName = arguments?.getString(EXTRA_DISEASE_NAME).toString()
            val diseaseImage = arguments?.getInt(EXTRA_DISEASE_IMAGE, 0)
            val diseaseDetail = arguments?.getInt(EXTRA_DISEASE_DETAIL, 0)

            Glide.with(this).load(diseaseImage).into(binding.rivPictureReceived)
            binding.tvNameReceived.text = diseaseName
            if (diseaseDetail != null){
                binding.apply {
                    vsDetailReceived.layoutResource = diseaseDetail
                    vsDetailReceived.inflate()
                }
            }
        }
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
            title = EXTRA_DISEASE_NAME
        }
    }

    companion object {
        const val EXTRA_DISEASE_NAME = "extra_disease_name"
        const val EXTRA_DISEASE_IMAGE = "extra_disease_image"
        const val EXTRA_DISEASE_DETAIL = "extra_disease_detail"
    }
}