package com.avatye.adcash.platform.provider.adpopcorn.bannerad.loader

import android.content.Context
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.adpopcorn.APsspErrorUnit
import com.avatye.adcash.platform.provider.adpopcorn.APsspNetworkUnit
import com.avatye.adcash.platform.provider.adpopcorn.Settings
import com.avatye.adcash.platform.provider.adpopcorn.Settings.printout
import com.avatye.adcash.platform.provider.adpopcorn.Settings.verifyBlocked
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize
import com.igaworks.ssp.AdSize
import com.igaworks.ssp.BannerAnimType
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.banner.AdPopcornSSPBannerAd
import com.igaworks.ssp.part.banner.listener.IBannerEventCallbackListener
import java.lang.ref.WeakReference

internal class APsspBannerLoader(
    private val context: Context,
    private val placementAppKey: String,
    private val placementId: String,
    private val placementSize: AdsviserBannerUnitSize,
    private val mediationExtra: HashMap<String, Any>? = null,
    private val storeUrls: String,
    private val callback: APsspBannerLoaderCallback
) : APsspBannerLoaderBase(), IBannerEventCallbackListener {

    private val sourceName = "APsspBannerLoader"
    private val weakContext = WeakReference(context)

    override val loaderName: String get() = "BannerLoader"
    override val networkUnitNum: Int get() = ssp?.currentNetwork ?: -1
    override val bannerUnitSize: AdsviserBannerUnitSize get() = placementSize

    private var ssp: AdPopcornSSPBannerAd? = null

    private fun initializer(blockCallback: () -> Unit) {
        weakContext.get()?.produce { wContext ->
            if (ssp == null) {
                ssp = AdPopcornSSPBannerAd(wContext).apply {
                    this.autoBgColor = false
                    this.placementId = this@APsspBannerLoader.placementId
                    this.setPlacementAppKey(placementAppKey)
                    this.setBannerAnimType(BannerAnimType.NONE)
                    this.setRefreshTime(Settings.RefreshTime)
                    this.setNetworkScheduleTimeout(Settings.NetworkScheduleTimeout)
                    this.setBannerEventCallbackListener(this@APsspBannerLoader)
                    this.setMediationExtras(configMediationExtraData(extraData = mediationExtra))
                }
            }

            val adSize: AdSize? = when (placementSize) {
                AdsviserBannerUnitSize.W320XH50 -> AdSize.BANNER_320x50
                AdsviserBannerUnitSize.W320XH100 -> AdSize.BANNER_320x100
                AdsviserBannerUnitSize.W300XH250 -> AdSize.BANNER_300x250
                AdsviserBannerUnitSize.DYNAMIC -> AdSize.BANNER_ADAPTIVE_SIZE
                else -> null
            }

            if (adSize != null) {
                ssp?.setAdSize(adSize)
                blockCallback()
            } else {
                callback.onFailed(
                    error = APsspErrorUnit.of(
                        errorUnit = APsspErrorUnit.BLOCKED_SIZE,
                        networkUnit = networkUnit
                    )
                )
            }
        }
    }

    private fun configMediationExtraData(extraData: HashMap<String, Any>? = null): HashMap<String, Any> {
        val hash = HashMap<String, Any>()
        extraData?.forEach {
            hash[it.key] = it.value
        }
        /**
         * MEZZO_AGE_LEVEL
         * Adsviser -> AgeVerifier -> true 경우에만 BannerLoader를 호출 한다.
         * MEZZO_AGE_LEVEL은 언제나 허용 상태인 1값을 가진다.
         */
        weakContext.get()?.produce {
            if (storeUrls.isNotEmpty()) {
                if (hash[AdPopcornSSPBannerAd.MediationExtraData.MEZZO_STORE_URL] == null) {
                    hash[AdPopcornSSPBannerAd.MediationExtraData.MEZZO_STORE_URL] = storeUrls
                }
                if (hash[AdPopcornSSPBannerAd.MediationExtraData.MEZZO_IS_USED_BACKGROUND_CHECK] == null) {
                    hash[AdPopcornSSPBannerAd.MediationExtraData.MEZZO_IS_USED_BACKGROUND_CHECK] = false
                }
                if (hash[AdPopcornSSPBannerAd.MediationExtraData.MEZZO_AGE_LEVEL] == null) {
                    hash[AdPopcornSSPBannerAd.MediationExtraData.MEZZO_AGE_LEVEL] = 1
                }
            }
        }
        return hash
    }

    override fun requestLoad() {
        printout.info(sourceName = sourceName, trace = { "requestLoad" })
        weakContext.get()?.produce { wContext ->
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
        } ?: run {
            callback.onFailed(
                error = APsspErrorUnit.of(
                    errorUnit = APsspErrorUnit.EXCEPTION_CONTEXT_IS_NULL,
                    networkUnit = networkUnit
                )
            )
        }
    }

    override fun onResume() {
        printout.info(sourceName = sourceName) {
            "onResume { passLifecycle: ${networkUnit == APsspNetworkUnit.MEZZOMEDIA} }"
        }
        if (networkUnit != APsspNetworkUnit.MEZZOMEDIA) {
            runCatching {
                ssp?.onResume()
            }.onFailure {
                printout.error(sourceName = sourceName, trace = { "onResume" }, throwable = it)
            }
        }
    }

    override fun onPause() {
        printout.info(sourceName = sourceName) {
            "onResume { passLifecycle: ${networkUnit == APsspNetworkUnit.MEZZOMEDIA} }"
        }
        if (networkUnit != APsspNetworkUnit.MEZZOMEDIA) {
            runCatching {
                ssp?.onPause()
            }.onFailure {
                printout.error(sourceName = sourceName, trace = { "onPause" }, throwable = it)
            }
        }
    }

    override fun onDestroy() {
        printout.info(sourceName = sourceName, trace = { "onDestroy" })
        try {
            ssp?.setBannerEventCallbackListener(null)
            ssp?.removeAllViews()
            ssp?.stopAd()
            ssp = null
            weakContext.clear()
        } catch (e: Exception) {
            printout.error(sourceName = sourceName, throwable = e)
        }
    }

    override fun OnBannerAdReceiveSuccess() {
        weakContext.get()?.produce {
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

    override fun OnBannerAdReceiveFailed(error: SSPErrorCode?) {
        weakContext.get()?.produce {
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
            }
        } ?: run {
            callback.onFailed(
                error = APsspErrorUnit.of(
                    sspErrorUnit = error,
                    networkUnit = networkUnit
                )
            )
        }
    }

    override fun OnBannerAdClicked() {
        if (ssp != null && weakContext.get() != null) {
            callback.onClicked()
        }
    }
}