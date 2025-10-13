package com.avatye.adcash.loader

import android.app.Activity
import com.avatye.adcash.ADCashSettings
import com.avatye.adcash.ADCashSettings.printout
import com.avatye.adcash.AdError
import com.avatye.adcash.AdErrorUnit
import com.avatye.adcash.AppKeySetting
import com.avatye.adcash.InterstitialAdType
import com.avatye.adcash.InterstitialMediationExtra
import com.avatye.adcash.biz.entity.app.AdsNetworkData
import com.avatye.adcash.biz.entity.app.AdsNetworkName
import com.avatye.adcash.biz.entity.app.AdsPlacementUnit
import com.avatye.adcash.biz.entity.app.AdsUnitPlacementData
import com.avatye.adcash.biz.interact.entity.ResAdsUnit
import com.avatye.adcash.platform.library.extension.isAlive
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.network.capacitor.CapacitorFailure
import com.avatye.adcash.platform.library.network.capacitor.ICapacitorCallback
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.adhouse.interstitialad.AHsspInterstitialHouse
//import com.avatye.adcash.platform.provider.admixer.interstitialad.AMsspInterstitial
import com.avatye.adcash.platform.provider.adpopcorn.interstitialad.APsspInterstitial
import com.avatye.adcash.platform.provider.basement.AdsviserAgeVerifier
import com.avatye.adcash.platform.provider.basement.AdsviserError
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitial
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialCallback
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialLoaderBase
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialProperty
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit
import java.lang.ref.WeakReference
import java.util.LinkedList
import java.util.Queue

class InterstitialAdLoader(
    private val ownerActivity: Activity,
    private val placementId: String,
    private val listener: InterstitialListener
) : BaseUnitConfig() {

    interface InterstitialListener {
        fun onLoaded(executor: InterstitialExecutor, adType: InterstitialAdType)
        fun onOpened()
        fun onClosed(completed: Boolean)
        fun onFailed(error: AdError)
        fun onClicked()
    }

    init {
        setAppKeySetting(
            AppKeySetting(
                appId = ADCashSettings.adCashAppId,
                appSecret = ADCashSettings.adCashAppSecret,
                ageVerifier = ADCashSettings.isVerified
            )
        )
    }

    private val sourceName = "InterstitialAdLoader"
    private val weakActivity = WeakReference(ownerActivity)
    /*private var mediationExtra: HashMap<String, Any> = HashMap<String, Any>().apply {
        ADCashSettings.userName?.produce { this[InterstitialMediationExtra.EXTRA_ADMIXER_USER_NAME] = it }
        ADCashSettings.userPhoneNumber?.produce { this[InterstitialMediationExtra.EXTRA_ADMIXER_USER_PHONE_NUMBER] = it }
        ADCashSettings.appName?.produce { this[InterstitialMediationExtra.EXTRA_ADMIXER_APP_NAME] = it }
    }*/
    private val interstitialQueue: Queue<AdsviserInterstitial?> = LinkedList()
    private var interstitial: AdsviserInterstitial? = null

    private val callback: AdsviserInterstitialCallback = object : AdsviserInterstitialCallback {
        override fun onLoaded(loader: AdsviserInterstitialLoaderBase, unitType: AdsviserInterstitialUnit, networkUnitName: String) {
            printout.info(sourceName = sourceName) {
                "InterstitialCallback:onLoaded { InterstitialUnitType: ${unitType.name} }"
            }
            weakActivity.isAvailable {
                this@InterstitialAdLoader.interstitialLoader = loader
                this@InterstitialAdLoader.listener.onLoaded(
                    executor = InterstitialExecutor(),
                    adType = InterstitialAdType.of(unitType)
                )

                interstitialQueue.forEach {
                    it?.let {
                        printout.info(
                            sourceName = sourceName,
                            trace = { "Destroying interstitial object: ${it::class.simpleName}" }
                        )
                        it.onDestroy()
                    }
                }

            }
        }

        override fun onOpened() {
            printout.info(sourceName = sourceName) { "InterstitialCallback:onOpened" }
            weakActivity.isAvailable {
                this@InterstitialAdLoader.listener.onOpened()
            }
        }

        override fun onComplete(completed: Boolean) {
            printout.info(sourceName = sourceName) { "InterstitialCallback:onComplete $completed" }
            weakActivity.isAvailable {
                this@InterstitialAdLoader.listener.onClosed(completed)
            }
        }

        override fun onFailed(error: AdsviserError) {
            printout.info(sourceName = sourceName) { "InterstitialCallback:ondFailed $error" }
            weakActivity.isAvailable {
                if (interstitialQueue.size > 0) {
                    poll()
                } else {
                    this@InterstitialAdLoader.listener.onFailed(error = AdError.of(error))
                }
            }
        }

        override fun onNeedAgeVerification() {
            printout.info(sourceName = sourceName) { "InterstitialCallback:onNeedAgeVerification" }
            weakActivity.isAvailable {
                this@InterstitialAdLoader.listener.onFailed(error = AdError.of(AdErrorUnit.NEED_AGE_VERIFICATION))
            }
        }

        override fun onClicked() {
            printout.info(sourceName = sourceName) { "InterstitialCallback:onClicked" }
            weakActivity.isAvailable {
                this@InterstitialAdLoader.listener.onClicked()
            }
        }
    }

    fun requestAd() {
        interstitialQueue.clear()
        if (weakActivity.get()?.isAlive == true) {
            loadAdvertiseData {
                if (interstitialQueue.size > 0) {
                    poll()
                } else {
                    listener.onFailed(error = AdError.of(AdErrorUnit.EXCEPTION_LOADER_IS_NULL))
                }
            }
        } else {
            listener.onFailed(error = AdError.of(AdErrorUnit.EXCEPTION_LOADER_IS_NULL))
        }
    }

    private fun poll() {
        interstitial = interstitialQueue.poll()
        interstitial?.let {
            if (it.propertySize > 0) {
                it.requestAD()
            } else {
                poll()
            }
        } ?: run {
            listener.onFailed(error = AdError.of(AdErrorUnit.EXCEPTION_LOADER_IS_NULL))
        }
    }

    private var interstitialLoader: AdsviserInterstitialLoaderBase? = null
    private fun requestShowAd() {
        weakActivity.isAvailable {
            if (interstitialLoader?.isLoaded == true) {
                interstitialLoader?.show()
            } else {
                this@InterstitialAdLoader.listener.onFailed(AdError.of(AdErrorUnit.NOT_LOADED))
            }
        }
    }

    fun onResume() {
        interstitial?.onResume()
    }

    fun onPause() {
        interstitial?.onPause()
    }

    fun onDestroy() {
        interstitial?.onDestroy()
        interstitialQueue.forEach {
            it?.onDestroy()
        }
        interstitialQueue.clear()
        interstitial = null
    }

    private fun loadAdvertiseData(blockCallback: () -> Unit) {
        interactor.retrieveADUnit(placementId = placementId, response = object : ICapacitorCallback<ResAdsUnit> {
            override fun onSuccess(success: ResAdsUnit) {
                weakActivity.isAvailable { activity ->

                    printout.info(sourceName = sourceName) { "loadAdvertiseData -> { ${success.result} }" }

                    if (success.result.networks.size > 0) {
                        success.result.networks.forEach { adsNetworkData ->
                            val hasEnabledUnitId = hasEnabledUnitId(
                                networkName = adsNetworkData.networkName,
                                adUnitPlacementList = adsNetworkData.placements
                            )

                            printout.info(sourceName = sourceName) { "loadAdvertiseData -> { networkName ${adsNetworkData.networkName} hasEnabledUnitId $hasEnabledUnitId }" }

                            if (hasEnabledUnitId) {
                                interstitialQueue.add(
                                    makeInterstitialAdsviser(
                                        activity = activity,
                                        adsNetworkData = adsNetworkData,
                                        timeout = success.result.timeout,
                                        ageVerifier = ageVerifier,
                                        callback = callback
                                    )
                                )
                            } else {
                                return@forEach
                            }
                        }

                        if (interstitialQueue.isNotEmpty()) {
                            blockCallback.invoke()
                        } else {
                            printout.info(sourceName = sourceName) { "loadAdvertiseData -> { queue empty }" }
                            listener.onFailed(AdError.of(AdErrorUnit.INVALID_APID_TYPE))
                        }
                    } else {
                        listener.onFailed(AdError.of(AdErrorUnit.INVALID_APID_TYPE))
                    }
                }
            }

            override fun onFailure(failure: CapacitorFailure) {
                weakActivity.isAvailable { _ ->
                    this@InterstitialAdLoader.listener.onFailed(
                        error = AdError.of(
                            status = failure.status,
                            serverError = failure.code,
                            serverMessage = failure.message
                        )
                    )
                }
            }
        })
    }

    private fun hasEnabledUnitId(networkName: AdsNetworkName?, adUnitPlacementList: MutableList<AdsUnitPlacementData>): Boolean {
        return when(networkName) {
            AdsNetworkName.IGAWORKS -> {
                adUnitPlacementList.find {
                    it.unitId == AdsPlacementUnit.Interstitial
                            || it.unitId == AdsPlacementUnit.InterstitialNative
                            || it.unitId == AdsPlacementUnit.InterstitialBox
                            || it.unitId == AdsPlacementUnit.InterstitialVideo
                            || it.unitId == AdsPlacementUnit.InterstitialRewardVideo
                }?.let { true } ?: false
            }
            /*AdsNetworkName.ADMIXER -> {
                adUnitPlacementList.find {
                    it.unitId == AdsPlacementUnit.InterstitialAdmixer
                            || it.unitId == AdsPlacementUnit.InterstitialAdmixerNative
                            || it.unitId == AdsPlacementUnit.InterstitialAdmixerBox
                            || it.unitId == AdsPlacementUnit.InterstitialAdmixerVideo
                            || it.unitId == AdsPlacementUnit.InterstitialAdmixerRewardVideo
                            || it.unitId == AdsPlacementUnit.Interstitial_ADM
                            || it.unitId == AdsPlacementUnit.InterstitialNative_ADM
                            || it.unitId == AdsPlacementUnit.InterstitialBox_ADM
                            || it.unitId == AdsPlacementUnit.InterstitialVideo_ADM
                            || it.unitId == AdsPlacementUnit.InterstitialRewardVideo_ADM
                }?.let { true } ?: false
            }*/
            AdsNetworkName.HOUSE -> {
                adUnitPlacementList.find {
                    it.unitId == AdsPlacementUnit.InterstitialHouse
                }?.let { true } ?: false
            }
            else -> {
                false
            }
        }
    }

    private fun makeInterstitialAdsviser(
        activity: Activity,
        adsNetworkData: AdsNetworkData,
        ageVerifier: AdsviserAgeVerifier,
        timeout: Long,
        callback: AdsviserInterstitialCallback
    ): AdsviserInterstitial {
        return when (adsNetworkData.networkName) {
            AdsNetworkName.IGAWORKS -> {
                APsspInterstitial(
                    ownerActivity = activity,
                    placementAppKey = adsNetworkData.keyValue,
                    properties = makeProviderProperties(adsNetworkData.placements),
                    placementTimeout = timeout,
                    ageVerifier = ageVerifier,
                    callback = callback
                )
            }
            /*AdsNetworkName.ADMIXER -> {
                AMsspInterstitial(
                    ownerActivity = activity,
                    placementAppKey = adsNetworkData.keyValue,
                    properties = makeProviderProperties(adsNetworkData.placements),
                    placementTimeout = timeout,
                    ageVerifier = ageVerifier,
                    mediationExtra = mediationExtra,
                    callback = callback
                )
            }*/
            else -> {
                AHsspInterstitialHouse(
                    ownerActivity = activity,
                    properties = makeProviderProperties(
                        adUnitPlacementList = adsNetworkData.placements
                    ),
                    placementTimeout = timeout,
                    ageVerifier = ageVerifier,
                    callback = callback
                )
            }
        }
    }

    private fun makeProviderProperties(adUnitPlacementList: MutableList<AdsUnitPlacementData>): MutableList<AdsviserInterstitialProperty> {
        val result = mutableListOf<AdsviserInterstitialProperty>()
        adUnitPlacementList.forEach {
            makeProviderProperty(it)?.produce { prop ->
                result.add(prop)
            }
        }
        return result
    }

    private fun makeProviderProperty(entity: AdsUnitPlacementData): AdsviserInterstitialProperty? {
        return when (entity.unitId) {
            //AdsPlacementUnit.Interstitial_ADM,
            //AdsPlacementUnit.InterstitialAdmixer,
            AdsPlacementUnit.Interstitial -> AdsviserInterstitialProperty.of(
                unitType = AdsviserInterstitialUnit.INTERSTITIAL,
                placementID = entity.unitValue,
                videoInterval = 0L
            )
            //AdsPlacementUnit.InterstitialBox_ADM,
            //AdsPlacementUnit.InterstitialAdmixerBox,
            AdsPlacementUnit.InterstitialBox -> AdsviserInterstitialProperty.of(
                unitType = AdsviserInterstitialUnit.INTERSTITIAL_BOX,
                placementID = entity.unitValue,
                videoInterval = 0L
            )
            //AdsPlacementUnit.InterstitialNative_ADM,
            //AdsPlacementUnit.InterstitialAdmixerNative,
            AdsPlacementUnit.InterstitialNative -> AdsviserInterstitialProperty.of(
                unitType = AdsviserInterstitialUnit.INTERSTITIAL_NATIVE,
                placementID = entity.unitValue,
                videoInterval = 0L
            )
            //AdsPlacementUnit.InterstitialVideo_ADM,
            //AdsPlacementUnit.InterstitialAdmixerVideo,
            AdsPlacementUnit.InterstitialVideo -> AdsviserInterstitialProperty.of(
                unitType = AdsviserInterstitialUnit.INTERSTITIAL_VIDEO,
                placementID = entity.unitValue,
                videoInterval = ADCashSettings.videoInterval
            )
            //AdsPlacementUnit.InterstitialRewardVideo_ADM,
            //AdsPlacementUnit.InterstitialAdmixerRewardVideo,
            AdsPlacementUnit.InterstitialRewardVideo -> AdsviserInterstitialProperty.of(
                unitType = AdsviserInterstitialUnit.INTERSTITIAL_REWARD_VIDEO,
                placementID = entity.unitValue,
                videoInterval = ADCashSettings.videoInterval
            )
            AdsPlacementUnit.InterstitialHouse -> AdsviserInterstitialProperty.ofHouse(
                imageUrl = entity.imageUrl,
                landingUrl = entity.landingUrl
            )
            else -> null
        }
    }

    inner class InterstitialExecutor {
        fun show() {
            this@InterstitialAdLoader.requestShowAd()
        }
    }
}