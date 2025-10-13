package com.avatye.adcash.platform.provider.admixer.interstitialad.loader

import android.app.Activity
import com.avatye.adcash.platform.library.extension.isAlive
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.admixer.AMsspErrorUnit
import com.avatye.adcash.platform.provider.admixer.Settings
import com.avatye.adcash.platform.provider.admixer.Settings.printout
import com.avatye.adcash.platform.provider.basement.AdsviserMediationExtra
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit
import com.nasmedia.admixer.ads.AdEvent
import com.nasmedia.admixer.ads.AdInfo
import com.nasmedia.admixer.ads.AdListener
import com.nasmedia.admixer.ads.InterstitialVideoAd
import java.lang.ref.WeakReference

internal class AMsspInterstitialVideoLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementId: String,
    private val videoInterval: Long,
    private val mediationExtra: HashMap<String, Any>? = null,
    private val callback: AMsspInterstitialLoaderCallback
) : AMsspInterstitialLoaderBase(), AdListener {

    private val sourceName = "AMsspInterstitialVideoLoader"
    private val weakActivity = WeakReference(activity)
    override val loaderName: String get() = "InterstitialLoader"
    override val interstitialUnit = AdsviserInterstitialUnit.INTERSTITIAL
    private var interstitialVideoAd: InterstitialVideoAd? = null

    private fun initializer(blockCallback: () -> Unit) {
        weakActivity.isAvailable { wActivity ->
            if (interstitialVideoAd == null) {
                val params: MutableMap<String, String> = HashMap()
                mediationExtra?.produce { extra ->
                    with(AdsviserMediationExtra) {
                        (extra[EXTRA_INTERSTITIAL_ADMIXER_APP_NAME] as? String)?.produce {
                            params[EXTRA_INTERSTITIAL_ADMIXER_APP_NAME] = it // "nas" // userID
                        }
                        (extra[EXTRA_INTERSTITIAL_ADMIXER_USER_NAME] as? String)?.produce {
                            params[EXTRA_INTERSTITIAL_ADMIXER_USER_NAME] = it // "choi" // 이름
                        }
                        (extra[EXTRA_INTERSTITIAL_ADMIXER_USER_PHONE_NUMBER] as? String)?.produce {
                            params[EXTRA_INTERSTITIAL_ADMIXER_USER_PHONE_NUMBER] = it // "010-1111-1111"// phoneNum
                        }
                    }
                }

                val adInfo =
                    AdInfo.Builder(placementId) // AdMixer 플랫폼에서 발급받은 전면 비디오 ADUNIT_ID
                        .interstitialTimeout((videoInterval * 0.001).toInt()) // 초단위로 전면 광고 타임아웃 설정 (기본값 : 0, 0 이면 서버지정 시간으로 처리, 서버지정 시간 : 20s)
                        .maxRetryCountInSlot(0) // 리로드 시간 내에 반복 횟수(-1 : 무한, 0 : 반복 없음, n : n번 반복)
                        .isRetry(false) // 광고 재요청 설정 (true - 기본값), false 시, 1회 요청 후 바로 Callback
                        .setCustomParams(params) // Reward Callback 커스텀데이터 Map형태로 추가 (선택사항)
                        .setMute(true)
                        .build()

                interstitialVideoAd = InterstitialVideoAd(wActivity)
                interstitialVideoAd?.setAdInfo(adInfo)
                interstitialVideoAd?.setListener(this)

                blockCallback()
            }
        }
    }

    override fun requestLoad() {
        printout.info(sourceName = sourceName, trace = { "requestLoad" })
        weakActivity.isAvailable { wActivity ->
            Settings.initSSP(context = wActivity, appKey = placementAppKey, placementId = placementId) {
                initializer {
                    interstitialVideoAd?.loadInterstitialVideoAd() ?: run {
                        callback.onFailed(
                            error = AMsspErrorUnit.of(
                                errorUnit = AMsspErrorUnit.EXCEPTION_LOADER_IS_NULL,
                            )
                        )
                    }
                }
            }
        }
    }

    override val isLoaded: Boolean
        get() {
            return (weakActivity.get()?.isAlive == true) && (interstitialVideoAd?.hasInterstitial ?: false)
        }

    override fun show(blockCallback: (success: Boolean) -> Unit) {
        printout.info(sourceName = sourceName, trace = { "show" })
        weakActivity.isAvailable {
            if (isLoaded) {
                interstitialVideoAd?.showInterstitialVideoAd()
                blockCallback(true)
            } else {
                blockCallback(false)
            }
        }
    }

    override fun onResume() {
        printout.info(sourceName = sourceName, trace = { "onResume" })
    }

    override fun onPause() {
        printout.info(sourceName = sourceName, trace = { "onPause" })
    }

    override fun onDestroy() {
        printout.info(sourceName = sourceName, trace = { "onDestroy" })
        runCatching {
            interstitialVideoAd?.stopInterstitialVideoAd()
            interstitialVideoAd?.setListener(null)
            interstitialVideoAd?.onDestroy()
            interstitialVideoAd = null
            weakActivity.clear()
        }.onFailure {
            printout.error(sourceName = sourceName, trace = { "onDestroy" }, throwable = it)
        }
    }

    override fun onReceivedAd(p0: Any?) {
        printout.info(sourceName = sourceName, trace = { "onReceivedAd" })
        weakActivity.isAvailable {
            if (interstitialVideoAd != null) {
                callback.onLoaded(unitType = interstitialUnit, networkUnitName = networkUnitName)
            } else {
                callback.onFailed(
                    error = AMsspErrorUnit.of(
                        errorUnit = AMsspErrorUnit.EXCEPTION_LOADER_IS_NULL,
                    )
                )
            }
        }
    }

    override fun onFailedToReceiveAd(p0: Any?, p1: Int, p2: String?) {
        printout.info(sourceName = sourceName, trace = { "onFailedToReceiveAd" })
        weakActivity.isAvailable {
            callback.onFailed(
                error = AMsspErrorUnit.of(errorCode = p1, errorMessage = p2)
            )
        }
    }

    override fun onEventAd(p0: Any?, p1: AdEvent?) {
        printout.info(sourceName = sourceName, trace = { "onEventAd -> p1:$p1" })
        when (p1) {
            AdEvent.CLOSE, AdEvent.SKIPPED -> { // 광고 창이 닫혔을 때
                printout.info(sourceName = sourceName, trace = { "onEventAd -> CLOSE, SKIPPED" })
                weakActivity.isAvailable {
                    interstitialVideoAd?.closeInterstitialVideoAd()
                }
                callback.onClosed(isCompleted = false)
            }
            AdEvent.COMPLETION -> {
                printout.info(sourceName = sourceName, trace = { "onEventAd: COMPLETION" })
            }
            AdEvent.DISPLAYED -> {
                printout.info(sourceName = sourceName, trace = { "onEventAd: DISPLAYED" })
                weakActivity.isAvailable {
                    callback.onOpened()
                }
            }
            AdEvent.CLICK -> {
                printout.info(sourceName = sourceName, trace = { "onEventAd: CLICK" })
            }
            else -> {}
        }
    }

}