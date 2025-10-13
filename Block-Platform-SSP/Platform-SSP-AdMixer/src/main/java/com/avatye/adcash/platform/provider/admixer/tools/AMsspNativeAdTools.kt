package com.avatye.adcash.platform.provider.admixer.tools

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.admixer.Settings.printout
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdmixerNativeViewBinder
import com.nasmedia.admixer.common.nativeads.NativeAdViewBinder

internal object AMsspNativeAdTools {

    private const val SourceName = "AMsspNativeAdTools"

    fun bannerBinderSize(amsspBannerUnitSize: AdsviserBannerUnitSize): IMediationConnect.Size {
        return when (amsspBannerUnitSize) {
            AdsviserBannerUnitSize.W320XH50 -> IMediationConnect.Size.W320XH50
            AdsviserBannerUnitSize.W320XH100 -> IMediationConnect.Size.W320XH100
            AdsviserBannerUnitSize.W300XH250 -> IMediationConnect.Size.W300XH250
            AdsviserBannerUnitSize.W320XH480 -> IMediationConnect.Size.W320XH480
            AdsviserBannerUnitSize.DYNAMIC -> IMediationConnect.Size.DYNAMIC
        }
    }

    fun makeAdmixerViewBinder(
        context: Context,
        sspNativeAd: FrameLayout,
        nativeBinder: AdmixerNativeViewBinder
    ): NativeAdViewBinder? {

        attachNativeMediationView(
            context = context,
            sspNativeAd = sspNativeAd,
            nativeAdLayoutId = nativeBinder.nativeAdLayoutId,
            nativeAdViewId = nativeBinder.nativeAdViewId
        )

        return try {
            NativeAdViewBinder.Builder(nativeBinder.nativeAdLayoutId).apply {
                nativeBinder.iconImageId?.produce {
                    this.setIconImageId(it)
                }
                nativeBinder.ctaId?.produce {
                    this.setCtaId(it)
                }
                nativeBinder.titleId?.produce {
                    this.setTitleId(it)
                }
                nativeBinder.descriptionId?.produce {
                    this.setDescriptionId(it)
                }
                nativeBinder.advertiserId?.produce {
                    this.setAdvertiserId(it)
                }
                nativeBinder.mainViewId?.produce {
                    this.setMainViewId(it)
                }
            }.build()
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "makeAdmixerViewBinder::exception"
            }
            null
        }
    }

    private fun attachNativeMediationView(
        context: Context,
        sspNativeAd: FrameLayout,
        nativeAdLayoutId: Int,
        nativeAdViewId: Int
    ) {
        for (i in 0 until sspNativeAd.childCount) {
            val currentView = sspNativeAd.getChildAt(i)
            if (currentView.id == nativeAdViewId) {
                sspNativeAd.removeView(currentView)
            }
        }
        val attachNativeView = LayoutInflater.from(context).inflate(nativeAdLayoutId, null, false)
        sspNativeAd.addView(attachNativeView)
    }

}