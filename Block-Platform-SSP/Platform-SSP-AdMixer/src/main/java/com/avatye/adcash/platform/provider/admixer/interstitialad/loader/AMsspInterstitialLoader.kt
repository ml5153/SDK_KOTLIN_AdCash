package com.avatye.adcash.platform.provider.admixer.interstitialad.loader

import android.app.Activity
import com.avatye.adcash.platform.library.extension.isAlive
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.provider.admixer.AMsspErrorUnit
import com.avatye.adcash.platform.provider.admixer.Settings
import com.avatye.adcash.platform.provider.admixer.Settings.printout
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit
import com.nasmedia.admixer.ads.AdEvent
import com.nasmedia.admixer.ads.AdInfo
import com.nasmedia.admixer.ads.AdListener
import com.nasmedia.admixer.ads.InterstitialAd
import com.nasmedia.admixer.ads.PopupInterstitialAdOption
import java.lang.ref.WeakReference

internal class AMsspInterstitialLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementId: String,
    private val callback: AMsspInterstitialLoaderCallback
) : AMsspInterstitialLoaderBase(), AdListener {

    private val sourceName = "AMsspInterstitialLoader"
    private val weakActivity = WeakReference(activity)
    override val loaderName: String get() = "InterstitialLoader"
    override val interstitialUnit = AdsviserInterstitialUnit.INTERSTITIAL
    private var interstitialAd: InterstitialAd? = null

    private fun initializer(blockCallback: () -> Unit) {
        weakActivity.isAvailable { wActivity ->
            if (interstitialAd == null) {
                // [아래 설정은 AdInfo.InterstitialAdType.Popup 을 사용 했을 때 원하시는 조건만 추가하시면 됩니다.]
                // [팝업형 전면광고] 추가옵션 (Basic : 일반전면, Popup : 버튼이 있는 팝업형 전면)
                val adConfig = PopupInterstitialAdOption()
                adConfig.setDisableBackKey(false) // [팝업형 전면광고] 노출 상태에서 뒤로가기 버튼 방지 (true : 비활성화, false : 활성화)
//                adConfig.setButtonLeft("확인", null) // 디폴트로 제공되며, 광고를 닫는 기능이 적용되는 버튼 (버튼문구, 버튼색상)
//                adConfig.setButtonRight("오른쪽버튼", null) // 설정시에만 노출되는 옵션버튼이며, 앱을 종료하는 기능이 적용되는 버튼. 미설정 시 위 광고닫기 버튼만 노출
                adConfig.setButtonFrameColor(null) // 버튼영역 색상지정

                val adInfo = AdInfo.Builder(placementId) // AdMixer 플랫폼에서 발급받은 전면 배너 ADUNIT_ID
                    .interstitialTimeout(0) // 초단위로 전면 광고 타임아웃 설정 (기본값 : 0, 0 이면 서버지정 시간으로 처리, 서버지정 시간 : 20s)
                    .maxRetryCountInSlot(-1) // 리로드 시간 내에 반복 횟수(-1 : 무한, 0 : 반복 없음, n : n번 반복)
                    .isUseBackgroundAlpha(true) // 반투명처리 여부 (true: 반투명, false: 처리안함) / 기본값 : true
                    .isRetry(false) // 광고 재요청 설정 (true - 기본값), false 시, 1회 요청 후 바로 Callback
//                    .popupAdOption(adConfig) // [팝업형 전면광고] 사용 시 설정
//                    .interstitialAdType(AdInfo.InterstitialAdType.Popup) // (default : AdInfo.InterstitialAdType.Basic)
                    .build()

                interstitialAd = InterstitialAd(wActivity)
                interstitialAd?.setAdInfo(adInfo)
                interstitialAd?.setAdListener(this)

                blockCallback()
            }
        }
    }

    override fun requestLoad() {
        printout.info(sourceName = sourceName, trace = { "requestLoad" })
        weakActivity.isAvailable { wActivity ->
            Settings.initSSP(context = wActivity, appKey = placementAppKey, placementId = placementId) {
                initializer {
                    interstitialAd?.loadInterstitial() ?: run {
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
            return (weakActivity.get()?.isAlive == true) && (interstitialAd?.hasInterstitial ?: false)
        }

    override fun show(blockCallback: (success: Boolean) -> Unit) {
        printout.info(sourceName = sourceName, trace = { "show" })
        weakActivity.isAvailable {
            if (isLoaded) {
                interstitialAd?.showInterstitial()
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
            interstitialAd?.stopInterstitial()
            interstitialAd?.setAdListener(null)
            interstitialAd?.onDestroy()
            interstitialAd = null
            weakActivity.clear()
        }.onFailure {
            printout.error(sourceName = sourceName, trace = { "onDestroy" }, throwable = it)
        }
    }

    override fun onReceivedAd(p0: Any?) {
        printout.info(sourceName = sourceName, trace = { "onReceivedAd" })
        weakActivity.isAvailable {
            if (interstitialAd != null) {
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
        printout.info(sourceName = sourceName, trace = { "onEventAd" })
        when (p1) {
            AdEvent.CLOSE -> { // 광고 창이 닫혔을 때
                printout.info(sourceName = sourceName, trace = { "onEventAd: CLOSE" })
                interstitialAd?.closeInterstitial()
                weakActivity.isAvailable {
                    callback.onClosed(isCompleted = true)
                }
            }
            AdEvent.DISPLAYED -> {
                printout.info(sourceName = sourceName, trace = { "onEventAd: DISPLAYED" })
                weakActivity.isAvailable {
                    callback.onOpened()
                }
            }
            else -> {

            }
        }
    }

}