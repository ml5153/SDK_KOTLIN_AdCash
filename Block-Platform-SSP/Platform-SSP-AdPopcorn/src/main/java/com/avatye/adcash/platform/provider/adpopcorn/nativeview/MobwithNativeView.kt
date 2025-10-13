package com.avatye.adcash.platform.provider.adpopcorn.nativeview

import com.avatye.adcash.platform.provider.adpopcorn.R
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect.Mediation
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect.Size
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.MediationNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.MobwithNativeViewBinder

internal class MobwithNativeView : IMediationConnect {

    override val mediation: Mediation = Mediation.MOBWITH

    override fun requestNativeViewBinder(size: Size): MediationNativeViewBinder {
        return when (size) {
            // banner
            Size.W320XH50 -> {
                MediationNativeViewBinder.Builder().setMobwithNativeViewBinder(
                    nativeViewBinder = MobwithNativeViewBinder.Builder(
                        nativeAdLayoutId = R.layout.acb_adcash_archive_layout_native_mobwith_320x50,
                        nativeAdViewId = R.id.adcash_archive_native_view_mobwith_container_320x50
                    ).setMediaContainerViewId(viewId = R.id.adcash_archive_native_view_mobwith_image_320x50)
                        .setImageViewLogoId(viewId = R.id.adcash_archive_native_view_mobwith_icon_320x50)
                        .setTitleViewId(viewId = R.id.adcash_archive_native_view_mobwith_title_320x50)
                        .setDescriptionViewId(viewId = R.id.adcash_archive_native_view_mobwith_body_320x50)
                        .setImageViewInfoId(viewId = R.id.adcash_archive_native_view_mobwith_cta_320x50)
                        .build()
                ).build()
            }
            // banner
            Size.W320XH100,
            Size.DYNAMIC -> {
                MediationNativeViewBinder.Builder().setMobwithNativeViewBinder(
                    nativeViewBinder = MobwithNativeViewBinder.Builder(
                        nativeAdLayoutId = R.layout.acb_adcash_archive_layout_native_mobwith_320x100,
                        nativeAdViewId = R.id.adcash_archive_native_view_mobwith_container_320x100
                    ).setMediaContainerViewId(viewId = R.id.adcash_archive_native_view_mobwith_image_320x100)
                        .setImageViewLogoId(viewId = R.id.adcash_archive_native_view_mobwith_icon_320x100)
                        .setTitleViewId(viewId = R.id.adcash_archive_native_view_mobwith_title_320x100)
                        .setDescriptionViewId(viewId = R.id.adcash_archive_native_view_mobwith_body_320x100)
                        .setImageViewInfoId(viewId = R.id.adcash_archive_native_view_mobwith_cta_320x100)
                        .build()
                ).build()
            }
            // banner
            Size.W300XH250 -> {
                MediationNativeViewBinder.Builder().setMobwithNativeViewBinder(
                    nativeViewBinder = MobwithNativeViewBinder.Builder(
                        nativeAdLayoutId = R.layout.acb_adcash_archive_layout_native_mobwith_300x250,
                        nativeAdViewId = R.id.adcash_archive_native_view_mobwith_container_300x250
                    ).setMediaContainerViewId(viewId = R.id.adcash_archive_native_view_mobwith_image_300x250)
                        .setImageViewId(viewId = R.id.adcash_archive_native_view_mobwith_ad_image_300x250)
                        .setImageViewLogoId(viewId = R.id.adcash_archive_native_view_mobwith_icon_300x250)
                        .setTitleViewId(viewId = R.id.adcash_archive_native_view_mobwith_title_300x250)
                        .setDescriptionViewId(viewId = R.id.adcash_archive_native_view_mobwith_body_300x250)
                        .setButtonGoViewId(viewId = R.id.adcash_archive_native_view_mobwith_cta_300x250)
                        .setLayoutInfoViewId(viewId = R.id.adcash_archive_native_view_mobwith_logo_layout_300x250)
                        .setImageViewInfoId(viewId = R.id.adcash_archive_native_view_mobwith_logo_image_300x250)
                        .build()
                ).build()
            }
            // interstitial
            Size.W320XH480 -> {
                MediationNativeViewBinder.Builder().setMobwithNativeViewBinder(
                    nativeViewBinder = MobwithNativeViewBinder.Builder(
                        nativeAdLayoutId = R.layout.acb_adcash_archive_layout_native_mobwith_320x480,
                        nativeAdViewId = R.id.adcash_archive_native_view_mobwith_container_320x480
                    ).setMediaContainerViewId(viewId = R.id.adcash_archive_native_view_mobwith_image_320x480)
                        .setImageViewLogoId(viewId = R.id.adcash_archive_native_view_mobwith_icon_320x480)
                        .setTitleViewId(viewId = R.id.adcash_archive_native_view_mobwith_title_320x480)
                        .setDescriptionViewId(viewId = R.id.adcash_archive_native_view_mobwith_body_320x480)
                        .setImageViewInfoId(viewId = R.id.adcash_archive_native_view_mobwith_cta_320x480)
                        .build()
                ).build()
            }
        }
    }
}