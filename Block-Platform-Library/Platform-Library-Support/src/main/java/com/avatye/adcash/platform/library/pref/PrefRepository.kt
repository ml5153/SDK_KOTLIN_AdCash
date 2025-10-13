package com.avatye.adcash.platform.library.pref

import android.content.Context
import android.content.SharedPreferences
import com.avatye.adcash.platform.library.SingletonContextHolder
import com.avatye.adcash.platform.library.extension.editor

internal class PrefRepository private constructor(context: Context) {

    private val sourceName = "PrefRepository"
    private val preferenceName = "avatye-adcash:preferences"

    companion object : SingletonContextHolder<PrefRepository>(::PrefRepository)

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
    }

    private val keyAndroidAdvertiseId: String = "android:advertise-id"
    var advertiseId: String
        get() {
            return preferences.getString(keyAndroidAdvertiseId, "") ?: ""
        }
        set(value) {
            preferences.editor {
                putString(keyAndroidAdvertiseId, value)
            }
        }

    private val keyIsLimitAdTrackingEnabled = "android:advertise-is-limit-tracking"
    var isLimitAdTrackingEnabled: Boolean
        get() {
            return preferences.getBoolean(keyIsLimitAdTrackingEnabled, false)
        }
        set(value) {
            preferences.editor {
                putBoolean(keyIsLimitAdTrackingEnabled, value)
            }
        }


    // DoYouAd
    private val keyDoYouAdUseInterval = "doyouad:use-interval"
    var useInterval: Boolean
        get() {
            return preferences.getBoolean(keyDoYouAdUseInterval, false)
        }
        set(value) {
            preferences.editor {
                putBoolean(keyDoYouAdUseInterval, value)
            }
        }

    private val keyDoYouAdIntervalSec = "doyouad:interval-sec"
    var intervalSec: Int
        get() {
            return preferences.getInt(keyDoYouAdIntervalSec, 120) // Default value: 120
        }
        set(value) {
            preferences.editor {
                putInt(keyDoYouAdIntervalSec, value)
            }
        }
}