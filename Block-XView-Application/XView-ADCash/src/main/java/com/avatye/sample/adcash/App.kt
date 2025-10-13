package com.avatye.sample.adcash

import androidx.multidex.MultiDexApplication
import com.avatye.adcash.ADCashSDK

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        val builder = ADCashSDK.Builder(
            context = this,
            appId = getString(R.string.appId),
            appSecret = getString(R.string.appSecret),
        )

        builder.setStoreUrl(url = "https://www.avatye.com")
        builder.setUserPhoneNumber("1111-1111-1111")
        builder.setUserName("KIM")
        builder.setAppName("ADCASH")
        builder.build()
    }
}