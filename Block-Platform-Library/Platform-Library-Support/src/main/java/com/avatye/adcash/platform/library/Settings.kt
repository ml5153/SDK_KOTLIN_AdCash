package com.avatye.adcash.platform.library

import com.avatye.adcash.platform.library.pref.PrefRepository
import com.avatye.adcash.platform.library.printout.PrintOut

internal object Settings {
    // logger
    val printout by lazy {
        PrintOut(moduleName = "ADCASH:Platform:Library:Basement")
    }

//    val useInterval
//        get() = PrefRepository.instance(context =   ).useInterval

}