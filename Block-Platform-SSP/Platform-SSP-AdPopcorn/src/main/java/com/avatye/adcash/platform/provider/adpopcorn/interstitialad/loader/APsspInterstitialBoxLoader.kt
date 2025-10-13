package com.avatye.adcash.platform.provider.adpopcorn.interstitialad.loader

import android.app.Activity
import android.view.LayoutInflater
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.let2
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.adpopcorn.APsspErrorUnit
import com.avatye.adcash.platform.provider.adpopcorn.APsspNetworkUnit
import com.avatye.adcash.platform.provider.adpopcorn.Settings
import com.avatye.adcash.platform.provider.adpopcorn.Settings.printout
import com.avatye.adcash.platform.provider.adpopcorn.databinding.AcbAdcashSspAdpopcornContainerInterstitialBox300x250Binding
import com.avatye.adcash.platform.provider.adpopcorn.interstitialad.loader.viewer.APsspInterstitialViewer
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit
import com.igaworks.ssp.BannerAnimType
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.banner.AdPopcornSSPBannerAd
import com.igaworks.ssp.part.banner.listener.IBannerEventCallbackListener
import java.lang.ref.WeakReference

internal class APsspInterstitialBoxLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementID: String,
    private val callback: APsspInterstitialLoaderCallback
) : APsspInterstitialLoaderBase(), IBannerEventCallbackListener {

    private val sourceName = "APsspInterstitialBoxLoader"
    private val weakActivity = WeakReference(activity)
    private var leakView: AcbAdcashSspAdpopcornContainerInterstitialBox300x250Binding? = null

    override val loaderName: String get() = "InterstitialBoxLoader"
    override val networkUnitNum: Int get() = ssp?.currentNetwork ?: -1
    override val interstitialUnit = AdsviserInterstitialUnit.INTERSTITIAL_BOX

    init {
        weakActivity.isAvailable { wActivity ->
            leakView = AcbAdcashSspAdpopcornContainerInterstitialBox300x250Binding.inflate(LayoutInflater.from(wActivity))
        }
    }

    private var ssp: AdPopcornSSPBannerAd? = null
    private fun initializer(blockCallback: () -> Unit) {
        weakActivity.isAvailable { wActivity ->
            if (ssp == null) {
                ssp = AdPopcornSSPBannerAd(wActivity.application).apply {
                    this.autoBgColor = false
                    this.placementId = placementID
                    this.setPlacementAppKey(placementAppKey)
                    this.setAdSize(com.igaworks.ssp.AdSize.BANNER_300x250)
                    this.setBannerAnimType(BannerAnimType.NONE)
                    this.setRefreshTime(Settings.RefreshTime)
                    this.setNetworkScheduleTimeout(Settings.NetworkScheduleTimeout)
                    this.setBannerEventCallbackListener(this@APsspInterstitialBoxLoader)
                }
            }
        }
        blockCallback()
    }

    override fun requestLoad() {
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
            return ssp?.isDisplayed ?: false
        }

    override fun show(blockCallback: (success: Boolean) -> Unit) {
        printout.info(sourceName = sourceName, trace = { "show" })
        weakActivity.isAvailable {
            leakView?.produce { rootView ->
                if (isLoaded) {
                    APsspInterstitialViewer.create(
                        activity = activity,
                        adView = rootView.root,
                        actionDismiss = object : APsspInterstitialViewer.DismissActionCallback {
                            override fun onDismiss() = callback.onClosed(isCompleted = true)
                        }
                    ).show(cancelable = false) {
                        if (it) {
                            callback.onOpened()
                        }
                        blockCallback(it)
                    }
                } else {
                    blockCallback(false)
                }
            }
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
            ssp?.setBannerEventCallbackListener(null)
            ssp?.removeAllViews()
            ssp?.stopAd()
            ssp = null
            weakActivity.clear()
        }.onFailure {
            printout.error(sourceName = sourceName, trace = { "onDestroy" }, throwable = it)
        }
    }

    override fun OnBannerAdReceiveSuccess() {
        weakActivity.isAvailable {
            let2(ssp, leakView) { innerSSP, innerLeakView ->
                innerLeakView.interstitialBoxContent.addView(innerSSP)
                callback.onLoaded(unitType = interstitialUnit, networkUnitName = networkUnitName)
            } ?: run {
                callback.onFailed(
                    error = APsspErrorUnit.of(
                        errorUnit = APsspErrorUnit.EXCEPTION_LOADER_IS_NULL,
                        networkUnit = networkUnit
                    )
                )
            }
        }
    }

    override fun OnBannerAdReceiveFailed(error: SSPErrorCode?) {
        weakActivity.isAvailable {
            ssp?.produce {
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
            } ?: run {
                callback.onFailed(
                    error = APsspErrorUnit.of(
                        errorUnit = APsspErrorUnit.EXCEPTION_LOADER_IS_NULL,
                        networkUnit = networkUnit
                    )
                )
            }
        }
    }

    override fun OnBannerAdClicked() {
        weakActivity.isAvailable {
            if (ssp != null) {
                callback.onClicked()
            }
        }
    }
}