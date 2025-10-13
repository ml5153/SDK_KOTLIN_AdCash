package com.avatye.adcash.platform.provider.doyouad.bannerad.loader

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.metaBoolean
import com.avatye.adcash.platform.provider.basement.AdsviserProviderUnit
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize
import com.avatye.adcash.platform.provider.doyouad.DoYouAdErrorUnit
import com.avatye.adcash.platform.provider.doyouad.Settings.printout
import com.avatye.adcash.platform.provider.doyouad.databinding.AcbAdcashSspDoyouadContainerBinding
import com.wisernd.doyouad.constant.BannerSize
import com.wisernd.doyouad.view.AdResult
import com.wisernd.doyouad.view.AdView
import java.lang.ref.WeakReference

internal class DoYouAdBannerLoader(
    private val context: Context,
    private val placementAppKey: String,
    private val placementId: String,
    private val placementSize: AdsviserBannerUnitSize,
    private val callback: DoYouAdBannerLoaderCallback
) : DoYouAdBannerLoaderBase() {

    private val sourceName = "DoYouAdBannerLoader"
    private val weakContext = WeakReference(context)

    override val loaderName: String get() = "BannerLoader[ DoYouAdBannerLoader ]"
    override val bannerUnitSize: AdsviserBannerUnitSize get() = placementSize

    var binding: AcbAdcashSspDoyouadContainerBinding? = null

    companion object {
        const val META_KEY_DOYOUAD_IS_LIVE = "adcash.doyouad.islive"
    }


    init {
        weakContext.get()?.let { wContext ->
            binding = AcbAdcashSspDoyouadContainerBinding.inflate(LayoutInflater.from(wContext))
        }
    }

    override fun requestLoad() {
        printout.info(sourceName = sourceName, trace = { "requestLoad -> placementId: $placementId" })
        initializer()

    }


    private fun initializer() {
        printout.info(sourceName = sourceName, trace = { "initializer -> { placementAppKey: $placementAppKey, placementId: $placementId, placementSize: $placementSize  }" })


        if (placementId.isEmpty()) {
            printout.error(sourceName = sourceName, trace = { "initializer -> placementID IsEmpty" })
            callback.onFailed(DoYouAdErrorUnit.of(DoYouAdErrorUnit.INVALID_PARAMETER))
            return
        }

        val doyYouAdBannerSize = when (placementSize) {
            AdsviserBannerUnitSize.W320XH50 -> BannerSize.BANNER
            AdsviserBannerUnitSize.W320XH100 -> BannerSize.LARGE_BANNER
            AdsviserBannerUnitSize.W300XH250 -> BannerSize.MEDIUM_RECTANGLE
            else -> {
                printout.error(sourceName = sourceName, trace = { "initializer -> BLOCKED_SIZE" })
                callback.onFailed(DoYouAdErrorUnit.of(DoYouAdErrorUnit.BLOCKED_SIZE))
                return
            }
        }

        loadBannerAd(doyYouAdBannerSize)
    }


    private fun loadBannerAd(doyYouAdBannerSize: String) {
        try {
            weakContext.isAvailable { wContext ->
                if (wContext is Activity) {
                    val isLiveDoyouad: Boolean = wContext.metaBoolean(keyName = META_KEY_DOYOUAD_IS_LIVE, defaulValue = true) ?: true
                    printout.info(sourceName = sourceName, trace = { "loadBannerAd:: { wContext is Activity // isLiveDoyouad: $isLiveDoyouad, doyYouAdBannerSize: $doyYouAdBannerSize } " })

                    binding?.doyouad?.apply {
                        setZoneId(str = placementId)
                        setSize(str = doyYouAdBannerSize)  // BANNER: 320x50 , LARGE_BANNER: 320x100 , MEDIUM_RECTANGLE: 300x250
                        isLive(check = isLiveDoyouad) // true: 라이브 , false: 테스트
                        setPadding(0)
                    }

                    // listener
                    binding?.doyouad?.setAdReceivedEvent(wContext, object : AdView.AdEventListener {
                        override fun result(result: AdResult, error: String) {
                            when (result) {
                                AdResult.SUCCESS -> {
                                    printout.info(sourceName = sourceName, trace = { "AdResult::SUCCESS $result" })
                                    binding?.root?.let {
                                        callback.onLoaded(view = it, networkUnitName = AdsviserProviderUnit.DOYOUAD.providerName)
                                    } ?: run {
                                        callback.onFailed(DoYouAdErrorUnit.of(errorUnit = DoYouAdErrorUnit.ERROR))
                                    }
                                }

                                AdResult.FAIL -> {
                                    printout.info(sourceName = sourceName, trace = { "AdResult::FAIL { $result //  error: $error }" })
                                    callback.onFailed(DoYouAdErrorUnit.of(errorMessage = error))
                                }

                                AdResult.RELOAD -> {
                                    printout.info(sourceName = sourceName, trace = { "AdResult::RELOAD $result" })
                                    binding?.root?.let {
                                        callback.onReLoaded(view = it, networkUnitName = AdsviserProviderUnit.DOYOUAD.providerName)
                                    } ?: run {
                                        callback.onFailed(DoYouAdErrorUnit.of(errorUnit = DoYouAdErrorUnit.RELOAD_ERROR))
                                    }
                                }

                                AdResult.CLICK -> {
                                    printout.info(sourceName = sourceName, trace = { "AdResult::CLICK $result" })
                                    callback.onClicked()
                                }

                                AdResult.REWARD -> {
                                    printout.info(sourceName = sourceName, trace = { "AdResult::REWARD $result" })
                                    // Nothing
                                }
                            }
                        }
                    })


                    // load
                    binding?.doyouad?.adLoad()
                } else {
                    printout.error(sourceName = sourceName, trace = { "loadBannerAd:: { wContext is not Activity!! }" })
                    callback.onFailed(DoYouAdErrorUnit.of(errorUnit = DoYouAdErrorUnit.EXCEPTION_CONTEXT_IS_NULL))
                }

            }
        } catch (e: Exception) {
            printout.error(sourceName = sourceName, throwable = e, trace = { "loadBannerAd::exception" })
            callback.onFailed(DoYouAdErrorUnit.of(errorUnit = DoYouAdErrorUnit.SERVER_TIMEOUT))
        }
    }

    override fun onResume() {
        printout.info(sourceName = sourceName, trace = { "onResume" })
    }

    override fun onPause() {
        printout.info(sourceName = sourceName, trace = { "onPause" })
    }

    override fun onDestroy() {
        printout.info(sourceName = sourceName, trace = { "onDestroy()" })
        binding?.doyouad?.removeAd()
        binding?.doyouad?.removeAllViews()
        binding = null

    }
}