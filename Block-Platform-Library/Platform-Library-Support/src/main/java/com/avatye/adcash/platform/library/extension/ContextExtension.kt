package com.avatye.adcash.platform.library.extension

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

fun Context.metaString(keyName: String): String? {
    return try {
        metaDataBundle()?.getString(keyName, "")?.ifEmpty { null }
    } catch (e: Exception) {
        null
    }
}


fun Context.metaBoolean(keyName: String, defaulValue: Boolean = false): Boolean? {
    return try {
        metaDataBundle()?.getBoolean(keyName, defaulValue)
    } catch (e: Exception) {
        null
    }
}

fun Context.metaDataBundle(): Bundle? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
            ).metaData
        } else {
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            ).metaData
        }
    } catch (e: Exception) {
        null
    }
}