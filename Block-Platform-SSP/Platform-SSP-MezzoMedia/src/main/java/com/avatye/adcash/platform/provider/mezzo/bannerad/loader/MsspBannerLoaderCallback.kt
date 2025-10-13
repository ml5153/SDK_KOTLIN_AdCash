package com.avatye.adcash.platform.provider.mezzo.bannerad.loader

import android.view.View
import com.avatye.adcash.platform.provider.basement.AdsviserError

internal interface MsspBannerLoaderCallback {
    fun onLoaded(view: View, networkUnitName: String)
    fun onFailed(error: AdsviserError)
    fun onClicked()
}