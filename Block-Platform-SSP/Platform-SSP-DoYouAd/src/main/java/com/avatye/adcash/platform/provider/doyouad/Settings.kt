package com.avatye.adcash.platform.provider.doyouad

import com.avatye.adcash.platform.library.printout.PrintOut

internal object Settings {


    const val adsviserName = "DoYouAd"
    private const val sourceName = "DoYouAdSettings"
    
    // extra config
    val doyouad_use_interval = true



    // logger
    val printout by lazy { PrintOut(moduleName = "ADCASH:Platform:SSP:${adsviserName}") }

}