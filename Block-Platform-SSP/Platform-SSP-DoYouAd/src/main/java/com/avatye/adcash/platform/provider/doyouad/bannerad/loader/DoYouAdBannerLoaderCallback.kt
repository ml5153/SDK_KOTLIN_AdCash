package com.avatye.adcash.platform.provider.doyouad.bannerad.loader

import android.view.View
import com.avatye.adcash.platform.provider.basement.AdsviserError

internal interface DoYouAdBannerLoaderCallback {
    fun onLoaded(view: View, networkUnitName: String)
    fun onReLoaded(view: View, networkUnitName: String)
    fun onFailed(error: AdsviserError)
    fun onClicked()
}