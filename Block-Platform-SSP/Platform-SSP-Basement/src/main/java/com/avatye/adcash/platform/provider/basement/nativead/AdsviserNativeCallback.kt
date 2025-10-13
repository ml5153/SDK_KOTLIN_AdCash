package com.avatye.adcash.platform.provider.basement.nativead

import android.view.View
import com.avatye.adcash.platform.provider.basement.AdsviserError

interface AdsviserNativeCallback {
    fun onLoaded(adView: View, networkUnitName: String)
    fun onFailed(error: AdsviserError)
    fun onImpression()
    fun onNeedAgeVerification()
    fun onClicked()
}