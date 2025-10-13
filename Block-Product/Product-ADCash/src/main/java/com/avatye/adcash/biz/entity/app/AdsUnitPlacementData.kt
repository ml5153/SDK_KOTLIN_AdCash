package com.avatye.adcash.biz.entity.app

data class AdsUnitPlacementData(
    val unitId: AdsPlacementUnit? = null,
    val unitValue: String,
    val imageUrl: String = "",
    val landingUrl: String = "",
    val houseUnit: AdsUnitHouseData? = null
)