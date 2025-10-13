package com.avatye.adcash.loader

import android.content.Context
import android.view.View
import com.avatye.adcash.ADCashSettings
import com.avatye.adcash.ADCashSettings.printout
import com.avatye.adcash.AdError
import com.avatye.adcash.AdErrorUnit
import com.avatye.adcash.AppKeySetting
import com.avatye.adcash.BannerAdSize
import com.avatye.adcash.biz.entity.app.AdsNetworkData
import com.avatye.adcash.biz.entity.app.AdsNetworkName
import com.avatye.adcash.biz.entity.app.AdsPlacementUnit
import com.avatye.adcash.biz.entity.app.AdsUnitPlacementData
import com.avatye.adcash.biz.interact.entity.ResAdsUnit
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.network.capacitor.CapacitorFailure
import com.avatye.adcash.platform.library.network.capacitor.ICapacitorCallback
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.adhouse.bannerad.AHsspBannerHouse
import com.avatye.adcash.platform.provider.adpopcorn.bannerad.APsspBanner
import com.avatye.adcash.platform.provider.basement.AdsviserAgeVerifier
import com.avatye.adcash.platform.provider.basement.AdsviserError
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBanner
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerCallback
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerProperty
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnit
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize
import com.avatye.adcash.platform.provider.doyouad.bannerad.DoYouAdBanner
import com.avatye.adcash.platform.provider.mezzo.bannerad.MsspBannerMezzo
import java.lang.ref.WeakReference
import java.util.LinkedList
import java.util.Queue

class BannerAdLoader(
    private val context: Context,
    private val placementId: String,
    private val bannerAdSize: BannerAdSize,
    private val listener: BannerListener
) : BaseUnitConfig() {

    interface BannerListener {
        fun onLoaded(adView: View, size: BannerAdSize)
        fun onFailed(error: AdError)
        fun onClicked()
    }

    private val sourceName = "BannerAdLoader"
    private var mediationExtra: HashMap<String, Any>? = null
    private val weakContext = WeakReference(context)
    private val bannerQueue: Queue<AdsviserBanner?> = LinkedList()
    private var banner: AdsviserBanner? = null

    init {
        setAppKeySetting(
            AppKeySetting(
                appId = ADCashSettings.adCashAppId,
                appSecret = ADCashSettings.adCashAppSecret,
                ageVerifier = ADCashSettings.isVerified
            )
        )
    }

    fun requestAd() {
        bannerQueue.clear()
        if (weakContext.get() != null) {
            loadAdvertiseData {
                if (bannerQueue.size > 0) {
                    poll()
                } else {
                    listener.onFailed(AdError.of(AdErrorUnit.EXCEPTION_LOADER_IS_NULL))
                }
            }
        } else {
            listener.onFailed(AdError.of(AdErrorUnit.EXCEPTION_LOADER_IS_NULL))
        }
    }

    private fun poll() {
        banner = bannerQueue.poll()
        banner?.let {
            if (it.propertySize > 0) {
                it.requestAD()
            } else {
                poll()
            }
        } ?: run {
            listener.onFailed(AdError.of(AdErrorUnit.EXCEPTION_LOADER_IS_NULL))
        }
    }

    fun setMediationExtra(extra: HashMap<String, Any>?) {
        this.mediationExtra = extra
    }

    fun onResume() {
        banner?.onResume()
    }

    fun onPause() {
        banner?.onPause()
    }

    fun onDestroy() {
        banner?.onDestroy()
        bannerQueue.forEach {
            it?.onDestroy()
        }
        bannerQueue.clear()
        banner = null
    }

    private fun loadAdvertiseData(blockCallback: () -> Unit) {
        interactor.retrieveADUnit(placementId = placementId, response = object : ICapacitorCallback<ResAdsUnit> {
            override fun onSuccess(success: ResAdsUnit) {
                weakContext.isAvailable { context ->
                    if (success.result.networks.size > 0) {
                        success.result.networks.forEach { adsNetworkData ->
                            val hasEnabledUnitId = hasEnabledUnitId(
                                networkName = adsNetworkData.networkName,
                                adUnitPlacementList = adsNetworkData.placements
                            )
                            if (hasEnabledUnitId) {
                                bannerQueue.add(
                                    makeBannerAdsviser(
                                        context = context,
                                        adsNetworkData = adsNetworkData,
                                        ageVerifier = ageVerifier,
                                        timeout = success.result.timeout,
                                        callback = bannerProviderCallback
                                    )
                                )
                            } else {
                                return@forEach
                            }
                        }

                        if (bannerQueue.isNotEmpty()) {
                            printout.info(sourceName = sourceName, trace = { "bannerQueue:size ${bannerQueue.size}" })
                            blockCallback.invoke()
                        } else {
                            listener.onFailed(AdError.of(AdErrorUnit.INVALID_APID_TYPE))
                        }
                    } else {
                        listener.onFailed(AdError.of(AdErrorUnit.INVALID_APID_TYPE))
                    }
                }
            }

            override fun onFailure(failure: CapacitorFailure) {
                weakContext.isAvailable {
                    listener.onFailed(
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

    private fun makeBannerAdsviser(
        context: Context,
        adsNetworkData: AdsNetworkData,
        ageVerifier: AdsviserAgeVerifier,
        timeout: Long,
        callback: AdsviserBannerCallback
    ): AdsviserBanner {
        return when (adsNetworkData.networkName) {
            AdsNetworkName.IGAWORKS -> {
                APsspBanner(
                    context = context,
                    placementAppKey = adsNetworkData.keyValue,
                    properties = makeProviderProperties(
                        adUnitPlacementList = adsNetworkData.placements
                    ),
                    placementTimeout = timeout,
                    ageVerifier = ageVerifier,
                    storeUrls = storeUrl,
                    callback = callback
                )
            }

            /*AdsNetworkName.ADMIXER -> {
                AMsspBanner(
                    context = context,
                    placementAppKey = adsNetworkData.keyValue,
                    properties = makeProviderProperties(
                        adUnitPlacementList = adsNetworkData.placements
                    ),
                    placementTimeout = timeout,
                    ageVerifier = ageVerifier,
                    callback = callback
                )
            }*/

            AdsNetworkName.MEZZOMEDIA -> {
                MsspBannerMezzo(
                    context = context,
                    placementAppKey = adsNetworkData.keyValue,
                    properties = makeProviderProperties(
                        adUnitPlacementList = adsNetworkData.placements
                    ),
                    placementTimeout = timeout,
                    ageVerifier = ageVerifier,
                    storeUrls = storeUrl,
                    callback = callback
                )
            }

            AdsNetworkName.DOYOUAD -> {
                DoYouAdBanner(
                    context = context,
                    placementAppKey = adsNetworkData.keyValue,
                    properties = makeProviderProperties(
                        adUnitPlacementList = adsNetworkData.placements
                    ),
                    placementTimeout = timeout,
                    ageVerifier = ageVerifier,
                    callback = callback
                )
            }

            else -> {
                AHsspBannerHouse(
                    context = context,
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

    private fun hasEnabledUnitId(networkName: AdsNetworkName?, adUnitPlacementList: MutableList<AdsUnitPlacementData>): Boolean {
        return when (networkName) {
            AdsNetworkName.IGAWORKS -> {
                adUnitPlacementList.find {
                    it.unitId == AdsPlacementUnit.Banner320X50
                            || it.unitId == AdsPlacementUnit.Banner320X100
                            || it.unitId == AdsPlacementUnit.Banner300X250
                            || it.unitId == AdsPlacementUnit.BannerNative320X50
                            || it.unitId == AdsPlacementUnit.BannerNative320X100
                            || it.unitId == AdsPlacementUnit.BannerNative300X250
                            || it.unitId == AdsPlacementUnit.BannerNam
                            || it.unitId == AdsPlacementUnit.BannerNativeNam
                }?.let { true } ?: false
            }

            /*AdsNetworkName.ADMIXER -> {
                adUnitPlacementList.find {
                    it.unitId == AdsPlacementUnit.BannerAdmixer320X50
                            || it.unitId == AdsPlacementUnit.BannerAdmixer320X100
                            || it.unitId == AdsPlacementUnit.BannerAdmixer300X250
                            || it.unitId == AdsPlacementUnit.BannerAdmixerNative320X50
                            || it.unitId == AdsPlacementUnit.BannerAdmixerNative320X100
                            || it.unitId == AdsPlacementUnit.BannerAdmixerNative300X250
                            || it.unitId == AdsPlacementUnit.Banner320X50_ADM
                            || it.unitId == AdsPlacementUnit.Banner320X100_ADM
                            || it.unitId == AdsPlacementUnit.Banner300X250_ADM
                            || it.unitId == AdsPlacementUnit.BannerNative320X50_ADM
                            || it.unitId == AdsPlacementUnit.BannerNative320X100_ADM
                            || it.unitId == AdsPlacementUnit.BannerNative300X250_ADM
                }?.let { true } ?: false
            }*/

            AdsNetworkName.MEZZOMEDIA -> {
                adUnitPlacementList.find {
                    it.unitId == AdsPlacementUnit.BannerMezzoMedia320X50
                            || it.unitId == AdsPlacementUnit.BannerMezzoMedia320X100
                }?.let { true } ?: false
            }

            AdsNetworkName.DOYOUAD -> {
                adUnitPlacementList.find {
                    it.unitId == AdsPlacementUnit.BannerDoYouAd320X50
                            || it.unitId == AdsPlacementUnit.BannerDoYouAd320X100
                }?.let { true } ?: false
            }


            AdsNetworkName.HOUSE -> {
                adUnitPlacementList.find {
                    it.unitId == AdsPlacementUnit.BannerHouse320X50
                            || it.unitId == AdsPlacementUnit.BannerHouse320X100
                            || it.unitId == AdsPlacementUnit.BannerHouse300X250
                }?.let { true } ?: false
            }

            else -> {
                false
            }
        }
    }

    private fun makeProviderProperties(adUnitPlacementList: MutableList<AdsUnitPlacementData>): MutableList<AdsviserBannerProperty> {
        val result = mutableListOf<AdsviserBannerProperty>()
        adUnitPlacementList.forEach {
            makeBannerProviderProperty(it)?.produce { prop ->
                result.add(prop)
            }
        }
        return result
    }

    private fun makeBannerProviderProperty(entity: AdsUnitPlacementData): AdsviserBannerProperty? {
        return when (bannerAdSize) {
            BannerAdSize.W320XH50 -> {
                when (entity.unitId) {
                    AdsPlacementUnit.BannerMezzoMedia320X50,
                    AdsPlacementUnit.BannerDoYouAd320X50,
                    //AdsPlacementUnit.Banner320X50_ADM,
                    //AdsPlacementUnit.BannerAdmixer320X50,
                    AdsPlacementUnit.Banner320X50 -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.BANNER,
                        unitSize = AdsviserBannerUnitSize.W320XH50,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )



                    //AdsPlacementUnit.BannerNative320X50_ADM,
                    //AdsPlacementUnit.BannerAdmixerNative320X50,
                    AdsPlacementUnit.BannerNative320X50 -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.NATIVE,
                        unitSize = AdsviserBannerUnitSize.W320XH50,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    AdsPlacementUnit.BannerHouse320X50 -> AdsviserBannerProperty.ofHouse(
                        unitSize = AdsviserBannerUnitSize.W320XH50,
                        imageUrl = entity.imageUrl,
                        landingUrl = entity.landingUrl
                    )

                    else -> null
                }
            }

            BannerAdSize.W320XH100 -> {
                when (entity.unitId) {
                    AdsPlacementUnit.BannerMezzoMedia320X100,
                    AdsPlacementUnit.BannerDoYouAd320X100,
                    //AdsPlacementUnit.Banner320X100_ADM,
                    //AdsPlacementUnit.BannerAdmixer320X100,
                    AdsPlacementUnit.Banner320X100 -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.BANNER,
                        unitSize = AdsviserBannerUnitSize.W320XH100,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    //AdsPlacementUnit.BannerNative320X100_ADM,
                    //AdsPlacementUnit.BannerAdmixerNative320X100,
                    AdsPlacementUnit.BannerNative320X100 -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.NATIVE,
                        unitSize = AdsviserBannerUnitSize.W320XH100,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    AdsPlacementUnit.BannerHouse320X100 -> AdsviserBannerProperty.ofHouse(
                        unitSize = AdsviserBannerUnitSize.W320XH100,
                        imageUrl = entity.imageUrl,
                        landingUrl = entity.landingUrl
                    )

                    else -> null
                }
            }

            BannerAdSize.W300XH250 -> {
                when (entity.unitId) {
                    //AdsPlacementUnit.Banner300X250_ADM,
                    //AdsPlacementUnit.BannerAdmixer300X250,
                    AdsPlacementUnit.Banner300X250 -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.BANNER,
                        unitSize = AdsviserBannerUnitSize.W300XH250,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    //AdsPlacementUnit.BannerNative300X250_ADM,
                    //AdsPlacementUnit.BannerAdmixerNative300X250,
                    AdsPlacementUnit.BannerNative300X250 -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.NATIVE,
                        unitSize = AdsviserBannerUnitSize.W300XH250,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    AdsPlacementUnit.BannerHouse300X250 -> AdsviserBannerProperty.ofHouse(
                        unitSize = AdsviserBannerUnitSize.W300XH250,
                        imageUrl = entity.imageUrl,
                        landingUrl = entity.landingUrl
                    )

                    else -> null
                }
            }

            BannerAdSize.DYNAMIC -> {
                when (entity.unitId) {
                    //AdsPlacementUnit.Banner320X50_ADM,
                    //AdsPlacementUnit.BannerAdmixer320X50,
                    AdsPlacementUnit.BannerMezzoMedia320X50,
                    AdsPlacementUnit.BannerDoYouAd320X50,
                    AdsPlacementUnit.Banner320X50 -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.BANNER,
                        unitSize = AdsviserBannerUnitSize.W320XH50,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    //AdsPlacementUnit.BannerNative320X50_ADM,
                    //AdsPlacementUnit.BannerAdmixerNative320X50,
                    AdsPlacementUnit.BannerNative320X50 -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.NATIVE,
                        unitSize = AdsviserBannerUnitSize.W320XH50,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    AdsPlacementUnit.BannerHouse320X50 -> AdsviserBannerProperty.ofHouse(
                        unitSize = AdsviserBannerUnitSize.W320XH50,
                        imageUrl = entity.imageUrl,
                        landingUrl = entity.landingUrl
                    )

                    //AdsPlacementUnit.Banner320X100_ADM,
                    //AdsPlacementUnit.BannerAdmixer320X100,
                    AdsPlacementUnit.BannerMezzoMedia320X100,
                    AdsPlacementUnit.BannerDoYouAd320X100,
                    AdsPlacementUnit.Banner320X100 -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.BANNER,
                        unitSize = AdsviserBannerUnitSize.W320XH100,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    //AdsPlacementUnit.BannerNative320X100_ADM,
                    //AdsPlacementUnit.BannerAdmixerNative320X100,
                    AdsPlacementUnit.BannerNative320X100 -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.NATIVE,
                        unitSize = AdsviserBannerUnitSize.W320XH100,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    AdsPlacementUnit.BannerHouse320X100 -> AdsviserBannerProperty.ofHouse(
                        unitSize = AdsviserBannerUnitSize.W320XH100,
                        imageUrl = entity.imageUrl,
                        landingUrl = entity.landingUrl
                    )

                    //AdsPlacementUnit.Banner300X250_ADM,
                    //AdsPlacementUnit.BannerAdmixer300X250,
                    AdsPlacementUnit.Banner300X250 -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.BANNER,
                        unitSize = AdsviserBannerUnitSize.W300XH250,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    //AdsPlacementUnit.BannerNative300X250_ADM,
                    //AdsPlacementUnit.BannerAdmixerNative300X250,
                    AdsPlacementUnit.BannerNative300X250 -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.NATIVE,
                        unitSize = AdsviserBannerUnitSize.W300XH250,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    AdsPlacementUnit.BannerHouse300X250 -> AdsviserBannerProperty.ofHouse(
                        unitSize = AdsviserBannerUnitSize.W300XH250,
                        imageUrl = entity.imageUrl,
                        landingUrl = entity.landingUrl
                    )

                    AdsPlacementUnit.BannerNam -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.BANNER,
                        unitSize = AdsviserBannerUnitSize.DYNAMIC,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    AdsPlacementUnit.BannerNativeNam -> AdsviserBannerProperty.of(
                        unitType = AdsviserBannerUnit.NATIVE,
                        unitSize = AdsviserBannerUnitSize.DYNAMIC,
                        placementId = entity.unitValue,
                        mediationExtra = mediationExtra
                    )

                    else -> null
                }
            }
        }
    }

    private val bannerProviderCallback = object : AdsviserBannerCallback {

        override fun onLoaded(adView: View, unitSize: AdsviserBannerUnitSize, networkUnitName: String) {
            printout.info(
                sourceName = sourceName,
                trace = { "BannerCallback:onLoaded { BannerAdSize: ${bannerAdSize.name}, NetworkUnit: $networkUnitName }" }
            )
            weakContext.isAvailable {
                listener.onLoaded(
                    adView = adView.apply {
                        tag = networkUnitName
                    },
                    size = bannerAdSize
                )
                bannerQueue.forEach {
                    it?.let {
                        printout.info(
                            sourceName = sourceName,
                            trace = { "Destroying banner object: ${it::class.simpleName}" }
                        )
                        it.onDestroy()
                    }
                }
                bannerQueue.clear()
            }
        }

        override fun onFailed(error: AdsviserError) {
            printout.info(
                sourceName = sourceName,
                trace = { "BannerCallback:ondFailed $error" }
            )
            weakContext.isAvailable {
                if (bannerQueue.size > 0) {
                    poll()
                } else {
                    listener.onFailed(AdError.of(error))
                }
            }
        }

        override fun onNeedAgeVerification() {
            printout.info(
                sourceName = sourceName,
                trace = { "BannerCallback:onNeedAgeVerification" }
            )
            weakContext.isAvailable {
                listener.onFailed(AdError.of(AdErrorUnit.NEED_AGE_VERIFICATION))
            }
        }

        override fun onClicked() {
            printout.info(
                sourceName = sourceName,
                trace = { "BannerCallback:onClicked" }
            )
            weakContext.isAvailable {
                listener.onClicked()
            }
        }
    }
}

