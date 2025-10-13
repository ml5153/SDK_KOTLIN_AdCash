package com.avatye.adcash.platform.provider.adhouse

import com.avatye.adcash.platform.library.printout.PrintOut

internal object Settings {

    const val adsviserName = "AHSSP"
    private const val sourceName = "AhsspSettings"

    // logger
    val printout by lazy { PrintOut(moduleName = "ADCASH:Platform:SSP:AdHouse") }

    const val RefreshTime = -1
    const val NetworkScheduleTimeout = 8
    const val VideoNetworkScheduleTimeout = 8
}