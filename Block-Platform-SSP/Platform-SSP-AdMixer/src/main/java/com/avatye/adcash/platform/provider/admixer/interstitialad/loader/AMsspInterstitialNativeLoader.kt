package com.avatye.adcash.platform.provider.admixer.interstitialad.loader

import android.app.Activity
import android.view.LayoutInflater
import com.avatye.adcash.platform.library.extension.isAlive
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.let2
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.admixer.AMsspErrorUnit
import com.avatye.adcash.platform.provider.admixer.R
import com.avatye.adcash.platform.provider.admixer.Settings
import com.avatye.adcash.platform.provider.admixer.Settings.printout
import com.avatye.adcash.platform.provider.admixer.databinding.AcbAdcashSspAdmixerContainerInterstitialNative320x480Binding
import com.avatye.adcash.platform.provider.admixer.interstitialad.loader.viewer.AMsspInterstitialViewer
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit
import com.nasmedia.admixer.ads.AdEvent
import com.nasmedia.admixer.ads.AdInfo
import com.nasmedia.admixer.ads.AdListener
import com.nasmedia.admixer.ads.NativeAdView
import com.nasmedia.admixer.common.nativeads.NativeAdViewBinder
import java.lang.ref.WeakReference

internal class AMsspInterstitialNativeLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementId: String,
    private val callback: AMsspInterstitialLoaderCallback
) : AMsspInterstitialLoaderBase(), AdListener {

    private val sourceName = "AMsspInterstitialNativeLoader"
    private val weakActivity = WeakReference(activity)

    private var leakView: AcbAdcashSspAdmixerContainerInterstitialNative320x480Binding? = null
    override val loaderName: String get() = "InterstitialNativeLoader"
    override val interstitialUnit = AdsviserInterstitialUnit.INTERSTITIAL_NATIVE
    private var nativeAdView: NativeAdView? = null


    init {
        weakActivity.isAvailable { wActivity ->
            leakView = AcbAdcashSspAdmixerContainerInterstitialNative320x480Binding.inflate(
                LayoutInflater.from(wActivity))
        }
    }

    private fun initializer(blockCallback: () -> Unit) {
        weakActivity.isAvailable { wActivity ->
            if (nativeAdView == null) {
                nativeAdView = NativeAdView(wActivity)

                val adInfo: AdInfo = AdInfo.Builder(placementId)
                    .isRetry(false)
                    .build()

                val viewBinder = NativeAdViewBinder.Builder(R.layout.acb_adcash_ssp_admixer_container_interstitial_native_view_320x480)
                    .setIconImageId(R.id.admixer_banner_native_icon)
                    .setCtaId(R.id.admixer_banner_native_cta)
                    .setTitleId(R.id.admixer_banner_native_title)
                    .setDescriptionId(R.id.admixer_banner_native_description)
                    .setAdvertiserId(R.id.admixer_banner_native_advertiser)
                    .setMainViewId(R.id.admixer_banner_native_main)
                    .build()

                nativeAdView?.setAdInfo(adInfo)
                nativeAdView?.setViewBinder(viewBinder)
                nativeAdView?.setAdViewListener(this)

                blockCallback()
            }
        }
    }

    override fun requestLoad() {
        printout.info(sourceName = sourceName, trace = { "requestLoad" })
        weakActivity.isAvailable { wActivity ->
            Settings.initSSP(context = wActivity, appKey = placementAppKey, placementId = placementId) {
                initializer {
                    nativeAdView?.loadNativeAd() ?: run {
                        callback.onFailed(
                            error = AMsspErrorUnit.of(
                                errorUnit = AMsspErrorUnit.EXCEPTION_CONTEXT_IS_NULL
                            )
                        )
                    }
                }
            }
        }
    }

    override val isLoaded: Boolean
        get() {
            return (weakActivity.get()?.isAlive == true) && (nativeAdView?.hasAd ?: false)
        }

    override fun show(blockCallback: (success: Boolean) -> Unit) {
        printout.info(sourceName = sourceName, trace = { "show" })
        weakActivity.isAvailable {
            leakView?.produce { rootView ->
                if (isLoaded) {
                    AMsspInterstitialViewer.create(
                        activity = activity,
                        adView = rootView.root,
                        actionDismiss = object : AMsspInterstitialViewer.DismissActionCallback {
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
        nativeAdView?.onResume()
        nativeAdView = null
    }

    override fun onPause() {
        printout.info(sourceName = sourceName, trace = { "onPause" })
        nativeAdView?.onPause()
        nativeAdView = null
    }

    override fun onDestroy() {
        printout.info(sourceName = sourceName, trace = { "onDestroy" })
        runCatching {
            nativeAdView?.setAdViewListener(null)
            nativeAdView?.removeAllViews()
            nativeAdView?.onDestroy()
            nativeAdView = null
            weakActivity.clear()
        }.onFailure {
            printout.error(sourceName = sourceName, trace = { "onDestroy" }, throwable = it)
        }
    }

    override fun onReceivedAd(p0: Any?) {
        printout.info(sourceName = sourceName, trace = { "onReceivedAd" })
        weakActivity.isAvailable {
            let2(nativeAdView, leakView) { innerSSP, innerLeakView ->
                innerLeakView.interstitialNativeContent.addView(innerSSP)
                callback.onLoaded(unitType = interstitialUnit, networkUnitName = networkUnitName)
            } ?: run {
                callback.onFailed(
                    error = AMsspErrorUnit.of(
                        errorUnit = AMsspErrorUnit.EXCEPTION_LOADER_IS_NULL
                    )
                )
            }
        }
    }

    override fun onFailedToReceiveAd(p0: Any?, p1: Int, p2: String?) {
        printout.info(sourceName = sourceName, trace = { "onFailedToReceiveAd" })
        weakActivity.isAvailable {
            callback.onFailed(
                error = AMsspErrorUnit.of(errorCode = p1, errorMessage = p2)
            )
        }
    }

    override fun onEventAd(p0: Any?, p1: AdEvent?) {
        printout.info(sourceName = sourceName, trace = { "onEventAd" })
        when (p1) {
            AdEvent.CLICK -> {
                printout.info(sourceName = sourceName, trace = { "onEventAd: CLICK" })
                if (nativeAdView != null) {
                    weakActivity.isAvailable {
                        callback.onClicked()
                    }
                }
            }
            AdEvent.DISPLAYED -> {
                printout.info(sourceName = sourceName, trace = { "onEventAd: DISPLAYED" })
            }
            else -> {

            }
        }
    }
}