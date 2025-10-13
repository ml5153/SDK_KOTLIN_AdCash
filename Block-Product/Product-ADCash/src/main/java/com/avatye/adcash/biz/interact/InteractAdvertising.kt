package com.avatye.adcash.biz.interact

import com.avatye.adcash.ADCashSettings
import com.avatye.adcash.biz.interact.entity.ResAdsUnit
import com.avatye.adcash.platform.library.network.capacitor.Capacitor
import com.avatye.adcash.platform.library.network.capacitor.CapacitorExecutor
import com.avatye.adcash.platform.library.network.capacitor.ICapacitorCallback

object Interact {
    class ADUnit(private val appId: String, private val appSecret: String) {
        fun retrieveADUnit(placementId: String, response: ICapacitorCallback<ResAdsUnit>) {
            CapacitorExecutor(
                host = ADCashSettings.property.appHostType,
                requestMethod = Capacitor.Method.GET,
                requestUrl = "advertising/appADUnit",
                requestAppId = appId,
                requestAppSecret = appSecret,
                acceptVersion = "1.1.0",
                argsHeader = null,
                argsBody = hashMapOf(
                    "appID" to appId,
                    "placementID" to placementId
                )
            ).execute(
                responseClass = ResAdsUnit::class.java,
                callback = response
            )
        }
    }
}