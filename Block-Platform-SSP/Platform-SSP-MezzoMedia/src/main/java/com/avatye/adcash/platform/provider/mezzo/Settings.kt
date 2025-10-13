package com.avatye.adcash.platform.provider.mezzo

import android.content.Context
import android.graphics.Color
import com.avatye.adcash.platform.library.printout.PrintOut

internal object Settings {


    const val adsviserName = "MSSP"
    private const val sourceName = "MsspSettings"

    // logger
    val printout by lazy { PrintOut(moduleName = "ADCASH:Platform:SSP:MezzoMedia") }

    const val RefreshTime = -1
    const val NetworkScheduleTimeout = 8
    const val VideoNetworkScheduleTimeout = 8

    private var innerInterstitialBackgroundColor = Color.parseColor("#52000000")

    // region { advertise ssp module init - async }
    fun initSSP(context: Context, appKey: String, placementId: String, block: () -> Unit) {
        printout.info { "initSSP => appKey: $appKey, placementId: $placementId" }
    }
}