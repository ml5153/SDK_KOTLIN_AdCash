package com.avatye.adcash.platform.provider.adhouse.interstitialad.loader

import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialLoaderBase

abstract class AHsspInterstitialLoaderBase: AdsviserInterstitialLoaderBase() {

    protected val networkUnitName: String get() = "AdHouse"

}