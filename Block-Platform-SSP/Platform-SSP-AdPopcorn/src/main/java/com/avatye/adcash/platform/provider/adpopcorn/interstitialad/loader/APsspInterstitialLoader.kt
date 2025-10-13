package com.avatye.adcash.platform.provider.adpopcorn.interstitialad.loader

import android.app.Activity
import com.avatye.adcash.platform.library.extension.isAlive
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.provider.adpopcorn.APsspErrorUnit
import com.avatye.adcash.platform.provider.adpopcorn.Settings
import com.avatye.adcash.platform.provider.adpopcorn.Settings.printout
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.interstitial.AdPopcornSSPInterstitialAd
import com.igaworks.ssp.part.interstitial.listener.IInterstitialEventCallbackListener
import java.lang.ref.WeakReference

internal class APsspInterstitialLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementID: String,
    private val callback: APsspInterstitialLoaderCallback
) : APsspInterstitialLoaderBase(), IInterstitialEventCallbackListener {

    private val sourceName = "APsspInterstitialLoader"
    override val loaderName: String get() = "InterstitialLoader"
    override val networkUnitNum: Int get() = ssp?.currentNetwork ?: -1
    override val interstitialUnit = AdsviserInterstitialUnit.INTERSTITIAL
    private val weakActivity = WeakReference(activity)
    private var ssp: AdPopcornSSPInterstitialAd? = null

    private fun initializer(blockCallback: () -> Unit) {
        weakActivity.isAvailable { wActivity ->
            if (ssp == null) {
                ssp = AdPopcornSSPInterstitialAd(wActivity).apply {
                    this.setPlacementId(this@APsspInterstitialLoader.placementID)
                    this.setPlacementAppKey(this@APsspInterstitialLoader.placementAppKey)
                    this.setCurrentActivity(wActivity)
                    val extras: HashMap<String, Any> = hashMapOf(
                        AdPopcornSSPInterstitialAd.CustomExtraData.APSSP_AD_DISABLE_BACK_BTN to true,
                        AdPopcornSSPInterstitialAd.CustomExtraData.IS_ENDING_AD to false,
                        AdPopcornSSPInterstitialAd.CustomExtraData.APSSP_AD_BACKGROUND_COLOR to Settings.interstitialBackgroundColor
                    )
                    this.setCustomExtras(extras)
                    this.setInterstitialEventCallbackListener(this@APsspInterstitialLoader)
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
            return (weakActivity.get()?.isAlive == true) && (ssp?.isLoaded ?: false)
        }

    override fun show(blockCallback: (success: Boolean) -> Unit) {
        printout.info(sourceName = sourceName, trace = { "show { networkUnitName: $networkUnitName }" })
        weakActivity.isAvailable {
            if (ssp?.isLoaded == true) {
                ssp?.showAd()
                blockCallback(true)
            } else {
                blockCallback(false)
            }
        }
    }

    override fun onResume() {
        printout.info(sourceName = sourceName, trace = { "onResume" })
        runCatching {
            ssp?.onResume()
        }.onFailure {
            printout.error(sourceName = sourceName, trace = { "onResume" }, throwable = it)
        }
    }

    override fun onPause() {
        printout.info(sourceName = sourceName, trace = { "onPause" })
    }

    override fun onDestroy() {
        printout.info(sourceName = sourceName, trace = { "onDestroy" })
        runCatching {
            ssp?.setInterstitialEventCallbackListener(null)
            ssp?.destroy()
            ssp = null
            weakActivity.clear()
        }.onFailure {
            printout.error(sourceName = sourceName, trace = { "onDestroy" }, throwable = it)
        }
    }

    override fun OnInterstitialLoaded() {
        weakActivity.isAvailable {
            if (ssp != null) {
                printout.info(sourceName = sourceName, trace = { "OnInterstitialLoaded { networkUnitName: $networkUnitName }" })
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

    override fun OnInterstitialReceiveFailed(error: SSPErrorCode?) {
        printout.info(sourceName = sourceName, trace = { "OnInterstitialReceiveFailed { error: $error }" })
        weakActivity.isAvailable {
            if (ssp != null) {
                val verifyBlock = Settings.verifyBlocked(error)
                if (verifyBlock) {
                    callback.onFailed(
                        error = APsspErrorUnit.of(
                            errorUnit = APsspErrorUnit.BLOCKED,
                            networkUnit = networkUnit
                        )
                    )
                } else {
                    callback.onFailed(
                        error = APsspErrorUnit.of(
                            sspErrorUnit = error,
                            networkUnit = networkUnit
                        )
                    )
                }
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

    override fun OnInterstitialOpened() {
        printout.info(sourceName = sourceName, trace = { "OnInterstitialOpened { networkUnitName: $networkUnitName }" })
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

    override fun OnInterstitialOpenFailed(error: SSPErrorCode?) {
        printout.info(sourceName = sourceName, trace = { "OnInterstitialOpenFailed { networkUnitName: $networkUnitName }" })
        weakActivity.isAvailable {
            callback.onFailed(
                error = APsspErrorUnit.of(
                    sspErrorUnit = error,
                    networkUnit = networkUnit
                )
            )
        }
    }

    override fun OnInterstitialClosed(reason: Int) {
        weakActivity.isAvailable {
            callback.onClosed(isCompleted = true)
        }
    }

    override fun OnInterstitialClicked() {
        weakActivity.isAvailable {
            callback.onClicked()
        }
    }
}