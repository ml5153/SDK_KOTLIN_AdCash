package com.avatye.adcash.platform.provider.adpopcorn.tools

import android.content.Context
import android.view.LayoutInflater
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdFitNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdMobNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdPopcornNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AppLovinMaxNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.BizBoardNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.FacebookNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.GAMNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.MobonNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.NamNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.PangleNativeViewBinder
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.adpopcorn.Settings.printout
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.MobwithNativeViewBinder
import com.igaworks.ssp.part.nativead.AdPopcornSSPNativeAd
import com.igaworks.ssp.part.nativead.binder.AdFitViewBinder
import com.igaworks.ssp.part.nativead.binder.AdMobViewBinder
import com.igaworks.ssp.part.nativead.binder.AdPopcornSSPViewBinder
import com.igaworks.ssp.part.nativead.binder.AppLovinMaxViewBinder
import com.igaworks.ssp.part.nativead.binder.FacebookViewBinder
import com.igaworks.ssp.part.nativead.binder.GAMViewBinder
import com.igaworks.ssp.part.nativead.binder.MobWithViewBinder
import com.igaworks.ssp.part.nativead.binder.MobonViewBinder
import com.igaworks.ssp.part.nativead.binder.NAMViewBinder
import com.igaworks.ssp.part.nativead.binder.PangleViewBinder

internal object APsspNativeAdTools {

    private const val SourceName = "APsspNativeAdTools"

    fun bannerBinderSize(apsspBannerUnitSize: AdsviserBannerUnitSize): IMediationConnect.Size {
        return when (apsspBannerUnitSize) {
            AdsviserBannerUnitSize.W320XH50 -> IMediationConnect.Size.W320XH50
            AdsviserBannerUnitSize.W320XH100 -> IMediationConnect.Size.W320XH100
            AdsviserBannerUnitSize.W300XH250 -> IMediationConnect.Size.W300XH250
            AdsviserBannerUnitSize.W320XH480 -> IMediationConnect.Size.W320XH480 // 여기서는 없는것이다.
            AdsviserBannerUnitSize.DYNAMIC -> IMediationConnect.Size.DYNAMIC
        }
    }

    fun makeAdFitViewBinder(
        context: Context,
        sspNativeAd: AdPopcornSSPNativeAd,
        nativeBinder: AdFitNativeViewBinder
    ): AdFitViewBinder? {
        return try {
            attachNativeMediationView(
                context = context,
                sspNativeAd = sspNativeAd,
                nativeAdLayoutId = nativeBinder.nativeAdLayoutId,
                nativeAdViewId = nativeBinder.nativeAdViewId
            )
            AdFitViewBinder.Builder(
                nativeBinder.nativeAdViewId,
                nativeBinder.nativeAdLayoutId
            ).apply {
                nativeBinder.titleViewId?.produce {
                    this.titleViewId(it)
                }
                nativeBinder.bodyViewId?.produce {
                    this.bodyViewId(it)
                }
                nativeBinder.callToActionButtonId?.produce {
                    this.callToActionButtonId(it)
                }
                nativeBinder.profileNameViewId?.produce {
                    this.profileNameViewId(it)
                }
                nativeBinder.profileIconViewId?.produce {
                    this.profileIconViewId(it)
                }
                nativeBinder.mediaViewId?.produce {
                    this.mediaViewId(it)
                }
                this.testMode(nativeBinder.useTestMode)
                this.bizBoardAd(false)
            }.build()
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "makeAdFitViewBinder::exception"
            }
            null
        }
    }

    fun makeAdMobViewBinder(
        context: Context,
        sspNativeAd: AdPopcornSSPNativeAd,
        nativeBinder: AdMobNativeViewBinder
    ): AdMobViewBinder? {
        return try {
            attachNativeMediationView(
                context = context,
                sspNativeAd = sspNativeAd,
                nativeAdLayoutId = nativeBinder.nativeAdLayoutId,
                nativeAdViewId = nativeBinder.nativeAdViewId
            )
            AdMobViewBinder.Builder(
                nativeBinder.nativeAdViewId,
                nativeBinder.nativeAdLayoutId
            ).apply {
                nativeBinder.mediaViewId?.produce {
                    this.mediaViewId(it)
                }
                nativeBinder.headlineViewId?.produce {
                    this.headlineViewId(it)
                }
                nativeBinder.bodyViewId?.produce {
                    this.bodyViewId(it)
                }
                nativeBinder.callToActionId?.produce {
                    this.callToActionId(it)
                }
                nativeBinder.iconViewId?.produce {
                    this.iconViewId(it)
                }
                nativeBinder.priceViewId?.produce {
                    this.priceViewId(it)
                }
                nativeBinder.starRatingViewId?.produce {
                    this.starRatingViewId(it)
                }
                nativeBinder.storeViewId?.produce {
                    this.storeViewId(it)
                }
                nativeBinder.advertiserViewId?.produce {
                    this.advertiserViewId(it)
                }
            }.build()
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "makeAdmobViewBinder::exception"
            }
            null
        }
    }


    fun makeAdPopcornViewBinder(
        context: Context,
        sspNativeAd: AdPopcornSSPNativeAd,
        nativeBinder: AdPopcornNativeViewBinder
    ): AdPopcornSSPViewBinder? {
        return try {
            attachNativeMediationView(
                context = context,
                sspNativeAd = sspNativeAd,
                nativeAdLayoutId = nativeBinder.nativeAdLayoutId,
                nativeAdViewId = nativeBinder.nativeAdViewId
            )
            AdPopcornSSPViewBinder.Builder(
                nativeBinder.nativeAdViewId,
                nativeBinder.nativeAdLayoutId
            ).apply {
                nativeBinder.titleViewId?.produce {
                    this.titleViewId(it)
                }
                nativeBinder.descViewId?.produce {
                    this.descViewId(it)
                }
                nativeBinder.mainImageViewId?.produce {
                    this.mainImageViewId(it)
                }
                nativeBinder.iconImageViewId?.produce {
                    this.iconImageViewId(it)
                }
                nativeBinder.callToActionId?.produce {
                    this.callToActionId(it)
                }
                this.privacyIconVisibility(nativeBinder.privacyIconVisibility)
                nativeBinder.privacyIconWidth?.produce {
                    this.privacyIconWidth(it)
                }
                nativeBinder.privacyIconHeight?.produce {
                    this.privacyIconHeight(it)
                }
                nativeBinder.privacyIconPosition?.produce {
                    this.privacyIconPosition(it)
                }
                nativeBinder.privacyIconLeftRightMargin?.produce {
                    this.privacyIconLeftRightMargin(it)
                }
                nativeBinder.privacyIconTopBottomMargin?.produce {
                    this.privacyIconTopBottomMargin(it)
                }
            }.build()
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "makeAdPopcornViewBinder::exception"
            }
            null
        }
    }

    fun makeAppLovinMaxViewBinder(
        context: Context,
        sspNativeAd: AdPopcornSSPNativeAd,
        nativeBinder: AppLovinMaxNativeViewBinder
    ): AppLovinMaxViewBinder? {
        return try {
            attachNativeMediationView(
                context = context,
                sspNativeAd = sspNativeAd,
                nativeAdLayoutId = nativeBinder.nativeAdLayoutId,
                nativeAdViewId = nativeBinder.nativeAdViewId
            )
            AppLovinMaxViewBinder.Builder(
                nativeBinder.nativeAdViewId,
                nativeBinder.nativeAdLayoutId
            ).apply {
                nativeBinder.titleViewId?.produce {
                    this.titleViewId(it)
                }
                nativeBinder.bodyViewId?.produce {
                    this.bodyViewId(it)
                }
                nativeBinder.advertiserViewId?.produce {
                    this.advertiserViewId(it)
                }
                nativeBinder.iconViewId?.produce {
                    this.iconViewId(it)
                }
                nativeBinder.mediaViewId?.produce {
                    this.mediaViewId(it)
                }
                nativeBinder.optionViewId?.produce {
                    this.optionViewId(it)
                }
                nativeBinder.ctaViewId?.produce {
                    this.ctaViewId(it)
                }
            }.build()
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "makeAppLovinMaxViewBinder::exception"
            }
            null
        }
    }

    fun makeBizBoardViewBinder(
        context: Context,
        sspNativeAd: AdPopcornSSPNativeAd,
        nativeBinder: BizBoardNativeViewBinder
    ): AdFitViewBinder? {
        return try {
            attachNativeMediationView(
                context = context,
                sspNativeAd = sspNativeAd,
                nativeAdLayoutId = nativeBinder.nativeAdLayoutId,
                nativeAdViewId = nativeBinder.nativeAdViewId
            )
            AdFitViewBinder.Builder(nativeBinder.nativeAdViewId)
                .bizBoardAd(true)
                .build()
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "makeBizBoardViewBinder::exception"
            }
            null
        }
    }

    fun makeFacebookViewBinder(
        context: Context,
        sspNativeAd: AdPopcornSSPNativeAd,
        nativeBinder: FacebookNativeViewBinder
    ): FacebookViewBinder? {
        return try {
            attachNativeMediationView(
                context = context,
                sspNativeAd = sspNativeAd,
                nativeAdLayoutId = nativeBinder.nativeAdLayoutId,
                nativeAdViewId = nativeBinder.nativeAdViewId
            )
            FacebookViewBinder.Builder(
                nativeBinder.nativeAdViewId,
                nativeBinder.nativeAdLayoutId
            ).apply {
                nativeBinder.titleId?.produce {
                    this.titleViewId(it)
                }
                nativeBinder.bodyId?.produce {
                    this.adBodyViewId(it)
                }
                nativeBinder.mediaViewId?.produce {
                    this.mediaViewId(it)
                }
                nativeBinder.adIconViewId?.produce {
                    this.adIconViewId(it)
                }
                nativeBinder.callToActionId?.produce {
                    this.callToActionId(it)
                }
                nativeBinder.adChoiceViewId?.produce {
                    this.adChoicesLayoutId(it)
                }
                nativeBinder.sponsoredViewId?.produce {
                    this.sponsoredViewId(it)
                }
                nativeBinder.socialContextViewId?.produce {
                    this.socialContextId(it)
                }
            }.build()
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "makeFanViewBinder::exception"
            }
            null
        }
    }

    fun makeGAMViewBinder(
        context: Context,
        sspNativeAd: AdPopcornSSPNativeAd,
        nativeBinder: GAMNativeViewBinder
    ): GAMViewBinder? {
        return try {
            attachNativeMediationView(
                context = context,
                sspNativeAd = sspNativeAd,
                nativeAdLayoutId = nativeBinder.nativeAdLayoutId,
                nativeAdViewId = nativeBinder.nativeAdViewId
            )
            GAMViewBinder.Builder(
                nativeBinder.nativeAdViewId,
                nativeBinder.nativeAdLayoutId
            ).apply {
                nativeBinder.mediaViewId?.produce {
                    this.mediaViewId(it)
                }
                nativeBinder.headlineViewId?.produce {
                    this.headlineViewId(it)
                }
                nativeBinder.bodyViewId?.produce {
                    this.bodyViewId(it)
                }
                nativeBinder.callToActionId?.produce {
                    this.callToActionId(it)
                }
                nativeBinder.iconViewId?.produce {
                    this.iconViewId(it)
                }
                nativeBinder.priceViewId?.produce {
                    this.priceViewId(it)
                }
                nativeBinder.starRatingViewId?.produce {
                    this.starRatingViewId(it)
                }
                nativeBinder.storeViewId?.produce {
                    this.storeViewId(it)
                }
                nativeBinder.advertiserViewId?.produce {
                    this.advertiserViewId(it)
                }
            }.build()
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "makeGAMViewBinder::exception"
            }
            null
        }
    }

    fun makeMobonViewBinder(
        context: Context,
        sspNativeAd: AdPopcornSSPNativeAd,
        nativeBinder: MobonNativeViewBinder
    ): MobonViewBinder? {
        return try {
            attachNativeMediationView(
                context = context,
                sspNativeAd = sspNativeAd,
                nativeAdLayoutId = nativeBinder.nativeAdLayoutId,
                nativeAdViewId = nativeBinder.nativeAdViewId
            )
            MobonViewBinder.Builder(
                nativeBinder.nativeAdViewId,
                nativeBinder.nativeAdLayoutId
            ).apply {
                nativeBinder.mainImageViewId?.produce {
                    this.mainImageViewId(it)
                }
                nativeBinder.logoImageViewId?.produce {
                    this.logoImageViewId(it)
                }
                nativeBinder.titleViewId?.produce {
                    this.titleViewId(it)
                }
                nativeBinder.descViewId?.produce {
                    this.descViewId(it)
                }
                nativeBinder.priceViewId?.produce {
                    this.priceViewId(it)
                }
            }.build()
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "makeMobonViewBinder::exception"
            }
            null
        }
    }

    fun makeNamViewBinder(
        context: Context,
        sspNativeAd: AdPopcornSSPNativeAd,
        nativeBinder: NamNativeViewBinder
    ): NAMViewBinder? {
        return try {
            attachNativeMediationView(
                context = context,
                sspNativeAd = sspNativeAd,
                nativeAdLayoutId = nativeBinder.nativeAdLayoutId,
                nativeAdViewId = nativeBinder.nativeAdViewId
            )
            NAMViewBinder.Builder(nativeBinder.nativeAdViewId)
                .build()
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "makeNamViewBinder::exception"
            }
            null
        }
    }

    fun makeMobwithViewBinder(
        context: Context,
        sspNativeAd: AdPopcornSSPNativeAd,
        nativeBinder: MobwithNativeViewBinder
    ): MobWithViewBinder? {
        return try {
            attachNativeMediationView(
                context = context,
                sspNativeAd = sspNativeAd,
                nativeAdLayoutId = nativeBinder.nativeAdLayoutId,
                nativeAdViewId = nativeBinder.nativeAdViewId
            )
            MobWithViewBinder.Builder(
                nativeBinder.nativeAdViewId,
                nativeBinder.nativeAdLayoutId
            ).apply {
                nativeBinder.mediaContainerViewId?.produce {
                    this.mediaContainerViewId(it)
                }
                nativeBinder.imageViewADId?.produce {
                    this.imageViewADId(it)
                }
                nativeBinder.imageViewLogoId?.produce {
                    this.imageViewLogoId(it)
                }
                nativeBinder.textViewTitleId?.produce {
                    this.textViewTitleId(it)
                }
                nativeBinder.textViewDescId?.produce {
                    this.textViewDescId(it)
                }
                nativeBinder.buttonGoId?.produce {
                    this.buttonGoId(it)
                }
                nativeBinder.layoutInfoViewId?.produce {
                    this.layoutInfoViewId(it)
                }
                nativeBinder.imageViewInfoId?.produce {
                    this.imageViewInfoId(it)
                }
            }.build()
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "makeMobwithViewBinder::exception"
            }
            null
        }
    }

    fun makePangleViewBinder(
        context: Context,
        sspNativeAd: AdPopcornSSPNativeAd,
        nativeBinder: PangleNativeViewBinder
    ): PangleViewBinder? {
        return try {
            attachNativeMediationView(
                context = context,
                sspNativeAd = sspNativeAd,
                nativeAdLayoutId = nativeBinder.nativeAdLayoutId,
                nativeAdViewId = nativeBinder.nativeAdViewId
            )
            PangleViewBinder.Builder(
                nativeBinder.nativeAdViewId,
                nativeBinder.nativeAdLayoutId
            ).apply {
                nativeBinder.mediaViewId?.produce {
                    this.mediaViewId(it)
                }
                nativeBinder.iconViewId?.produce {
                    this.iconViewId(it)
                }
                nativeBinder.titleViewId?.produce {
                    this.titleViewId(it)
                }
                nativeBinder.descriptionViewId?.produce {
                    this.descriptionViewId(it)
                }
                nativeBinder.creativeButtonViewId?.produce {
                    this.creativeButtonViewId(it)
                }
                nativeBinder.dislikeViewId?.produce {
                    this.dislikeViewId(it)
                }
                nativeBinder.logoViewId?.produce {
                    this.logoViewId(it)
                }
            }.build()
        } catch (e: Exception) {
            printout.error(sourceName = SourceName, throwable = e) {
                "makePangleViewBinder::exception"
            }
            null
        }
    }

    private fun attachNativeMediationView(
        context: Context,
        sspNativeAd: AdPopcornSSPNativeAd,
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