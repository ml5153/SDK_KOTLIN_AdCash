package com.avatye.adcash.platform.provider.adpopcorn.bannerad.loader

import com.avatye.adcash.platform.provider.adpopcorn.APsspNetworkUnit
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize

abstract class APsspBannerLoaderBase {

    protected abstract val networkUnitNum: Int
    protected val networkUnitName: String get() = APsspNetworkUnit.fromValue(networkUnitNum).name + "[$networkUnitNum]"
    protected val networkUnit: APsspNetworkUnit get() = APsspNetworkUnit.fromValue(networkUnitNum)

    abstract val loaderName: String
    abstract val bannerUnitSize: AdsviserBannerUnitSize

    abstract fun requestLoad()
    abstract fun onResume()
    abstract fun onPause()
    abstract fun onDestroy()
}