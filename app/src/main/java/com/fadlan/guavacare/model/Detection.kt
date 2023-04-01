package com.fadlan.guavacare.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Detection(
    val name: String?,
    val accuracy: Float?,
//    val picture: Int?,
    val detail: Int?
) : Parcelable