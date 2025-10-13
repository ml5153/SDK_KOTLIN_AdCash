package com.avatye.adcash.platform.provider.adpopcorn.bannerad.loader

import android.view.View
import com.avatye.adcash.platform.provider.basement.AdsviserError

internal interface APsspBannerLoaderCallback {
    fun onLoaded(view: View, networkUnitName: String)
    fun onFailed(error: AdsviserError)
    fun onClicked()
}