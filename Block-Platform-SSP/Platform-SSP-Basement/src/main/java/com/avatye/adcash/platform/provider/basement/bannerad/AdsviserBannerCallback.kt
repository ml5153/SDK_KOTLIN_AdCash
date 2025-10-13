package com.avatye.adcash.platform.provider.basement.bannerad

import android.view.View
import com.avatye.adcash.platform.provider.basement.AdsviserError

interface AdsviserBannerCallback {
    fun onLoaded(adView: View, unitSize: AdsviserBannerUnitSize, networkUnitName: String)
    fun onFailed(error: AdsviserError)
    fun onNeedAgeVerification()
    fun onClicked()
}