package com.avatye.adcash.platform.provider.admixer

import android.content.Context
import com.avatye.adcash.platform.library.printout.PrintOut
import com.nasmedia.admixer.common.AdMixer
import com.nasmedia.admixer.common.AdMixerLog

internal object Settings {

    const val adsviserName = "AMSSP"
    private const val sourceName = "AmsspSettings"

    // logger
    val printout by lazy { PrintOut(moduleName = "ADCASH:Platform:SSP:ADMixer") }

    const val RefreshTime = -1
    const val NetworkScheduleTimeout = 8
    const val VideoNetworkScheduleTimeout = 8

    fun initSSP(context: Context, appKey: String, placementId: String, block: () -> Unit) {
        printout.info { "initSSP => appKey: $appKey, placementId: $placementId" }

        if (printout.allowLog) {
            AdMixerLog.logLevel = AdMixerLog.LogLevel.VERBOSE
        }

        if (AdMixer.getInstance().mediaKey == null) {
            AdMixer.getInstance().initialize(context, appKey, arrayListOf(placementId))
        }

        if (!AdMixer.getInstance().hasAdUnit(placementId)) {
            AdMixer.getInstance().setAdUnit(placementId)
        }

        block()
    }

}