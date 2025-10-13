package com.avatye.adcash

import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit

enum class InterstitialAdType {
    /** 전면 광고 */
    INTERSTITIAL,

    /** 전면 네이티브 광고 */
    INTERSTITIAL_NATIVE,

    /** 300x250 박스형 광고 */
    INTERSTITIAL_BOX,

    /** 300x250 박스형 하우스 광고 */
    INTERSTITIAL_HOUSE,

    /** 전면 비디오 광고 */
    INTERSTITIAL_VIDEO,

    /** 보상형 비디오 광고 */
    INTERSTITIAL_REWARD_VIDEO;

    internal companion object {
        fun of(unitType: AdsviserInterstitialUnit): InterstitialAdType {
            return when (unitType) {
                AdsviserInterstitialUnit.INTERSTITIAL -> INTERSTITIAL
                AdsviserInterstitialUnit.INTERSTITIAL_NATIVE -> INTERSTITIAL_NATIVE
                AdsviserInterstitialUnit.INTERSTITIAL_BOX -> INTERSTITIAL_BOX
                AdsviserInterstitialUnit.INTERSTITIAL_HOUSE -> INTERSTITIAL_HOUSE
                AdsviserInterstitialUnit.INTERSTITIAL_VIDEO -> INTERSTITIAL_VIDEO
                AdsviserInterstitialUnit.INTERSTITIAL_REWARD_VIDEO -> INTERSTITIAL_REWARD_VIDEO
            }
        }
    }
}