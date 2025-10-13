package com.avatye.adcash.platform.provider.adpopcorn.interstitialad.loader

import android.app.Activity
import android.content.Intent
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.adpopcorn.APsspErrorUnit
import com.avatye.adcash.platform.provider.adpopcorn.APsspNetworkUnit
import com.avatye.adcash.platform.provider.adpopcorn.Settings
import com.avatye.adcash.platform.provider.adpopcorn.Settings.printout
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.video.AdPopcornSSPRewardVideoAd
import com.igaworks.ssp.part.video.listener.IRewardVideoAdEventCallbackListener
import java.lang.ref.WeakReference

internal class APsspInterstitialRewardVideoLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementID: String,
    private val callback: APsspInterstitialLoaderCallback
) : APsspInterstitialLoaderBase(), IRewardVideoAdEventCallbackListener {

    private val sourceName = "APsspInterstitialRewardVideoLoader"
    private val weakActivity = WeakReference(activity)

    override val loaderName: String get() = "InterstitialRewardVideoLoader"
    override val networkUnitNum: Int get() = ssp?.currentNetwork ?: -1
    override val interstitialUnit = AdsviserInterstitialUnit.INTERSTITIAL_REWARD_VIDEO

    private var playCompleted: Boolean = false
    private var ssp: AdPopcornSSPRewardVideoAd? = null

    private fun initializer(blockCallback: () -> Unit) {
        printout.info(sourceName = sourceName, trace = { "initializer" })
        weakActivity.isAvailable { wActivity ->
            if (ssp == null) {
                ssp = AdPopcornSSPRewardVideoAd(wActivity).apply {
                    this.setPlacementId(this@APsspInterstitialRewardVideoLoader.placementID)
                    this.setPlacementAppKey(this@APsspInterstitialRewardVideoLoader.placementAppKey)
                    this.setNetworkScheduleTimeout(Settings.VideoNetworkScheduleTimeout)
                    this.setRewardVideoAdEventCallbackListener(this@APsspInterstitialRewardVideoLoader)
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
                    playCompleted = false
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
            ssp?.setRewardVideoAdEventCallbackListener(null)
            ssp?.destroy()
            ssp = null
            weakActivity.clear()
        }.onFailure {
            printout.error(sourceName = sourceName, trace = { "onDestroy" }, throwable = it)
        }
    }

    override fun OnRewardVideoAdLoaded() {
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

    override fun OnRewardVideoAdLoadFailed(error: SSPErrorCode?) {
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

    override fun OnRewardVideoAdOpened() {
        weakActivity.isAvailable {
            if (ssp != null) {
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

    override fun OnRewardVideoAdOpenFalied() {
        weakActivity.isAvailable { wActivity ->
            wActivity.startActivity(Intent(wActivity, wActivity::class.java).apply {
                this.flags = (Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
            val errorUnit = if (ssp != null) APsspErrorUnit.FAIL_OPEN else APsspErrorUnit.EXCEPTION_LOADER_IS_NULL
            callback.onFailed(
                error = APsspErrorUnit.of(
                    errorUnit = errorUnit,
                    networkUnit = networkUnit
                )
            )
        }
    }

    override fun OnRewardVideoAdClosed() {
        weakActivity.isAvailable {
            activity.startActivity(Intent(activity, activity::class.java).apply {
                this.flags = (Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
            if (ssp != null) {
                callback.onClosed(isCompleted = this.playCompleted)
            } else {
                callback.onClosed(isCompleted = false)
            }
        }
    }

    override fun OnRewardVideoPlayCompleted(adNetworkNo: Int, completed: Boolean) {
        weakActivity.isAvailable {
            this.playCompleted = (completed && ssp != null)
        }
    }

    override fun OnRewardVideoAdClicked() {
        weakActivity.isAvailable {
            if (ssp != null) {
                callback.onClicked()
            }
        }
    }

    override fun OnRewardPlusCompleted(p0: Boolean, p1: Int, p2: Int) {
    }
}