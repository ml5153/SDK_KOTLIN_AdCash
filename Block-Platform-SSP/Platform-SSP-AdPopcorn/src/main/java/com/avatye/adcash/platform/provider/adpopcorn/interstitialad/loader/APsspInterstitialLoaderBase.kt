package com.avatye.adcash.platform.provider.adpopcorn.interstitialad.loader

import com.avatye.adcash.platform.provider.adpopcorn.APsspNetworkUnit
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialLoaderBase

abstract class APsspInterstitialLoaderBase: AdsviserInterstitialLoaderBase() {

    protected abstract val networkUnitNum: Int
    protected val networkUnitName: String get() = APsspNetworkUnit.fromValue(networkUnitNum).name + "[$networkUnitNum]"
    protected val networkUnit: APsspNetworkUnit get() = APsspNetworkUnit.fromValue(networkUnitNum)

}