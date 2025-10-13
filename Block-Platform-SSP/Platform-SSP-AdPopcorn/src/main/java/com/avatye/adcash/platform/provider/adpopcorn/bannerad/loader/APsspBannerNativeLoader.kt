package com.avatye.adcash.platform.provider.adpopcorn.bannerad.loader

import android.content.Context
import android.view.LayoutInflater
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect
import com.avatye.adcash.platform.provider.adpopcorn.MediationConnector
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.adpopcorn.APsspErrorUnit
import com.avatye.adcash.platform.provider.adpopcorn.R
import com.avatye.adcash.platform.provider.adpopcorn.Settings
import com.avatye.adcash.platform.provider.adpopcorn.Settings.printout
import com.avatye.adcash.platform.provider.adpopcorn.Settings.verifyBlocked
import com.avatye.adcash.platform.provider.adpopcorn.tools.APsspNativeAdTools
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.nativead.AdPopcornSSPNativeAd
import com.igaworks.ssp.part.nativead.listener.INativeAdEventCallbackListener
import java.lang.ref.WeakReference

internal class APsspBannerNativeLoader(
    private val context: Context,
    private val placementAppKey: String,
    private val placementId: String,
    private val placementSize: AdsviserBannerUnitSize,
    private val callback: APsspBannerLoaderCallback
) : APsspBannerLoaderBase(), INativeAdEventCallbackListener {

    private val sourceName = "APsspBannerNativeLoader"
    private val weakContext = WeakReference(context)
    override val networkUnitNum: Int get() = ssp?.currentNetwork ?: -1
    override val loaderName: String get() = "BannerNativeLoader"
    override val bannerUnitSize: AdsviserBannerUnitSize get() = placementSize

    private var ssp: AdPopcornSSPNativeAd? = null

    private fun initializer(blockCallback: () -> Unit) {
        printout.info(sourceName = sourceName, trace = { "initializer" })
        weakContext.isAvailable { wContext ->
            if (ssp == null) {
                LayoutInflater.from(wContext).inflate(R.layout.acb_adcash_ssp_adpopcorn_container_banner_native, null)?.produce { iflView ->
                    ssp = iflView.findViewById<AdPopcornSSPNativeAd>(R.id.ssp_native_adview).apply {
                        // callback
                        placementId = this@APsspBannerNativeLoader.placementId
                        setPlacementAppKey(this@APsspBannerNativeLoader.placementAppKey)
                        setNativeAdEventCallbackListener(this@APsspBannerNativeLoader)


                        // native-adpopcorn
                        MediationConnector.requestNativeViewBinder(
                            context= wContext,
                            mediation = IMediationConnect.Mediation.ADPOPCORN,
                            size = APsspNativeAdTools.bannerBinderSize(apsspBannerUnitSize = placementSize)
                        )?.produce { adPlusNativeViewBinder ->
                            adPlusNativeViewBinder.adPopcornNativeViewBinder?.produce { adpopcorn ->
                                APsspNativeAdTools.makeAdPopcornViewBinder(
                                    context = context,
                                    sspNativeAd = this,
                                    nativeBinder = adpopcorn
                                )?.produce {
                                    this.adPopcornSSPViewBinder = it
                                }
                            }
                        }
                        // native-mobwith
                        MediationConnector.requestNativeViewBinder(
                            context= wContext,
                            mediation = IMediationConnect.Mediation.MOBWITH,
                            size = APsspNativeAdTools.bannerBinderSize(apsspBannerUnitSize = placementSize)
                        )?.produce { adPlusNativeViewBinder ->
                            adPlusNativeViewBinder.mobwithNativeViewBinder?.produce { mobwith ->
                                APsspNativeAdTools.makeMobwithViewBinder(
                                    context = context,
                                    sspNativeAd = this,
                                    nativeBinder = mobwith
                                )?.produce {
                                    this.mobWithViewBinder = it
                                }
                            }
                        }
                        // native-pangle
                        MediationConnector.requestNativeViewBinder(
                            context= wContext,
                            mediation = IMediationConnect.Mediation.PANGLE,
                            size = APsspNativeAdTools.bannerBinderSize(apsspBannerUnitSize = placementSize)
                        )?.produce { adPlusNativeViewBinder ->
                            adPlusNativeViewBinder.pangleNativeViewBinder?.produce { pangle ->
                                APsspNativeAdTools.makePangleViewBinder(
                                    context = context,
                                    sspNativeAd = this,
                                    nativeBinder = pangle
                                )?.produce {
                                    this.pangleViewBinder = it
                                }
                            }
                        }
                        // native-nam
                        MediationConnector.requestNativeViewBinder(
                            context= wContext,
                            mediation = IMediationConnect.Mediation.NAM,
                            size = APsspNativeAdTools.bannerBinderSize(apsspBannerUnitSize = placementSize)
                        )?.produce { adPlusNativeViewBinder ->
                            adPlusNativeViewBinder.namNativeViewBinder?.produce { nam ->
                                APsspNativeAdTools.makeNamViewBinder(
                                    context = context,
                                    sspNativeAd = this,
                                    nativeBinder = nam
                                )?.produce {
                                    this.namViewBinder = it
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
        weakContext.isAvailable { wContext ->
            Settings.initSSP(context = wContext, appKey = placementAppKey, placementId = placementId) {
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
            weakContext.clear()
        }.onFailure {
            printout.error(sourceName = sourceName, trace = { "onDestroy" }, throwable = it)
        }
    }

    override fun onNativeAdLoadSuccess() {
        printout.info(sourceName = sourceName, trace = { "onNativeAdLoadSuccess { networkUnitNum: $networkUnitNum }" })
        weakContext.isAvailable {
            ssp?.produce {
                callback.onLoaded(view = it, networkUnitName = networkUnitName)
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
        printout.info(sourceName = sourceName, trace = { "onNativeAdLoadFailed { error: ${error?.errorMessage} }" })
        weakContext.isAvailable {
            if (ssp != null) {
                val verifyBlock = verifyBlocked(error)
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

    override fun onImpression() {
        printout.info(sourceName = sourceName, trace = { "onImpression" })
    }

    override fun onClicked() {
        printout.info(sourceName = sourceName, trace = { "onClicked" })
        weakContext.isAvailable {
            callback.onClicked()
        }
    }

    override fun onAdHidden() {
        printout.info(sourceName = sourceName, trace = { "onAdHidden" })
    }

}