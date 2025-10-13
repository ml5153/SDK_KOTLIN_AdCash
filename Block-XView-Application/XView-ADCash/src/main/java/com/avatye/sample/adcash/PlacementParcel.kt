package com.avatye.sample.adcash

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlacementParcel(val pid: String) : Parcelable {
    companion object {
        const val NAME = "parcel:adcash:pid"
    }
}