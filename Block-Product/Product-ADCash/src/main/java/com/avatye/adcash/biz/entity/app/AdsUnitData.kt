package com.avatye.adcash.biz.entity.app

import com.avatye.adcash.ADCashSettings.DEFAULT_LOADER_TIMEOUT

data class AdsUnitData(
    val placementId: String = "",
    val placementName: String = "",
    val placementUnit: String = "",
    val timeout: Long = DEFAULT_LOADER_TIMEOUT,
    val networks: MutableList<AdsNetworkData> = mutableListOf(),
)