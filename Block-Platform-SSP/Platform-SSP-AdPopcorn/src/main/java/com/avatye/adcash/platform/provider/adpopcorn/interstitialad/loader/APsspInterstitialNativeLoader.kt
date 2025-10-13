package com.avatye.adcash.platform.provider.adpopcorn.interstitialad.loader

import android.app.Activity
import android.view.LayoutInflater
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect
import com.avatye.adcash.platform.provider.adpopcorn.MediationConnector
import com.avatye.adcash.platform.library.extension.isAlive
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.adpopcorn.APsspErrorUnit
import com.avatye.adcash.platform.provider.adpopcorn.Settings
import com.avatye.adcash.platform.provider.adpopcorn.Settings.printout
import com.avatye.adcash.platform.provider.adpopcorn.databinding.AcbAdcashSspAdpopcornContainerInterstitialNative320x480Binding
import com.avatye.adcash.platform.provider.adpopcorn.interstitialad.loader.viewer.APsspInterstitialViewer
import com.avatye.adcash.platform.provider.adpopcorn.tools.APsspNativeAdTools
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.nativead.AdPopcornSSPNativeAd
import com.igaworks.ssp.part.nativead.listener.INativeAdEventCallbackListener
import java.lang.ref.WeakReference

internal class APsspInterstitialNativeLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementID: String,
    private val callback: APsspInterstitialLoaderCallback
) : APsspInterstitialLoaderBase(), INativeAdEventCallbackListener {

    private val sourceName = "APsspInterstitialNativeLoader"
    override val loaderName: String get() = "InterstitialNativeLoader"
    override val networkUnitNum: Int get() = ssp?.currentNetwork ?: -1
    override val interstitialUnit = AdsviserInterstitialUnit.INTERSTITIAL_NATIVE

    private val weakActivity = WeakReference(activity)
    private var leakView: AcbAdcashSspAdpopcornContainerInterstitialNative320x480Binding? = null

    init {
        weakActivity.isAvailable { wActivity ->
            leakView = AcbAdcashSspAdpopcornContainerInterstitialNative320x480Binding.inflate(LayoutInflater.from(wActivity))
        }
    }

    private var ssp: AdPopcornSSPNativeAd? = null
    private fun initializer(blockCallback: () -> Unit) {
        weakActivity.isAvailable { wActivity ->
            leakView?.produce { rootView ->
                if (ssp == null) {
                    ssp = rootView.sspNativeAdview.apply {
                        // callback
                        this.setPlacementId(this@APsspInterstitialNativeLoader.placementID)
                        this.setPlacementAppKey(this@APsspInterstitialNativeLoader.placementAppKey)
                        this.setNativeAdEventCallbackListener(this@APsspInterstitialNativeLoader)
                        // native-adpopcorn
                        MediationConnector.requestNativeViewBinder(
                            context= wActivity,
                            mediation = IMediationConnect.Mediation.ADPOPCORN,
                            size = IMediationConnect.Size.W320XH480
                        )?.produce { adPlusNativeViewBinder ->
                            adPlusNativeViewBinder.adPopcornNativeViewBinder?.produce { adpopcorn ->
                                APsspNativeAdTools.makeAdPopcornViewBinder(
                                    context = wActivity,
                                    sspNativeAd = this,
                                    nativeBinder = adpopcorn
                                )?.produce {
                                    this.adPopcornSSPViewBinder = it
                                }
                            }
                        }
                        // native-pangle
                        MediationConnector.requestNativeViewBinder(
                            context= wActivity,
                            mediation = IMediationConnect.Mediation.PANGLE,
                            size = IMediationConnect.Size.W320XH480
                        )?.produce { adPlusNativeViewBinder ->
                            adPlusNativeViewBinder.pangleNativeViewBinder?.produce { pangle ->
                                APsspNativeAdTools.makePangleViewBinder(
                                    context = wActivity,
                                    sspNativeAd = this,
                                    nativeBinder = pangle
                                )?.produce {
                                    this.pangleViewBinder = it
                                }
                            }
                        }
                    }
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
            return (ssp?.isLoaded ?: false) && (weakActivity.get()?.isAlive == true)
        }

    override fun show(blockCallback: (success: Boolean) -> Unit) {
        printout.info(sourceName = sourceName, trace = { "show" })
        weakActivity.isAvailable {
            leakView?.produce { rootView ->
                if (ssp?.isLoaded == true) {
                    APsspInterstitialViewer.create(
                        activity = activity,
                        adView = rootView.root,
                        actionDismiss = object : APsspInterstitialViewer.DismissActionCallback {
                            override fun onDismiss() = callback.onClosed(isCompleted = true)
                        }
                    ).show(cancelable = false) {
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
    }

    override fun onPause() {
        printout.info(sourceName = sourceName, trace = { "onPause" })
    }

    override fun onDestroy() {
        printout.info(sourceName = sourceName, trace = { "onDestroy" })
        runCatching {
            ssp?.setNativeAdEventCallbackListener(null)
            ssp?.removeAllViews()
            ssp?.destroy()
            ssp = null
            weakActivity.clear()
        }.onFailure {
            printout.error(sourceName = sourceName, trace = { "onDestroy" }, throwable = it)
        }
    }

    override fun onNativeAdLoadSuccess() {
        weakActivity.isAvailable {
            ssp?.produce {
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

    override fun onNativeAdLoadFailed(error: SSPErrorCode?) {
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

    override fun onImpression() {
        weakActivity.isAvailable {
            ssp?.produce {
                callback.onOpened()
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

    override fun onClicked() {
        weakActivity.isAvailable {
            ssp?.produce {
                callback.onClicked()
            }
        }
    }

    override fun onAdHidden() {
        printout.info(sourceName = sourceName, trace = { "onAdHidden" })
        weakActivity.isAvailable {
            ssp?.produce {
                callback.onClosed(isCompleted = true)
            }
        }

    }
}