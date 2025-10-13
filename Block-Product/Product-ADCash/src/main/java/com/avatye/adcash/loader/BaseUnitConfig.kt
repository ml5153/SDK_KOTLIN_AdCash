package com.avatye.adcash.loader

import com.avatye.adcash.ADCashSettings
import com.avatye.adcash.AppKeySetting
import com.avatye.adcash.biz.interact.Interact
import com.avatye.adcash.platform.provider.basement.AdsviserAgeVerifier

abstract class BaseUnitConfig {

    internal val interactor: Interact.ADUnit
        get() {
            return if (externalInteractor != null) {
                externalInteractor!!
            } else {
                baseInteractor
            }
        }

    internal val ageVerifier: AdsviserAgeVerifier
        get() {
            return if (externalAdAgeVerifier != null) {
                externalAdAgeVerifier!!
            } else {
                ADCashSettings.ageVerifier
            }
        }

    internal val storeUrl: String
        get() {
            return if (externalStoreUrl.isNotEmpty()) {
                externalStoreUrl
            } else {
                ADCashSettings.storeUrl
            }
        }


    private val baseInteractor by lazy {
        Interact.ADUnit(appId = "", appSecret = "")
    }

    private var externalAdAgeVerifier: AdsviserAgeVerifier? = null
    private var externalInteractor: Interact.ADUnit? = null
    private var externalStoreUrl: String = ""

    fun setAppKeySetting(appKeySetting: AppKeySetting) {
        // AgeVerifier
        externalAdAgeVerifier = object : AdsviserAgeVerifier {
            override fun isVerified(): Boolean {
                return appKeySetting.ageVerifier
            }
        }
        // Interactor
        externalInteractor = Interact.ADUnit(
            appId = appKeySetting.appId,
            appSecret = appKeySetting.appSecret
        )
    }

    fun externalStoreUrl(storeUrl: String) {
        externalStoreUrl = storeUrl
    }
}