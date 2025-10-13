package com.avatye.adcash.platform.provider.admixer.bannerad.loader

import android.view.View
import com.avatye.adcash.platform.provider.basement.AdsviserError

internal interface AMsspBannerLoaderCallback {
    fun onLoaded(view: View, networkUnitName: String)
    fun onFailed(error: AdsviserError)
    fun onClicked()
}