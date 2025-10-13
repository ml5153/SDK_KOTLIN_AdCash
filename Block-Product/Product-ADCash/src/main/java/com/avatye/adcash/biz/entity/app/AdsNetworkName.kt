package com.avatye.adcash.biz.entity.app

enum class AdsNetworkName(val value: String) {
    IGAWORKS("igaworks"),
    MEZZOMEDIA("mezzomedia"),
    DOYOUAD("doyouad"),
    HOUSE("avatye");

    companion object {
        fun from(value: String): AdsNetworkName? {
            return AdsNetworkName.values().find {
                it.value.equals(other = value, ignoreCase = true)
            }
        }
    }
}
