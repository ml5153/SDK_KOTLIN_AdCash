package com.avatye.adcash.platform.provider.basement.nativeviewbinder

import com.avatye.adcash.platform.library.printout.PrintOut

internal object Settings {
    // logger
    val printout by lazy {
        PrintOut(moduleName = "ADCash:Mediation:Base")
    }
}