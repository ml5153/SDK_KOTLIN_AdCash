package com.avatye.adcash.platform.provider.basement.nativead

abstract class AdsviserNative {
    abstract fun requestAD(): Unit?
    abstract fun onResume(): Unit?
    abstract fun onPause(): Unit?
    abstract fun onDestroy(): Unit?
}