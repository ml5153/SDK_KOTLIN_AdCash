package com.avatye.adcash.platform.provider.adpopcorn.interstitialad.loader

import com.avatye.adcash.platform.provider.basement.AdsviserError
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit

internal interface APsspInterstitialLoaderCallback {
    fun onLoaded(unitType: AdsviserInterstitialUnit, networkUnitName: String)
    fun onFailed(error: AdsviserError)
    fun onOpened()
    fun onClosed(isCompleted: Boolean)
    fun onClicked()
}