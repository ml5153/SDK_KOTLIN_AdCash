package com.avatye.adcash

import android.os.Build
import com.avatye.adcash.platform.library.BuildConfig
import com.avatye.adcash.platform.library.extension.produce

internal data class RequestProperty(
    val osName: String = "android",
    val osVersionCode: String = "${Build.VERSION.SDK_INT}",
    val deviceModel: String = "${Build.MODEL}:${Build.MANUFACTURER}",
    val sdkVersionCode: String = "${BuildConfig.X_BUILD_SDK_VERSION_CODE}",
    val sdkVersionName: String = BuildConfig.X_BUILD_SDK_VERSION_NAME,
    val isDeveloperMode: Boolean = false,
    val isMaintenanceMode: Boolean = false
) {

    var appHostType: String = "api"
        private set

    fun updateProperty(
        appHostType: String,
    ) {
        appHostType.produce { this.appHostType = it }
    }
}