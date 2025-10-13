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
import com.nasmedia.admixer.ads.RewardInterstitialVideoAd
import java.lang.ref.WeakReference

internal class AMsspInterstitialRewardVideoLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementId: String,
    private val mediationExtra: HashMap<String, Any>? = null,
    private val callback: AMsspInterstitialLoaderCallback
) : AMsspInterstitialLoaderBase(), AdListener {

    private val sourceName = "AMsspInterstitialRewardVideoLoader"
    private val weakActivity = WeakReference(activity)
    override val loaderName: String get() = "InterstitialRewardVideoLoader"
    override val interstitialUnit = AdsviserInterstitialUnit.INTERSTITIAL
    private var playCompleted: Boolean = false
    private var rewardInterstitialVideoAd: RewardInterstitialVideoAd? = null

    private fun initializer(blockCallback: () -> Unit) {
        weakActivity.isAvailable { wActivity ->
            if (rewardInterstitialVideoAd == null) {
                // [아래 설정은 AdInfo.InterstitialAdType.Popup 을 사용 했을 때 원하시는 조건만 추가하시면 됩니다.]
                // [팝업형 전면광고] 추가옵션 (Basic : 일반전면, Popup : 버튼이 있는 팝업형 전면)
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
                
                val adInfo = AdInfo.Builder(placementId) // AdMixer 플랫폼에서 발급받은 전면 배너 ADUNIT_ID
                    .interstitialTimeout(0) // 초단위로 전면 광고 타임아웃 설정 (기본값 : 0, 0 이면 서버지정 시간으로 처리, 서버지정 시간 : 20s)
                    .maxRetryCountInSlot(0) // 리로드 시간 내에 반복 횟수(-1 : 무한, 0 : 반복 없음, n : n번 반복)
                    .isUseBackgroundAlpha(true) // 반투명처리 여부 (true: 반투명, false: 처리안함) / 기본값 : true
                    .isRetry(false) // 광고 재요청 설정 (true - 기본값), false 시, 1회 요청 후 바로 Callback
                    .setMute(true)
                    .setCustomParams(params) // Reward Callback 커스텀데이터 Map형태로 추가 (선택사항)
                    .build()

                // 이 때 설정하신 RewardInterstitialVideoAd 의 부모 activity 는 원활한 광고 제공을 위해 hardwareAccelerated 가 true 설정되오니 참고 부탁드립니다.
                rewardInterstitialVideoAd = RewardInterstitialVideoAd(wActivity)
                rewardInterstitialVideoAd?.setAdInfo(adInfo)
                rewardInterstitialVideoAd?.setListener(this)

                blockCallback()
            }
        }
    }

    override fun requestLoad() {
        printout.info(sourceName = sourceName, trace = { "requestLoad" })
        weakActivity.isAvailable { wActivity ->
            Settings.initSSP(context = wActivity, appKey = placementAppKey, placementId = placementId) {
                initializer {
                    rewardInterstitialVideoAd?.loadRewardVideoAd() ?: run {
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
            return (weakActivity.get()?.isAlive == true) && (rewardInterstitialVideoAd?.hasInterstitial ?: false)
        }

    override fun show(blockCallback: (success: Boolean) -> Unit) {
        printout.info(sourceName = sourceName, trace = { "show" })
        weakActivity.isAvailable {
            if (isLoaded) {
                rewardInterstitialVideoAd?.showRewardVideoAd()
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
            rewardInterstitialVideoAd?.stopRewardVideoAd()
            rewardInterstitialVideoAd?.setListener(null)
            rewardInterstitialVideoAd?.onDestroy()
            rewardInterstitialVideoAd = null
            weakActivity.clear()
        }.onFailure {
            printout.error(sourceName = sourceName, trace = { "onDestroy" }, throwable = it)
        }
    }

    override fun onReceivedAd(p0: Any?) {
        printout.info(sourceName = sourceName, trace = { "onReceivedAd" })
        weakActivity.isAvailable {
            if (rewardInterstitialVideoAd != null) {
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
        printout.info(sourceName = sourceName, trace = { "onEventAd $p1" })
        when (p1) {
            AdEvent.CLOSE, AdEvent.SKIPPED -> {
                printout.info(sourceName = sourceName, trace = { "onEventAd -> CLOSE, SKIPPED" })
                rewardInterstitialVideoAd?.closeRewardVideoAd()
                weakActivity.isAvailable {
                    if (rewardInterstitialVideoAd != null) {
                        callback.onClosed(isCompleted = this.playCompleted)
                    } else {
                        callback.onClosed(isCompleted = false)
                    }
                }
            }
            AdEvent.DISPLAYED -> {
                printout.info(sourceName = sourceName, trace = { "onEventAd: DISPLAYED" })
                weakActivity.isAvailable {
                    callback.onOpened()
                }
            }
            AdEvent.COMPLETION -> {
                printout.info(sourceName = sourceName, trace = { "onEventAd: COMPLETION" })
                weakActivity.isAvailable {
                    this.playCompleted = true
                }
            }
            else -> {}
        }
    }

}