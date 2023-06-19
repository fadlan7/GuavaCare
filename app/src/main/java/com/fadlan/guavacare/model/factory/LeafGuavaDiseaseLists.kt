package com.fadlan.guavacare.model.factory

import android.content.res.Resources
import android.content.res.TypedArray
import com.fadlan.guavacare.R
import com.fadlan.guavacare.model.GuavaDisease
import com.fadlan.guavacare.model.LeafGuavaDisease

object LeafGuavaDiseaseLists {
    var leafGuavaDiseases = ArrayList<LeafGuavaDisease>()
    private lateinit var diseaseImage: TypedArray
    private lateinit var diseaseName: Array<String>
    private lateinit var diseaseDetail: TypedArray
    private lateinit var diseaseSubName: Array<String>

    fun addLeafDisease(resources: Resources): ArrayList<LeafGuavaDisease>{
        prepare(resources)
        for (position in diseaseName.indices){
            val guavaDisease = LeafGuavaDisease(
                diseaseName[position],
                diseaseImage.getResourceId(position, -1),
                diseaseDetail.getResourceId(position, -1),
                diseaseSubName[position]
            )
            leafGuavaDiseases.add(guavaDisease)
        }
        return leafGuavaDiseases
    }

    private fun prepare(resources: Resources){
        diseaseName = resources.getStringArray(R.array.leafDiseaseName)
        diseaseImage = resources.obtainTypedArray(R.array.leafDiseaseImage)
        diseaseDetail = resources.obtainTypedArray(R.array.detail_leaf_disease_layout)
        diseaseSubName = resources.getStringArray(R.array.leafDiseaseNameIndo)
    }
}