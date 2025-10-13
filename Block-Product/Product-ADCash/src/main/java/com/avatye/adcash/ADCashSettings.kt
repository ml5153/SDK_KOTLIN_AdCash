package com.avatye.adcash

import android.content.Context
import com.avatye.adcash.platform.library.extension.metaString
import com.avatye.adcash.platform.library.printout.PrintOut
import com.avatye.adcash.platform.provider.basement.AdsviserAgeVerifier

internal object ADCashSettings {
    // logger
    val printout by lazy {
        PrintOut(moduleName = "Product:Unit:ADCash")
    }

    const val videoInterval = 0L // 비디오 인터벌
    const val DEFAULT_LOADER_TIMEOUT = 30000L

    var adCashAppId: String = ""
        private set

    var adCashAppSecret: String = ""
        private set

    var userPhoneNumber: String? = null
    var userName: String? = null
    var appName: String? = null

    var isVerified = true
    private var adAgeVerifier: AdAgeVerifier? = null
    val property = RequestProperty()

    var storeUrl: String = ""

    fun isInitialized(context: Context): Boolean {
        return adCashAppId.isNotEmpty() && adCashAppSecret.isNotEmpty()
    }

    fun initialize(context: Context, adCashAppId: String, adCashAppSecret: String) {
        this.adCashAppId = adCashAppId
        this.adCashAppSecret = adCashAppSecret
        this.retrieveProperties(context)
    }

    fun updateAdAgeVerifier(verifier: AdAgeVerifier) {
        adAgeVerifier = verifier
    }

    var ageVerifier = object : AdsviserAgeVerifier {
        override fun isVerified(): Boolean {
            return adAgeVerifier?.isVerified() ?: isVerified
        }
    }

    internal fun retrieveProperties(context: Context) {
        runCatching {
            val appHostType = context.metaString(
                keyName = "avatye.config.host"
            )?: "api"
            property.updateProperty(
                appHostType = appHostType
            )
        }.onFailure {
            printout.error(sourceName = "Business:Data:Behavior:Basement::Settings", throwable = it)
        }
    }

}