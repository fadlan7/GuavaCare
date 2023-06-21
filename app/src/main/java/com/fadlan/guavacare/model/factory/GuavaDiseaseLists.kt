package com.fadlan.guavacare.model.factory

import android.content.res.Resources
import android.content.res.TypedArray
import com.fadlan.guavacare.R
import com.fadlan.guavacare.model.GuavaDisease

object GuavaDiseaseLists {
    var guavaDiseases = ArrayList<GuavaDisease>()
    private lateinit var diseaseImage: TypedArray
    private lateinit var diseaseName: Array<String>
    private lateinit var diseaseDetail: TypedArray
    private lateinit var diseaseSubName: Array<String>

    fun addDisease(resources: Resources): ArrayList<GuavaDisease>{
        prepare(resources)
        for (position in diseaseName.indices){
            val guavaDisease = GuavaDisease(
                diseaseName[position],
                diseaseImage.getResourceId(position, -1),
                diseaseDetail.getResourceId(position, -1),
                diseaseSubName[position]
            )
            guavaDiseases.add(guavaDisease)
        }
        return guavaDiseases
    }

    private fun prepare(resources: Resources){
        diseaseName = resources.getStringArray(R.array.guavaDiseaseName)
        diseaseImage = resources.obtainTypedArray(R.array.guavaDiseaseImage)
        diseaseDetail = resources.obtainTypedArray(R.array.detail_guava_disease_layout)
        diseaseSubName = resources.getStringArray(R.array.guavaDiseaseNameIndo)
    }
}