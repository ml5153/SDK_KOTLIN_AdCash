package com.avatye.adcash.platform.provider.adpopcorn

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.avatye.adcash.platform.library.printout.PrintOut
import com.igaworks.ssp.AdPopcornSSP
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.SdkInitListener

internal object Settings {

    const val adsviserName = "APSSP"
    private const val sourceName = "ApsspSettings"

    // logger
    val printout by lazy { PrintOut(moduleName = "ADCASH:Platform:SSP:AdPopcorn") }

    const val RefreshTime = -1
    const val NetworkScheduleTimeout = 8
    const val VideoNetworkScheduleTimeout = 8

    val interstitialBackgroundColor get() = innerInterstitialBackgroundColor


    private var innerInterstitialBackgroundColor = Color.parseColor("#52000000")

    init {
        verifyAdvertiseMediationImport()
    }

    // region { advertise ssp module init - async }
    fun initSSP(context: Context, appKey: String, placementId: String, block: () -> Unit) {
        printout.info { "initSSP => appKey: $appKey, placementId: $placementId" }
        if (!AdPopcornSSP.isInitialized(context.applicationContext)) {
            /*AdPopcornSSP.init(context.applicationContext, appKey) {
                kotlin.runCatching {
                    innerInterstitialBackgroundColor =
                        ContextCompat.getColor(context, R.color.acb_adcash_ssp_adpopcorn_color_interstitial_background)
                }.onFailure {
                    printout.error(throwable = it, sourceName = sourceName, trace = { "initSSP" })
                }
                block()
            }*/

            AdPopcornSSP.init(context, appKey, object : SdkInitListener {
                override fun onInitializationFinished() {
                    kotlin.runCatching {
                        innerInterstitialBackgroundColor =
                            ContextCompat.getColor(context, R.color.acb_adcash_ssp_adpopcorn_color_interstitial_background)
                    }.onFailure {
                        printout.error(throwable = it, sourceName = sourceName, trace = { "initSSP" })
                    }
                    block()
                }
            })
        } else {
            block()
        }
    }

    fun release(context: Context) {
        if (AdPopcornSSP.isInitialized(context.applicationContext)) {
            kotlin.runCatching {
                AdPopcornSSP.destroy()
            }.onFailure {
                printout.error(
                    throwable = it,
                    sourceName = sourceName,
                    trace = { "release::destroy" }
                )
            }
        }
    }
    // endregion

    enum class MediationImportType(val importClassName: String) {
        // used
        APPLOVIN("com.applovin.sdk.AppLovinSdk"),
        FACEBOOK("com.facebook.ads.AdListener"),
        MEZZOMEDIA("com.mmc.man.AdListener"),
        MOBON("com.mobon.sdk.MobonSDK"),
        PANGLE("com.bytedance.sdk.openadsdk.TTAdSdk"),
        UNITY("com.unity3d.ads.UnityAds"),
        VUNGLE("com.vungle.warren.LoadAdCallback"),
        COUPANG("com.coupang.ads.CoupangAds"),
        ADFIT("com.kakao.adfit.AdFitSdk"),
        CAULY("com.fsn.cauly.CaulyAdViewListener");
        /* not used
        ADCOLONY("com.adcolony.sdk.AdColonyRewardListener"),
        ADMOB("com.google.android.gms.ads.AdListener"),
        FYBER("com.fyber.inneractive.sdk.external.InneractiveAdSpot.RequestListener"),
        MINTEGRAL("com.mintegral.msdk.out.RewardVideoListener"),
        MOPUB("com.mopub.mobileads.MoPubView.BannerAdListener"),
        TABJOY("com.tapjoy.TJConnectListener")
        */
    }

    /* valid advertise mediation class */
    private fun verifyAdvertiseMediationImport() {
        if (printout.allowLog) {
            MediationImportType.values().forEach { map ->
                kotlin.runCatching {
                    Class.forName(map.importClassName)
                }.onSuccess {
                    println("########## ADN SDK [${map.name}: ${map.importClassName}] ##########")
                }
            }
        }
    }

    fun verifyBlocked(sspErrorCode: SSPErrorCode?): Boolean {
        return (sspErrorCode?.errorCode ?: 0) == SSPErrorCode.UNKNOWN_SERVER_ERROR
    }

    fun verifyMediationImport(importType: MediationImportType): Boolean {
        return try {
            Class.forName(importType.importClassName)
            true
        } catch (e: Exception) {
            printout.info(sourceName = sourceName) {
                "${importType.name}(${importType.importClassName}) is not imported !!!"
            }
            false
        }
    }
}