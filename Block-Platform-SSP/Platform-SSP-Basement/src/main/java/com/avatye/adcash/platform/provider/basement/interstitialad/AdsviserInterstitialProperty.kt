package com.avatye.adcash.platform.provider.basement.interstitialad

data class AdsviserInterstitialProperty(
    val unitType: AdsviserInterstitialUnit,
    val placementID: String,
    val videoInterval: Long,
    val houseImageUrl: String = "",
    val houseLandingUrl: String = ""
) {
    companion object {
        fun of(
            unitType: AdsviserInterstitialUnit,
            placementID: String,
            videoInterval: Long
        ): AdsviserInterstitialProperty? {
            return if (unitType != AdsviserInterstitialUnit.INTERSTITIAL_HOUSE && placementID.isNotEmpty()) {
                AdsviserInterstitialProperty(
                    unitType = unitType,
                    placementID = placementID,
                    houseImageUrl = "",
                    houseLandingUrl = "",
                    videoInterval = videoInterval
                )
            } else {
                null
            }
        }

        fun ofHouse(imageUrl: String, landingUrl: String): AdsviserInterstitialProperty? {
            return if (imageUrl.isNotEmpty() && landingUrl.isNotEmpty()) {
                AdsviserInterstitialProperty(
                    unitType = AdsviserInterstitialUnit.INTERSTITIAL_HOUSE,
                    placementID = "",
                    videoInterval = 0L,
                    houseImageUrl = imageUrl,
                    houseLandingUrl = landingUrl
                )
            } else {
                null
            }
        }
    }
}