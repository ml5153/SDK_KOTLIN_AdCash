package com.avatye.adcash.platform.library.printout

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build

internal object PrintOutSettings {
    const val SourceName: String = "ADCash"
    const val LOGGABLE: String = "adcash.printout"

    var allowLog: Boolean = false
        private set

    fun update(allowLog: Boolean? = null) {
        allowLog?.also {
            PrintOutSettings.allowLog = it
        }
    }

    fun retrieveLog(application: Application) {
        allowLog = try {
            val bundle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                application.packageManager.getApplicationInfo(
                    application.packageName,
                    PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
                ).metaData
            } else {
                application.packageManager.getApplicationInfo(
                    application.packageName,
                    PackageManager.GET_META_DATA
                ).metaData
            }
            bundle.getBoolean("adcash.config.log", false)
        } catch (e: Exception) {
            PrintOut.print(moduleName = SourceName, trace = {
                "PrintOut retrieveLog exception ## $e ##"
            })
            false
        }
        if (allowLog) {
            PrintOut.print(moduleName = SourceName, trace = { "PrintOut Initialized" })
        }
    }
}