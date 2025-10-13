package com.avatye.adcash.platform.provider.basement.interstitialad

import android.os.Handler
import android.os.Looper
import android.os.Message

abstract class AdsviserInterstitialLoaderBase {

    abstract val loaderName: String
    protected val leakHandler = LeakHandler()

    protected class LeakHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // nope
        }
    }

    abstract val interstitialUnit: AdsviserInterstitialUnit

    abstract val isLoaded: Boolean

    abstract fun requestLoad(): Unit

    abstract fun show(blockCallback: (success: Boolean) -> Unit = {}): Unit

    abstract fun onResume(): Unit

    abstract fun onPause(): Unit

    abstract fun onDestroy(): Unit
}