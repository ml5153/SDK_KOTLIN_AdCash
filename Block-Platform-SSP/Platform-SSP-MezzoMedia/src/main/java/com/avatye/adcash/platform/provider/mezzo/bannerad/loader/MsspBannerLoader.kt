package com.avatye.adcash.platform.provider.mezzo.bannerad.loader

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.provider.basement.AdsviserProviderUnit
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize
import com.avatye.adcash.platform.provider.mezzo.MsspErrorUnit
import com.avatye.adcash.platform.provider.mezzo.Settings.printout
import com.mmc.man.AdConfig
import com.mmc.man.AdEvent
import com.mmc.man.AdListener
import com.mmc.man.data.AdData
import com.mmc.man.view.AdManView
import java.lang.ref.WeakReference

internal class MsspBannerLoader(
    private val context: Context,
    private val placementAppKey: String,
    private val placementId: String,
    private val placementSize: AdsviserBannerUnitSize,
    private val storeUrls: String,
    private val callback: MsspBannerLoaderCallback
) : MsspBannerLoaderBase() {

    private val sourceName = "MsspBannerLoader"
    private val adType = "mezzobannerid"
    private val weakContext = WeakReference(context)

    override val loaderName: String get() = "BannerLoader"
    override val bannerUnitSize: AdsviserBannerUnitSize get() = placementSize

    private var banner: AdManView? = null
    private var adData: AdData? = null

    private fun initializer(blockCallback: () -> Unit) {
        try {
            weakContext.get()?.let { wContext ->
                val appName = wContext.applicationInfo.loadLabel(wContext.packageManager).toString()

                val (width, height) = when (placementSize) {
                    AdsviserBannerUnitSize.W320XH50 -> 320 to 50
                    AdsviserBannerUnitSize.W320XH100 -> 320 to 100
                    else -> {
                        callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.BLOCKED_SIZE))
                        return
                    }
                }

                val appKeyInt = placementAppKey.toIntOrNull() ?: run {
                    printout.info(sourceName = sourceName, trace = { "initializer -> appKey IsEmpty" })
                    callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.INVALID_PARAMETER))
                    return
                }

                val placementIdInt = placementId.toIntOrNull() ?: run {
                    printout.info(sourceName = sourceName, trace = { "initializer -> placementID IsEmpty" })
                    callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.INVALID_PARAMETER))
                    return
                }

                adData = adData ?: AdData()
                banner = banner ?: AdManView(wContext)

                adData?.apply {
                    major(
                        adType,
                        AdConfig.API_BANNER,
                        1349,
                        appKeyInt,
                        placementIdInt,
                        storeUrls,
                        context.packageName,
                        appName,
                        width,
                        height
                    )
                    userAgeLevel = -1
                    isPermission(AdConfig.NOT_USED)
                    setApiModule(AdConfig.NOT_USED, AdConfig.NOT_USED)
                }
                blockCallback()
            } ?: run {
                printout.warn(sourceName = sourceName, trace = { "initializer -> context was null" })
                callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.EXCEPTION_LOADER_IS_NULL))
            }
        } catch (e: Exception) {
            printout.error(sourceName = sourceName, trace = { "initializer exception: ${e.message}" })
            callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.EXCEPTION))
        }
    }

    override fun requestLoad() {
        printout.info(sourceName = sourceName, trace = { "requestLoad -> placementId: $placementId" })
        try {
            initializer {
                loadBannerAd()
            }
        } catch (e: Exception) {
            printout.error(sourceName = sourceName, trace = { "requestLoad exception: ${e.message}" })
            callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.EXCEPTION))
        }
    }

    private fun loadBannerAd() {
        try {
            weakContext.isAvailable { _ ->
                banner?.let { view ->
                    view.setData(adData, object : AdListener {
                        override fun onAdSuccessCode(
                            v: Any?, id: String?, type: String?, status: String?, jsonDataString: String?
                        ) {
                            try {
                                (view as? ViewGroup)?.let { vg ->
                                    printout.info(sourceName = sourceName, trace = { "AdResult::SUCCESS $status" })
                                    callback.onLoaded(
                                        view = vg,
                                        networkUnitName = AdsviserProviderUnit.MEZZOMEDIA.providerName
                                    )
                                } ?: run {
                                    callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.EXCEPTION))
                                }
                            } catch (e: Exception) {
                                printout.error(sourceName = sourceName, trace = { "onAdSuccessCode error: ${e.message}" })
                                callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.EXCEPTION))
                            }
                        }

                        override fun onAdFailCode(
                            v: Any?, id: String?, type: String?, status: String?, jsonDataString: String?
                        ) {
                            try {
                                val err = status
                                    ?.let(MsspErrorUnit::of)
                                    ?: MsspErrorUnit.of(MsspErrorUnit.EXCEPTION)
                                printout.info(sourceName = sourceName, trace = { "AdResult::FAIL $err" })
                                callback.onFailed(err)
                            } catch (e: Exception) {
                                printout.error(sourceName = sourceName, trace = { "onAdFailCode error: ${e.message}" })
                                callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.EXCEPTION))
                            }
                        }

                        override fun onAdErrorCode(
                            v: Any?, id: String?, type: String?, status: String?, failingUrl: String?
                        ) {
                            try {
                                val err = status
                                    ?.let(MsspErrorUnit::of)
                                    ?: MsspErrorUnit.of(MsspErrorUnit.EXCEPTION)
                                printout.info(sourceName = sourceName, trace = { "AdResult::ERROR $err" })
                                callback.onFailed(err)
                            } catch (e: Exception) {
                                printout.error(sourceName = sourceName, trace = { "onAdErrorCode error: ${e.message}" })
                                callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.EXCEPTION))
                            }
                        }

                        override fun onAdEvent(
                            v: Any?, id: String?, type: String?, status: String?, jsonDataString: String?
                        ) {
                            try {
                                when (type) {
                                    AdEvent.Type.CLICK -> {
                                        printout.info(sourceName = sourceName, trace = { "AdResult::CLICK" })
                                        callback.onClicked()
                                    }

                                    AdEvent.Type.CLOSE -> {
                                        printout.info(sourceName = sourceName, trace = { "AdResult::CLOSE" })
                                    }

                                    AdEvent.Type.IMP -> {
                                        printout.info(sourceName = sourceName, trace = { "AdResult::IMP" })
                                    }
                                }
                            } catch (e: Exception) {
                                printout.error(sourceName = sourceName, trace = { "onAdEvent error: ${e.message}" })
                            }
                        }

                        override fun onPermissionSetting(v: Any?, id: String?) {
                            // 권한 설정 요청 이벤트 발생 시 처리. isPermission 값을 NOT_USED 설정
                        }
                    })

                    try {
                        view.request(Handler(Looper.getMainLooper()))
                    } catch (e: Exception) {
                        printout.error(sourceName = sourceName, trace = { "banner.request failed: ${e.message}" })
                        callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.SERVER_TIMEOUT))
                    }
                } ?: run {
                    printout.warn(sourceName = sourceName, trace = { "loadBannerAd -> banner is null" })
                    callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.EXCEPTION_LOADER_IS_NULL))
                }
            }
        } catch (e: Exception) {
            printout.error(sourceName = sourceName, throwable = e, trace = { "loadBannerAd::exception" })
            callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.SERVER_TIMEOUT))
        }
    }

    override fun onResume() {
        try {
            banner?.onResume()
        } catch (e: Exception) {
            printout.error(sourceName = sourceName, trace = { "onResume failed: ${e.message}" })
        }
    }

    override fun onPause() {
        try {
            //MezzoMedia CPC webView Pause issue. 2024.03.24 Disable
            //banner?.onPause()
        } catch (e: Exception) {
            printout.error(sourceName = sourceName, trace = { "onPause failed: ${e.message}" })
        }
    }

    override fun onDestroy() {
        try {
            banner?.onDestroy()
        } catch (e: Exception) {
            printout.error(sourceName = sourceName, trace = { "onDestroy failed: ${e.message}" })
        } finally {
            banner = null
        }
    }
}
