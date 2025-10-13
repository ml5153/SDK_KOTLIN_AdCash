package com.avatye.adcash.platform.provider.adhouse.bannerad.loader

import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize

abstract class AHsspBannerLoaderBase {

    abstract val loaderName: String
    abstract val bannerUnitSize: AdsviserBannerUnitSize

    abstract fun requestLoad()
    abstract fun onResume()
    abstract fun onPause()
    abstract fun onDestroy()
}