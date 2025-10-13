package com.avatye.adcash.platform.provider.admixer.bannerad.loader

import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.admixer.AMsspErrorUnit
import com.avatye.adcash.platform.provider.admixer.Settings
import com.avatye.adcash.platform.provider.admixer.Settings.printout
import com.avatye.adcash.platform.provider.basement.AdsviserProviderUnit
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize
import com.nasmedia.admixer.ads.AdEvent
import com.nasmedia.admixer.ads.AdInfo
import com.nasmedia.admixer.ads.AdListener
import com.nasmedia.admixer.ads.AdView
import java.lang.ref.WeakReference

internal class AMsspBannerLoader(
    private val context: Context,
    private val placementAppKey: String,
    private val placementId: String,
    private val placementSize: AdsviserBannerUnitSize,
    private val callback: AMsspBannerLoaderCallback
) : AMsspBannerLoaderBase(), AdListener {

    private val sourceName = "AMsspBannerLoader"
    private val weakContext = WeakReference(context)

    override val loaderName: String get() = "BannerLoader"
    override val bannerUnitSize: AdsviserBannerUnitSize get() = placementSize

    private var banner: AdView? = null

    private fun initializer(blockCallback: () -> Unit) {
        weakContext.get()?.produce {
            if (banner == null) {
                val hasAdSize = when(placementSize) {
                    AdsviserBannerUnitSize.W320XH50,
                    AdsviserBannerUnitSize.W320XH100,
                    AdsviserBannerUnitSize.W300XH250,
                    AdsviserBannerUnitSize.W320XH480 -> {
                        true
                    }
                    else -> {
                        false
                    }
                }

                if (hasAdSize) {
                    val adInfo: AdInfo = AdInfo.Builder(placementId)
                        .isRetry(false)
                        .build()

                    val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    banner = AdView(it)

                    banner?.layoutParams = params
                    banner?.setAdInfo(adInfo)
                    banner?.setAlwaysShowAdView(true)
                    banner?.setAdViewListener(this)

                    blockCallback()
                } else {
                    callback.onFailed(
                        error = AMsspErrorUnit.of(errorUnit = AMsspErrorUnit.BLOCKED_SIZE)
                    )
                }
            }
        }
    }

    override fun requestLoad() {
        printout.info(sourceName = sourceName, trace = { "requestLoad -> placementId: $placementId" })
        weakContext.isAvailable { wContext ->
            Settings.initSSP(context = wContext, appKey = placementAppKey, placementId = placementId) {
                initializer {
                    banner?.loadAd() ?: run {
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
        banner?.onResume()
        banner = null
    }

    override fun onPause() {
        printout.info(sourceName = sourceName) { "onPause" }
        banner?.onPause()
        banner = null
    }

    override fun onDestroy() {
        printout.info(sourceName = sourceName, trace = { "onDestroy" })
        runCatching {
            banner?.onPause()
            banner?.setAdViewListener(null)
            banner?.removeAllViews()
            banner?.onDestroy()
            banner = null
            weakContext.clear()
        }.onFailure {
            printout.error(sourceName = sourceName, throwable = it)
        }
    }

    override fun onReceivedAd(p0: Any?) {
        printout.info(sourceName = sourceName, trace = { "onReceivedAd" })
        weakContext.isAvailable {
            banner?.produce {
                it.showAd()
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
                if (banner != null) {
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