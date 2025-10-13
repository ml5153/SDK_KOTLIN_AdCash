package com.avatye.adcash.platform.provider.basement.bannerad

abstract class AdsviserBanner {
    abstract val propertySize: Int
    abstract fun requestAD(): Unit?
    abstract fun onResume(): Unit?
    abstract fun onPause(): Unit?
    abstract fun onDestroy(): Unit?
}