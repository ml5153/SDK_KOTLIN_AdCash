package com.avatye.adcash.platform.provider.adhouse.bannerad.loader

import android.view.View
import com.avatye.adcash.platform.provider.basement.AdsviserError

internal interface AHsspBannerLoaderCallback {
    fun onLoaded(view: View, networkUnitName: String)
    fun onFailed(error: AdsviserError)
    fun onClicked()
}