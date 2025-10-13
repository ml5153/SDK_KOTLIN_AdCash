package com.example.xview_adcash_qa.data

import com.google.gson.annotations.SerializedName

enum class AdType {
    BANNER_50,
    BANNER_100,
    BANNER_250,
    DYNAMIC,
    INTERSTITIAL
}

data class Advertisement(
    @SerializedName("id")
    val id: Int,

    @SerializedName("pid")
    val pid: String,

    @SerializedName("type")
    val type: AdType
)