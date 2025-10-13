package com.avatye.adcash.platform.library.adid

import android.content.Context
import androidx.annotation.Keep
import com.avatye.adcash.platform.library.Settings.printout
import com.avatye.adcash.platform.library.CoroutineTask
import com.avatye.adcash.platform.library.pref.PrefRepository
import com.google.android.gms.ads.identifier.AdvertisingIdClient

class AndroidAdvertiseIdTask(
    private val context: Context,
    private val callback: (advertiseInfo: AndroidAdvertiseInfo) -> Unit
) : CoroutineTask<Void, AndroidAdvertiseInfo>() {

    private val sourceName = "AndroidAdvertiseIdTask"

    @Keep
    companion object {
        const val EmptyValue = "00000000-0000-0000-0000-000000000000"
        fun isValid(adid: String): Boolean {
            return !(adid.isEmpty() || adid == "0" || adid == EmptyValue)
        }
    }

    override fun doInBackground(vararg params: Void): AndroidAdvertiseInfo {
        return try {
            val info = AdvertisingIdClient.getAdvertisingIdInfo(context)
            AndroidAdvertiseInfo(
                id = info.id ?: EmptyValue,
                isLimitAdTrackingEnabled = info.isLimitAdTrackingEnabled
            )
        } catch (e: Exception) {
            printout.error(sourceName = sourceName, throwable = e) {
                "doInBackground::exception"
            }
            try {
                AndroidAdvertiseInfo(
                    id = PrefRepository.instance(context = context).advertiseId,
                    isLimitAdTrackingEnabled = PrefRepository.instance(context = context).isLimitAdTrackingEnabled
                )
            } catch (innerException: Exception) {
                printout.info(sourceName = sourceName, throwable = innerException) {
                    "doInBackground::innerException"
                }
                AndroidAdvertiseInfo(id = "", isLimitAdTrackingEnabled = false)
            }
        }
    }

    override fun onPostExecute(result: AndroidAdvertiseInfo) {
        val isLimitAdTrackingEnabled = result.isLimitAdTrackingEnabled
        val adid = if (isLimitAdTrackingEnabled) EmptyValue else result.id
        runCatching {
            if (PrefRepository.instance(context = context).advertiseId != adid) {
                PrefRepository.instance(context = context).advertiseId = adid
                printout.info(sourceName = sourceName) {
                    "onPostExecute::update { adid: $adid }"
                }
            }
            if (PrefRepository.instance(context = context).isLimitAdTrackingEnabled != isLimitAdTrackingEnabled) {
                PrefRepository.instance(context = context).isLimitAdTrackingEnabled = isLimitAdTrackingEnabled
                printout.info(sourceName = sourceName) {
                    "onPostExecute::update { isLimitAdTrackingEnabled: $isLimitAdTrackingEnabled }"
                }
            }
        }.onFailure {
            printout.error(sourceName = sourceName, throwable = it) {
                "onPostExecute::exception { isLimitAdTrackingEnabled: $isLimitAdTrackingEnabled }"
            }
        }
        printout.info(sourceName = sourceName) {
            "onPostExecute { adid: $adid, isLimitAdTrackingEnabled: $isLimitAdTrackingEnabled }"
        }
        callback.invoke(
            AndroidAdvertiseInfo(
                id = adid,
                isLimitAdTrackingEnabled = isLimitAdTrackingEnabled
            )
        )
    }
}