package com.avatye.adcash.platform.provider.admixer.bannerad.loader

import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize

abstract class AMsspBannerLoaderBase {

    abstract val loaderName: String
    abstract val bannerUnitSize: AdsviserBannerUnitSize

    abstract fun requestLoad()
    abstract fun onResume()
    abstract fun onPause()
    abstract fun onDestroy()
}