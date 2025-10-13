package com.avatye.adcash.platform.library.network.capacitor

import android.os.Build
import com.avatye.adcash.platform.library.BuildConfig
import com.avatye.adcash.platform.library.extension.toBase64

class CapacitorExecutor(
    private val host: String = "api",
    private val requestMethod: Capacitor.Method,
    private val requestUrl: String,
    private val requestAppId: String,
    private val requestAppSecret: String,
    private val acceptVersion: String,
    private val argsHeader: HashMap<String, String>? = null,
    private val argsBody: HashMap<String, Any>? = null
) {

    private val URL = "https://%s.reward.avatye.com/%s"

    fun <T : CapacitorResponseFactory> execute(responseClass: Class<T>, callback: ICapacitorCallback<T>) {
        Capacitor.Task(
            reqType = this.requestMethod,
            reqUrl = makeFormattedUrl(),
            reqBody = argsBody,
            reqHeader = makeHeaders(),
            reqInfo = makeInfo(),
            responseClass = responseClass
        ).execute(callback)
    }

    private fun makeInfo(): HashMap<String, String> {
        return hashMapOf(
            "x-device-os" to "android",
            "x-device-os-version" to "${Build.VERSION.SDK_INT}",
            "x-device-model" to "${Build.MODEL}:${Build.MANUFACTURER}",
            "x-sdk-version-code" to "${BuildConfig.X_BUILD_SDK_VERSION_CODE}",
            "x-sdk-version-name" to BuildConfig.X_BUILD_SDK_VERSION_NAME,
            "x-app-service-name" to "ADCash",
            "accept-version" to acceptVersion,
        ).apply {
            argsHeader?.let {
                for ((key, value) in it) {
                    this[key] = value
                }
            }
        }
    }

    private fun makeHeaders(): HashMap<String, String> {
        return hashMapOf(
            "Content-Type" to "application/json",
            "Authorization" to "Basic %s".format("${requestAppId}:${requestAppSecret}".toBase64)
        ).apply {
            argsHeader?.let {
                for ((key, value) in it) {
                    this[key] = value
                }
            }
        }
    }

    private fun makeFormattedUrl(): String {
        return when (CapacitorHostType.from(host)) {
            CapacitorHostType.DEV -> URL.format("api-dev", requestUrl)
            CapacitorHostType.QA -> URL.format("api-qa", requestUrl)
            CapacitorHostType.STAGE -> URL.format("api-stage", requestUrl)
            CapacitorHostType.LIVE -> URL.format("api", requestUrl)
        }
    }

}