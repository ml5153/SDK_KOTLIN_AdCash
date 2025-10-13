package com.avatye.adcash.biz.entity.app

enum class AdsPlacementUnit(val value: String) {

    // igaworks
    Banner320X50("Banner320X50"),
    Banner320X100("Banner320X100"),
    Banner320X250("Banner320X250"),
    Banner300X250("Banner300X250"),
    BannerNam("BannerNam"),
    BannerNativeNam("BannerNativeNam"),
    BannerNative320X50("BannerNative320X50"),
    BannerNative320X100("BannerNative320X100"),
    BannerNative300X250("BannerNative300X250"),
    Native("Native"),
    Interstitial("Interstitial"),
    InterstitialNative("InterstitialNative"),
    InterstitialBox("InterstitialBox"),
    InterstitialHouse("InterstitialHouse"),
    InterstitialVideo("InterstitialVideo"),
    InterstitialRewardVideo("InterstitialRewardVideo"),

    // admixer
/*
    BannerAdmixer320X50("BannerAdmixer320X50"),
    BannerAdmixer320X100("BannerAdmixer320X100"),
    BannerAdmixer320X250("BannerAdmixer320X250"),
    BannerAdmixer300X250("BannerAdmixer300X250"),
    Banner320X50_ADM("Banner320X50_ADM"),
    Banner320X100_ADM("Banner320X100_ADM"),
    Banner320X250_ADM("Banner320X250_ADM"),
    Banner300X250_ADM("Banner300X250_ADM"),
    BannerAdmixerNative320X50("BannerAdmixerNative320X50"),
    BannerAdmixerNative320X100("BannerAdmixerNative320X100"),
    BannerAdmixerNative300X250("BannerAdmixerNative300X250"),
    BannerNative320X50_ADM("BannerNative320X50_ADM"),
    BannerNative320X100_ADM("BannerNative320X100_ADM"),
    BannerNative300X250_ADM("BannerNative300X250_ADM"),
    InterstitialAdmixer("InterstitialAdmixer"),
    InterstitialAdmixerNative("InterstitialAdmixerNative"),
    InterstitialAdmixerBox("InterstitialAdmixerBox"),
    InterstitialAdmixerVideo("InterstitialAdmixerVideo"),
    InterstitialAdmixerRewardVideo("InterstitialAdmixerRewardVideo"),
    Interstitial_ADM("Interstitial_ADM"),
    InterstitialNative_ADM("InterstitialNative_ADM"),
    InterstitialBox_ADM("InterstitialBox_ADM"),
    InterstitialVideo_ADM("InterstitialVideo_ADM"),
    InterstitialRewardVideo_ADM("InterstitialRewardVideo_ADM"),
*/

    // mezzomedia
    BannerMezzoMedia320X50("BannerMezzoMedia320X50"),
    BannerMezzoMedia320X100("BannerMezzoMedia320X100"),

    // doyouad
    BannerDoYouAd320X50("BannerDoYouAd320X50"),
    BannerDoYouAd320X100("BannerDoYouAd320X100"),

    // house
    BannerHouse320X50("BannerHouse320X50"),
    BannerHouse320X100("BannerHouse320X100"),
    BannerHouse300X250("BannerHouse300X250"),
    BannerHouse320X250("BannerHouse320X250");

    companion object {
        fun from(value: String): AdsPlacementUnit? {
            return entries.find {
                it.value.equals(other = value, ignoreCase = true)
            }
        }
    }
}