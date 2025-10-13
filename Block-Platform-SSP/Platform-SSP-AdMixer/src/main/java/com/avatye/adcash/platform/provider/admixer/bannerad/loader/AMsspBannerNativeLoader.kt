package com.avatye.adcash.platform.provider.admixer.bannerad.loader

import android.content.Context
import android.view.ViewGroup
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.admixer.AMsspErrorUnit
import com.avatye.adcash.platform.provider.admixer.R
import com.avatye.adcash.platform.provider.admixer.Settings
import com.avatye.adcash.platform.provider.admixer.Settings.printout
import com.avatye.adcash.platform.provider.admixer.tools.AMsspNativeAdTools
import com.avatye.adcash.platform.provider.basement.AdsviserProviderUnit
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect
import com.nasmedia.admixer.ads.AdEvent
import com.nasmedia.admixer.ads.AdInfo
import com.nasmedia.admixer.ads.AdListener
import com.nasmedia.admixer.ads.NativeAdView
import com.nasmedia.admixer.common.nativeads.NativeAdViewBinder
import java.lang.ref.WeakReference

internal class AMsspBannerNativeLoader(
    private val context: Context,
    private val placementAppKey: String,
    private val placementId: String,
    private val placementSize: AdsviserBannerUnitSize,
    private val callback: AMsspBannerLoaderCallback
) : AMsspBannerLoaderBase(), AdListener {

    private val sourceName = "AMsspBannerNativeLoader"
    private val weakContext = WeakReference(context)

    override val loaderName: String get() = "BannerNativeLoader"
    override val bannerUnitSize: AdsviserBannerUnitSize get() = placementSize

    private var nativeAdView: NativeAdView? = null

    private fun initializer(blockCallback: () -> Unit) {
        weakContext.isAvailable { wContext ->
            AMsspNativeAdTools.bannerBinderSize(placementSize).produce { size ->
                val adInfo: AdInfo = AdInfo.Builder(placementId)
                    .isRetry(false)
                    .build()

                val viewBinder: NativeAdViewBinder = when (size) {
                    IMediationConnect.Size.W320XH50 -> {
                        NativeAdViewBinder.Builder(R.layout.acb_adcash_ssp_admixer_container_banner_native_320x50)
                            .setIconImageId(R.id.admixer_banner_native_icon)
                            .setCtaId(R.id.admixer_banner_native_cta)
                            .setTitleId(R.id.admixer_banner_native_title)
                            .setAdvertiserId(R.id.admixer_banner_native_advertiser)
                            .setDescriptionId(R.id.admixer_banner_native_description)
                            .build()
                    }

                    IMediationConnect.Size.DYNAMIC,
                    IMediationConnect.Size.W320XH100 -> {
                        NativeAdViewBinder.Builder(R.layout.acb_adcash_ssp_admixer_container_banner_native_320x100)
                            .setIconImageId(R.id.admixer_banner_native_icon)
                            .setAdvertiserId(R.id.admixer_banner_native_advertiser)
                            .setDescriptionId(R.id.admixer_banner_native_description)
                            .setTitleId(R.id.admixer_banner_native_title)
                            .setCtaId(R.id.admixer_banner_native_cta)
                            .setMainViewId(R.id.admixer_banner_native_main)
                            .build()
                    }

                    IMediationConnect.Size.W300XH250 -> {
                        NativeAdViewBinder.Builder(R.layout.acb_adcash_ssp_admixer_container_banner_native_300x250)
                            .setIconImageId(R.id.admixer_banner_native_icon)
                            .setCtaId(R.id.admixer_banner_native_cta)
                            .setTitleId(R.id.admixer_banner_native_title)
                            .setAdvertiserId(R.id.admixer_banner_native_advertiser)
                            .setDescriptionId(R.id.admixer_banner_native_description)
                            .setMainViewId(R.id.admixer_banner_native_main)
                            .build()
                    }

                    IMediationConnect.Size.W320XH480 -> {
                        NativeAdViewBinder.Builder(R.layout.acb_adcash_ssp_admixer_container_banner_native_320x480)
                            .setIconImageId(R.id.admixer_banner_native_icon)
                            .setCtaId(R.id.admixer_banner_native_cta)
                            .setTitleId(R.id.admixer_banner_native_title)
                            .setDescriptionId(R.id.admixer_banner_native_description)
                            .setAdvertiserId(R.id.admixer_banner_native_advertiser)
                            .setMainViewId(R.id.admixer_banner_native_main)
                            .build()
                    }
                }

                nativeAdView = NativeAdView(wContext)
                nativeAdView?.setAdInfo(adInfo)
                nativeAdView?.setViewBinder(viewBinder)
                nativeAdView?.setAdViewListener(this)

                blockCallback()
            }
        }
    }

    override fun requestLoad() {
        printout.info(sourceName = sourceName, trace = { "requestLoad -> placementId: $placementId" })
        weakContext.isAvailable { wContext ->
            Settings.initSSP(context = wContext, appKey = placementAppKey, placementId = placementId) {
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

    override fun onResume() {
        printout.info(sourceName = sourceName) { "onResume" }
        nativeAdView?.onResume()
        nativeAdView = null
    }

    override fun onPause() {
        printout.info(sourceName = sourceName) { "onPause" }
        nativeAdView?.onPause()
        nativeAdView = null
    }

    override fun onDestroy() {
        printout.info(sourceName = sourceName, trace = { "onDestroy" })
        runCatching {
            nativeAdView?.onPause()
            nativeAdView?.setAdViewListener(null)
            nativeAdView?.removeAllViews()
            nativeAdView?.onDestroy()
            nativeAdView = null
            weakContext.clear()
        }.onFailure {
            printout.error(sourceName = sourceName, trace = { "onDestroy" }, throwable = it)
        }
    }

    override fun onReceivedAd(p0: Any?) {
        printout.info(sourceName = sourceName, trace = { "onReceivedAd" })
        weakContext.isAvailable {
            nativeAdView?.produce {
                callback.onLoaded(view = it as ViewGroup, networkUnitName = AdsviserProviderUnit.ADMIXER.providerName)
            } ?: run {
                callback.onFailed(error = AMsspErrorUnit.of(errorUnit = AMsspErrorUnit.EXCEPTION_LOADER_IS_NULL))
            }
        }
    }

    override fun onFailedToReceiveAd(p0: Any?, p1: Int, p2: String?) {
        printout.info(sourceName = sourceName, trace = { "onFailedToReceiveAd -> p1:$p1, p2: $p2" })
        weakContext.isAvailable {
            callback.onFailed(error = AMsspErrorUnit.of(errorCode = p1, errorMessage = p2))
        }
    }

    override fun onEventAd(p0: Any?, p1: AdEvent?) {
        printout.info(sourceName = sourceName, trace = { "onEventAd -> AdEvent: $p1" })
        when (p1) {
            AdEvent.CLICK -> {
                if (nativeAdView != null) {
                    weakContext.isAvailable {
                        callback.onClicked()
                    }
                }
            }

            AdEvent.DISPLAYED -> {}
            else -> {}
        }
    }
}