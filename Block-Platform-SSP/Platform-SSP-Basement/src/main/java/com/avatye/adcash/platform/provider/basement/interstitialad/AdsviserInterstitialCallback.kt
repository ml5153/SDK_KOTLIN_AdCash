package com.avatye.adcash.platform.provider.basement.interstitialad

import androidx.annotation.Keep
import com.avatye.adcash.platform.provider.basement.AdsviserError

@Keep
interface AdsviserInterstitialCallback {
    fun onLoaded(loader: AdsviserInterstitialLoaderBase, unitType: AdsviserInterstitialUnit, networkUnitName: String)
    fun onOpened()
    fun onComplete(completed: Boolean)
    fun onFailed(error: AdsviserError)
    fun onNeedAgeVerification()
    fun onClicked()
}