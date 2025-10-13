package com.avatye.adcash

import android.content.Context
import com.avatye.adcash.platform.library.extension.produce

class ADCashSDK private constructor(private val builder: Builder) {

    init {
        ADCashSettings.initialize(
            context = builder.context,
            adCashAppId = builder.appId,
            adCashAppSecret = builder.appSecret
        )
        builder.storeUrl?.produce {
            ADCashSettings.storeUrl = it
        }
        builder.ageVerifier?.produce {
            ADCashSettings.updateAdAgeVerifier(verifier = it)
        }
        builder.isAgeVerified?.produce {
            ADCashSettings.isVerified = it
        }
        builder.userPhoneNumber?.produce {
            ADCashSettings.userPhoneNumber = it
        }
        builder.userName?.produce {
            ADCashSettings.userName = it
        }
        builder.appName?.produce {
            ADCashSettings.appName = it
        }
    }

    class Builder(val context: Context, val appId: String, val appSecret: String) {
        companion object {
            fun externalRetrieveProperties(context: Context) {
                ADCashSettings.retrieveProperties(context)
            }
        }

        var storeUrl: String? = null
            private set

        var ageVerifier: AdAgeVerifier? = null
            private set

        var isAgeVerified: Boolean? = null
            private set

        var userPhoneNumber: String? = null
            private set

        var userName: String? = null
            private set

        var appName: String? = null
            private set

        fun setStoreUrl(url: String) = apply {
            this.storeUrl = url
        }

        fun setUserPhoneNumber(phoneNumber: String) = apply {
            this.userPhoneNumber = phoneNumber
        }

        fun setUserName(name: String) = apply {
            this.userName = name
        }

        fun setAppName(appName: String) = apply { // 앱사의 이름 정도로
            this.appName = appName
        }

        fun setAgeVerified(adAgeVerifier: AdAgeVerifier) = apply {
            this.ageVerifier = adAgeVerifier
        }

        fun setAgeVerified(isVerified: Boolean) = apply {
            this.isAgeVerified = isVerified
        }

        fun build() = ADCashSDK(builder = this)
    }


    // as-is
    companion object {
        @JvmStatic
        fun initialize(context: Context, appId: String, appSecret: String, storeUrl: String? = null) {
            ADCashSettings.initialize(
                context = context,
                adCashAppId = appId,
                adCashAppSecret = appSecret
            )
            storeUrl?.produce {
                ADCashSettings.storeUrl = it
            }
        }

        @JvmStatic
        fun setStoreUrl(storeUrl: String) {
            ADCashSettings.storeUrl = storeUrl
        }

        @JvmStatic
        fun isInitialized(context: Context): Boolean {
            return ADCashSettings.isInitialized(context = context)
        }

        @JvmStatic
        fun setAgeVerified(adAgeVerifier: AdAgeVerifier) {
            ADCashSettings.updateAdAgeVerifier(verifier = adAgeVerifier)
        }

        @JvmStatic
        fun setAgeVerified(isVerified: Boolean) {
            ADCashSettings.isVerified = isVerified
        }

        @JvmStatic
        fun setUserPhoneNumber(userPhoneNumber: String) {
            ADCashSettings.userPhoneNumber = userPhoneNumber
        }

        @JvmStatic
        fun setUserName(userName: String) {
            ADCashSettings.userName = userName
        }

        @JvmStatic
        fun setAppName(appName: String) {
            ADCashSettings.appName = appName
        }
    }
}