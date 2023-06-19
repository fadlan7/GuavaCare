package com.fadlan.guavacare.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class GuavaDisease (
    val diseaseName: String?,
    val diseaseImage: Int?,
    val diseaseDetail: Int?,
    val diseaseSubName: String?,
): Parcelable
