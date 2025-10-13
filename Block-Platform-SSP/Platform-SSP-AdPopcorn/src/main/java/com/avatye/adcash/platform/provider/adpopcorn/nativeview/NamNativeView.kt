package com.avatye.adcash.platform.provider.adpopcorn.nativeview

import android.content.Context
import com.avatye.adcash.platform.provider.adpopcorn.Settings.printout
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect.Mediation
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect.Size
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.MediationNativeViewBinder
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.NamNativeViewBinder

internal class NamNativeView(val context: Context?) : IMediationConnect {

    override val mediation: Mediation = Mediation.NAM

    override fun requestNativeViewBinder(size: Size): MediationNativeViewBinder? {
        return if (size == Size.DYNAMIC || size == Size.W320XH100) {
            context?.let { createNamBinder(it) }
        } else {
            null
        }
    }

    private fun createNamBinder(context: Context): MediationNativeViewBinder? {
        val className = "com.avatye.adcash.mediation.archive.nam.NAMNativeView"

        return runCatching {
            val clazz = Class.forName(className)

            val layoutId = clazz
                .getMethod("getLayoutId", Context::class.java)
                .invoke(null, context) as Int

            val viewId = clazz
                .getMethod("getViewId", Context::class.java)
                .invoke(null, context) as Int

            printout.info(sourceName = "NamNativeView") {
                "createNamBinder { layoutId: $layoutId, viewId: $viewId }"
            }

            MediationNativeViewBinder.Builder().setNamNativeViewBinder(
                NamNativeViewBinder.Builder(
                    nativeAdLayoutId = layoutId,
                    nativeAdViewId = viewId
                ).build()
            ).build()
        }.onFailure { e ->
            printout.error(sourceName = "NamNativeView", throwable = e) {
                "mediation failed for $className"
            }
        }.onSuccess {
            printout.info(sourceName = "NamNativeView") {
                "mediation success for $className"
            }
        }.getOrNull()
    }
}