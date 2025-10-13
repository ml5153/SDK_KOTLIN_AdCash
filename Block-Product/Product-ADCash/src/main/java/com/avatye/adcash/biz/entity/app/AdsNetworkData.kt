package com.avatye.adcash.biz.entity.app

data class AdsNetworkData(
    val keyName: String,
    val keyValue: String,
    val networkName: AdsNetworkName? = null,
    val isHouseUnit: Boolean,
    val placements: MutableList<AdsUnitPlacementData> = mutableListOf(),
)