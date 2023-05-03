package com.fadlan.guavacare.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class LeafGuavaDisease (
    val diseaseName: String?,
    val diseaseImage: Int?,
    val diseaseDetail: Int?
): Parcelable
