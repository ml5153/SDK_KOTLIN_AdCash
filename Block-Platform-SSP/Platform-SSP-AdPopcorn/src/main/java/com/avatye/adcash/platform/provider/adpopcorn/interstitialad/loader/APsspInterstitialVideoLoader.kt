package com.avatye.adcash.platform.provider.adpopcorn.interstitialad.loader

import android.app.Activity
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.adpopcorn.APsspErrorUnit
import com.avatye.adcash.platform.provider.adpopcorn.APsspNetworkUnit
import com.avatye.adcash.platform.provider.adpopcorn.Settings
import com.avatye.adcash.platform.provider.adpopcorn.Settings.printout
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.video.AdPopcornSSPInterstitialVideoAd
import com.igaworks.ssp.part.video.listener.IInterstitialVideoAdEventCallbackListener
import org.joda.time.DateTime
import java.lang.ref.WeakReference

internal class APsspInterstitialVideoLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementID: String,
    private val videoInterval: Long,
    private val callback: APsspInterstitialLoaderCallback
) : APsspInterstitialLoaderBase(), IInterstitialVideoAdEventCallbackListener {

    private val sourceName = "APsspInterstitialVideoLoader"
    private val weakActivity = WeakReference(activity)

    override val loaderName: String get() = "InterstitialVideoLoader"
    override val networkUnitNum: Int get() = ssp?.currentNetwork ?: -1
    override val interstitialUnit = AdsviserInterstitialUnit.INTERSTITIAL_VIDEO

    private var videoPlayTimestamp = 0L
    private var ssp: AdPopcornSSPInterstitialVideoAd? = null
    private fun initializer(blockCallback: () -> Unit) {
        printout.info(sourceName = sourceName, trace = { "initializer" })
        weakActivity.isAvailable { wActivity ->
            if (ssp == null) {
                ssp = AdPopcornSSPInterstitialVideoAd(wActivity).apply {
                    this.setPlacementId(this@APsspInterstitialVideoLoader.placementID)
                    this.setPlacementAppKey(this@APsspInterstitialVideoLoader.placementAppKey)
                    this.setNetworkScheduleTimeout(Settings.VideoNetworkScheduleTimeout)
                    this.setEventCallbackListener(this@APsspInterstitialVideoLoader)
                }
            }
        }
        blockCallback()
    }

    override fun requestLoad() {
        printout.info(sourceName = sourceName, trace = { "requestLoad" })
        weakActivity.isAvailable { wActivity ->
            Settings.initSSP(context = wActivity, appKey = placementAppKey, placementId = placementID) {
                initializer {
                    ssp?.loadAd() ?: run {
                        callback.onFailed(
                            error = APsspErrorUnit.of(
                                errorUnit = APsspErrorUnit.EXCEPTION_LOADER_IS_NULL,
                                networkUnit = networkUnit
                            )
                        )
                    }
                }
            }
        }
    }

    override val isLoaded: Boolean
        get() {
            return ssp?.isReady ?: false
        }

    override fun show(blockCallback: (success: Boolean) -> Unit) {
        printout.info(sourceName = sourceName, trace = { "show" })
        weakActivity.isAvailable {
            ssp?.produce { innerSSP ->
                if (innerSSP.isReady) {
                    innerSSP.showAd()
                    blockCallback(true)
                } else {
                    blockCallback(false)
                }
            } ?: blockCallback(false)
        }
    }

    override fun onResume() {
        printout.info(sourceName = sourceName, trace = { "onResume" })
        if (networkUnit != APsspNetworkUnit.MEZZOMEDIA) {
            runCatching {
                ssp?.onResume()
            }.onFailure {
                printout.error(sourceName = sourceName, trace = { "onResume" }, throwable = it)
            }
        }
    }

    override fun onPause() {
        printout.info(sourceName = sourceName, trace = { "onPause" })
        if (networkUnit != APsspNetworkUnit.MEZZOMEDIA) {
            runCatching {
                ssp?.onPause()
            }.onFailure {
                printout.error(sourceName = sourceName, trace = { "onPause" }, throwable = it)
            }
        }
    }

    override fun onDestroy() {
        printout.info(sourceName = sourceName, trace = { "onDestroy" })
        runCatching {
            ssp?.setEventCallbackListener(null)
            ssp?.destroy()
            ssp = null
            weakActivity.clear()
        }.onFailure {
            printout.error(sourceName = sourceName, trace = { "onDestroy" }, throwable = it)
        }
    }

    override fun OnInterstitialVideoAdLoaded() {
        weakActivity.isAvailable {
            if (ssp != null) {
                callback.onLoaded(unitType = interstitialUnit, networkUnitName = networkUnitName)
            } else {
                callback.onFailed(
                    error = APsspErrorUnit.of(
                        errorUnit = APsspErrorUnit.EXCEPTION_LOADER_IS_NULL,
                        networkUnit = networkUnit
                    )
                )
            }
        }
    }

    override fun OnInterstitialVideoAdLoadFailed(error: SSPErrorCode?) {
        weakActivity.isAvailable {
            if (ssp != null) {
                callback.onFailed(
                    when (Settings.verifyBlocked(error)) {
                        true -> APsspErrorUnit.of(APsspErrorUnit.BLOCKED, networkUnit)
                        false -> APsspErrorUnit.of(error, networkUnit)
                    }
                )
            } else {
                callback.onFailed(
                    error = APsspErrorUnit.of(
                        errorUnit = APsspErrorUnit.EXCEPTION_LOADER_IS_NULL,
                        networkUnit = networkUnit
                    )
                )
            }
        }
    }

    override fun OnInterstitialVideoAdOpened() {
        weakActivity.isAvailable {
            if (ssp != null) {
                videoPlayTimestamp = DateTime().millis
                callback.onOpened()
            } else {
                callback.onFailed(
                    error = APsspErrorUnit.of(
                        errorUnit = APsspErrorUnit.EXCEPTION_LOADER_IS_NULL,
                        networkUnit = networkUnit
                    )
                )
            }
        }
    }

    override fun OnInterstitialVideoAdOpenFalied() {
        weakActivity.isAvailable {
            if (ssp != null) {
                callback.onFailed(
                    error = APsspErrorUnit.of(
                        errorUnit = APsspErrorUnit.FAIL_OPEN,
                        networkUnit = networkUnit
                    )
                )
            } else {
                callback.onFailed(
                    error = APsspErrorUnit.of(
                        errorUnit = APsspErrorUnit.EXCEPTION_LOADER_IS_NULL,
                        networkUnit = networkUnit
                    )
                )
            }
        }
    }

    override fun OnInterstitialVideoAdClosed() {
        weakActivity.isAvailable {
            if (ssp != null) {
                val isCompleted = (DateTime().millis - videoPlayTimestamp) >= videoInterval
                callback.onClosed(isCompleted = isCompleted)
            } else {
                callback.onFailed(
                    error = APsspErrorUnit.of(
                        errorUnit = APsspErrorUnit.EXCEPTION_LOADER_IS_NULL,
                        networkUnit = networkUnit
                    )
                )
            }
        }
    }

    override fun OnInterstitialVideoAdClicked() {
        weakActivity.isAvailable {
            if (ssp != null) {
                callback.onClicked()
            } else {
                callback.onFailed(
                    error = APsspErrorUnit.of(
                        errorUnit = APsspErrorUnit.EXCEPTION_LOADER_IS_NULL,
                        networkUnit = networkUnit
                    )
                )
            }
        }
    }
}