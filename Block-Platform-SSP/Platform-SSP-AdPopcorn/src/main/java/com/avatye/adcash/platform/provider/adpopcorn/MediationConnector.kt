package com.avatye.adcash.platform.provider.adpopcorn

import android.content.Context
import com.avatye.adcash.platform.provider.adpopcorn.Settings.printout
import com.avatye.adcash.platform.provider.adpopcorn.nativeview.AdPopcornNativeView
import com.avatye.adcash.platform.provider.adpopcorn.nativeview.MobwithNativeView
import com.avatye.adcash.platform.provider.adpopcorn.nativeview.NamNativeView
import com.avatye.adcash.platform.provider.adpopcorn.nativeview.PangleNativeView
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.MediationNativeViewBinder

object MediationConnector {

    private const val SourceName = "MediationConnector"

    fun requestNativeViewBinder(context: Context?, mediation: IMediationConnect.Mediation, size: IMediationConnect.Size): MediationNativeViewBinder? {
        printout.info(sourceName = SourceName) { "retrieveMediationBinder(${mediation.name})" }

        return try {
            val nativeView = when (mediation) {
                IMediationConnect.Mediation.ADPOPCORN -> makeNativeView(context, mediation)
                else -> {
                    if (MediationFactory.hasConnector(mediation)) {
                        makeNativeView(context, mediation)
                    } else {
                        printout.error(sourceName = SourceName) {
                            "requestNativeViewBinder { connector is notting!! }"
                        }
                        null
                    }
                }
            }

            nativeView?.requestNativeViewBinder(size)
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "retrieveMediationBinder -> exception"
            }
            null
        }

    }


    private fun makeNativeView(context: Context?, mediation: IMediationConnect.Mediation): IMediationConnect? {
        return when (mediation) {
            IMediationConnect.Mediation.ADPOPCORN -> AdPopcornNativeView()
            IMediationConnect.Mediation.PANGLE -> PangleNativeView()
            IMediationConnect.Mediation.MOBWITH -> MobwithNativeView()
            IMediationConnect.Mediation.NAM -> NamNativeView(context)
            else -> {
                printout.error(sourceName = SourceName) { "retrieveMediationBinder(${mediation.name} is not supported!! )" }
                null
            }
        }
    }

}