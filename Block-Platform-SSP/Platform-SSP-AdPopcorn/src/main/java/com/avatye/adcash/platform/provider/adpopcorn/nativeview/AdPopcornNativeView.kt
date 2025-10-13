package com.avatye.adcash.platform.provider.adpopcorn.nativeview

import com.avatye.adcash.platform.provider.adpopcorn.R
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect.Mediation
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect.Size
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.MediationNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdPopcornNativeViewBinder

internal class AdPopcornNativeView : IMediationConnect {

    override val mediation: Mediation = Mediation.ADPOPCORN

    override fun requestNativeViewBinder(size: Size): MediationNativeViewBinder? {
        return when (size) {
            // banner
            Size.W320XH50 -> {
                MediationNativeViewBinder.Builder().setAdPopcornNativeViewBinder(
                    nativeViewBinder = AdPopcornNativeViewBinder.Builder(
                        nativeAdLayoutId = R.layout.acb_adcash_archive_layout_native_adpopcorn_320x50,
                        nativeAdViewId = R.id.ad_plus_native_view_adpopcorn_container_320x50
                    ).setIconImageViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_icon_320x50)
                        .setTitleViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_title_320x50)
                        .setDescViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_body_320x50)
                        .setCallToActionId(viewId = R.id.adcash_archive_native_view_adpopcorn_cta_320x50)
                        .build()
                ).build()
            }
            // banner
            Size.W320XH100,
            Size.DYNAMIC -> {
                MediationNativeViewBinder.Builder().setAdPopcornNativeViewBinder(
                    nativeViewBinder = AdPopcornNativeViewBinder.Builder(
                        nativeAdLayoutId = R.layout.acb_adcash_archive_layout_native_adpopcorn_320x100,
                        nativeAdViewId = R.id.adcash_archive_native_view_adpopcorn_container_320x100
                    ).setMainImageViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_image_320x100)
                        .setIconImageViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_icon_320x100)
                        .setTitleViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_title_320x100)
                        .setDescViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_body_320x100)
                        .setCallToActionId(viewId = R.id.adcash_archive_native_view_adpopcorn_cta_320x100)
                        .build()
                ).build()
            }
            // banner
            Size.W300XH250 -> {
                MediationNativeViewBinder.Builder().setAdPopcornNativeViewBinder(
                    nativeViewBinder = AdPopcornNativeViewBinder.Builder(
                        nativeAdLayoutId = R.layout.acb_adcash_archive_layout_native_adpopcorn_300x250,
                        nativeAdViewId = R.id.adcash_archive_native_view_adpopcorn_container_300x250
                    ).setMainImageViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_image_300x250)
                        .setIconImageViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_icon_300x250)
                        .setTitleViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_title_300x250)
                        .setDescViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_body_300x250)
                        .setCallToActionId(viewId = R.id.adcash_archive_native_view_adpopcorn_cta_300x250)
                        .build()
                ).build()
            }
            // interstitial
            Size.W320XH480 -> {
                MediationNativeViewBinder.Builder().setAdPopcornNativeViewBinder(
                    nativeViewBinder = AdPopcornNativeViewBinder.Builder(
                        nativeAdLayoutId = R.layout.acb_adcash_archive_layout_native_adpopcorn_320x480,
                        nativeAdViewId = R.id.adcash_archive_native_view_adpopcorn_container_320x480
                    ).setMainImageViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_image_320x480)
                        .setIconImageViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_icon_320x480)
                        .setTitleViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_title_320x480)
                        .setDescViewId(viewId = R.id.adcash_archive_native_view_adpopcorn_body_320x480)
                        .setCallToActionId(viewId = R.id.adcash_archive_native_view_adpopcorn_cta_320x480)
                        .build()
                ).build()
            }
        }
    }
}