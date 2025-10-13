package com.avatye.adcash.platform.provider.basement.interstitialad

abstract class AdsviserInterstitial {

    abstract val propertySize: Int
    abstract fun requestAD(): Unit?
    abstract fun onResume(): Unit?
    abstract fun onPause(): Unit?
    abstract fun onDestroy(): Unit?

}