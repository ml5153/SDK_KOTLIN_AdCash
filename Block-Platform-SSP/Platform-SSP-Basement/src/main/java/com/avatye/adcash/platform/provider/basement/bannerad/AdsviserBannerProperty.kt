package com.avatye.adcash.platform.provider.basement.bannerad

data class AdsviserBannerProperty(
    val unitType: AdsviserBannerUnit,
    val unitSize: AdsviserBannerUnitSize,
    val placementId: String,
    val houseImageUrl: String,
    val houseLandingUrl: String,
    val mediationExtra: HashMap<String, Any>? = null
) {
    companion object {
        fun of(
            unitType: AdsviserBannerUnit,
            unitSize: AdsviserBannerUnitSize,
            placementId: String,
            mediationExtra: HashMap<String, Any>? = null
        ): AdsviserBannerProperty? {
            return if (placementId.isNotEmpty()) {
                AdsviserBannerProperty(
                    unitType = unitType,
                    unitSize = unitSize,
                    placementId = placementId,
                    houseImageUrl = "",
                    houseLandingUrl = "",
                    mediationExtra = mediationExtra
                )
            } else {
                null
            }
        }

        fun ofHouse(unitSize: AdsviserBannerUnitSize, imageUrl: String, landingUrl: String): AdsviserBannerProperty? {
            return if (imageUrl.isNotEmpty() && landingUrl.isNotEmpty()) {
                AdsviserBannerProperty(
                    unitType = AdsviserBannerUnit.HOUSE,
                    unitSize = unitSize,
                    placementId = "",
                    houseImageUrl = imageUrl,
                    houseLandingUrl = landingUrl
                )
            } else {
                null
            }
        }
    }
}