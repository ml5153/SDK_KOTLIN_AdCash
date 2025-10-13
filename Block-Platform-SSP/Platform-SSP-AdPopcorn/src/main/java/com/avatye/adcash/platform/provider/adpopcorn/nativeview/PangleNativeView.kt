package com.avatye.adcash.platform.provider.adpopcorn.nativeview

import com.avatye.adcash.platform.provider.adpopcorn.R
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect.Mediation
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect.Size
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.MediationNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.PangleNativeViewBinder

internal class PangleNativeView : IMediationConnect {

    override val mediation: Mediation = Mediation.PANGLE

    override fun requestNativeViewBinder(size: Size): MediationNativeViewBinder {
        return when (size) {
            // banner
            Size.W320XH50 -> {
                MediationNativeViewBinder.Builder().setPangleNativeViewBinder(
                    nativeViewBinder = PangleNativeViewBinder.Builder(
                        nativeAdLayoutId = R.layout.acb_adcash_archive_layout_native_pangle_320x50,
                        nativeAdViewId = R.id.adcash_archive_native_view_pangle_container_320x50
                    ).setMediaViewId(viewId = R.id.adcash_archive_native_view_pangle_image_320x50)
                        .setIconViewId(viewId = R.id.adcash_archive_native_view_pangle_icon_320x50)
                        .setTitleViewId(viewId = R.id.adcash_archive_native_view_pangle_title_320x50)
                        .setDescriptionViewId(viewId = R.id.adcash_archive_native_view_pangle_body_320x50)
                        .setCreativeButtonViewId(viewId = R.id.adcash_archive_native_view_pangle_cta_320x50)
                        .build()
                ).build()
            }
            // banner
            Size.W320XH100,
            Size.DYNAMIC -> {
                MediationNativeViewBinder.Builder().setPangleNativeViewBinder(
                    nativeViewBinder = PangleNativeViewBinder.Builder(
                        nativeAdLayoutId = R.layout.acb_adcash_archive_layout_native_pangle_320x100,
                        nativeAdViewId = R.id.adcash_archive_native_view_pangle_container_320x100
                    ).setMediaViewId(viewId = R.id.adcash_archive_native_view_pangle_image_320x100)
                        .setIconViewId(viewId = R.id.adcash_archive_native_view_pangle_icon_320x100)
                        .setTitleViewId(viewId = R.id.adcash_archive_native_view_pangle_title_320x100)
                        .setDescriptionViewId(viewId = R.id.adcash_archive_native_view_pangle_body_320x100)
                        .setCreativeButtonViewId(viewId = R.id.adcash_archive_native_view_pangle_cta_320x100)
                        .build()
                ).build()
            }
            // banner
            Size.W300XH250 -> {
                MediationNativeViewBinder.Builder().setPangleNativeViewBinder(
                    nativeViewBinder = PangleNativeViewBinder.Builder(
                        nativeAdLayoutId = R.layout.acb_adcash_archive_layout_native_pangle_300x250,
                        nativeAdViewId = R.id.adcash_archive_native_view_pangle_container_300x250
                    ).setMediaViewId(viewId = R.id.adcash_archive_native_view_pangle_image_300x250)
                        .setIconViewId(viewId = R.id.adcash_archive_native_view_pangle_icon_300x250)
                        .setTitleViewId(viewId = R.id.adcash_archive_native_view_pangle_title_300x250)
                        .setDescriptionViewId(viewId = R.id.adcash_archive_native_view_pangle_body_300x250)
                        .setCreativeButtonViewId(viewId = R.id.adcash_archive_native_view_pangle_cta_300x250)
                        .build()
                ).build()
            }
            // interstitial
            Size.W320XH480 -> {
                MediationNativeViewBinder.Builder().setPangleNativeViewBinder(
                    nativeViewBinder = PangleNativeViewBinder.Builder(
                        nativeAdLayoutId = R.layout.acb_adcash_archive_layout_native_pangle_320x480,
                        nativeAdViewId = R.id.adcash_archive_native_view_pangle_container_320x480
                    ).setMediaViewId(viewId = R.id.adcash_archive_native_view_pangle_image_320x480)
                        .setIconViewId(viewId = R.id.adcash_archive_native_view_pangle_icon_320x480)
                        .setTitleViewId(viewId = R.id.adcash_archive_native_view_pangle_title_320x480)
                        .setDescriptionViewId(viewId = R.id.adcash_archive_native_view_pangle_body_320x480)
                        .setCreativeButtonViewId(viewId = R.id.adcash_archive_native_view_pangle_cta_320x480)
                        .build()
                ).build()
            }
        }
    }
}