package com.avatye.adcash.platform.provider.mezzo.bannerad.loader

/*
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import com.avatye.adcash.platform.provider.basement.AdsviserProviderUnit
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize
import com.avatye.adcash.platform.provider.mezzo.MsspErrorUnit
import com.avatye.adcash.platform.provider.mezzo.Settings.printout
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

private const val AD_MAN_VIEW_CLASS = "com.mmc.man.view.AdManView"
private const val AD_DATA_CLASS = "com.mmc.man.data.AdData"
private const val AD_LISTENER_INTERFACE = "com.mmc.man.AdListener"

internal class MsspBannerRefLoader(
    context: Context,
    private val placementAppKey: String,
    private val placementId: String,
    private val placementSize: AdsviserBannerUnitSize,
    private val storeUrls: String,
    private val callback: MsspBannerLoaderCallback
) : MsspBannerLoaderBase() {

    private val adManViewClass: Class<*>? = try {
        Class.forName(AD_MAN_VIEW_CLASS)
    } catch (e: ClassNotFoundException) {
        null
    }
    private val adDataClass: Class<*>? = try {
        Class.forName(AD_DATA_CLASS)
    } catch (e: ClassNotFoundException) {
        null
    }
    private val adListenerInterface: Class<*>? =
        try {
            Class.forName(AD_LISTENER_INTERFACE)
        } catch (e: ClassNotFoundException) {
            null
        }

    private val sourceName = "MsspBannerRefLoader"
    private val adType = "mezzobannerid"
    private val weakContext = WeakReference(context)

    override val loaderName: String get() = "BannerLoader"
    override val bannerUnitSize: AdsviserBannerUnitSize get() = placementSize

    private var banner: Any? = null
    private var adData: Any? = null

    init {
        if (adManViewClass == null || adDataClass == null || adListenerInterface == null) {
            fail(MsspErrorUnit.EXCEPTION, "Ad library classes not found. Disabling feature.")
        }
    }

    private fun initializer(): Boolean {
        val wContext = weakContext.get() ?: run {
            fail(MsspErrorUnit.EXCEPTION_LOADER_IS_NULL, "Context is null")
            return false
        }

        try {
            val (width, height) = when (placementSize) {
                AdsviserBannerUnitSize.W320XH50 -> 320 to 50
                AdsviserBannerUnitSize.W320XH100 -> 320 to 100
                else -> {
                    fail(MsspErrorUnit.BLOCKED_SIZE, "Unsupported size")
                    return false
                }
            }

            val appKeyInt = placementAppKey.toIntOrNull() ?: run {
                fail(MsspErrorUnit.INVALID_PARAMETER, "appKey is invalid")
                return false
            }

            val placementIdInt = placementId.toIntOrNull() ?: run {
                fail(MsspErrorUnit.INVALID_PARAMETER, "placementID is invalid")
                return false
            }

            adData = adData ?: createInstance(adDataClass) ?: return false
            banner = banner ?: createInstance(adManViewClass, Context::class.java to wContext)
                    ?: return false

            val appName = wContext.applicationInfo.loadLabel(wContext.packageManager).toString()

            invokeMethod(
                adData,
                "major",
                String::class.java to adType,
                String::class.java to "API_BANNER",
                Int::class.java to 1349,
                Int::class.java to appKeyInt,
                Int::class.java to placementIdInt,
                String::class.java to storeUrls,
                String::class.java to wContext.packageName,
                String::class.java to appName,
                Int::class.java to width,
                Int::class.java to height
            )

            invokeMethod(adData, "setUserAgeLevel", Int::class.java to -1)
            invokeMethod(adData, "isPermission", String::class.java to "NOT_USED")
            invokeMethod(
                adData,
                "setApiModule",
                String::class.java to "NOT_USED",
                String::class.java to "NOT_USED"
            )

            return true
        } catch (e: Exception) {
            fail(MsspErrorUnit.EXCEPTION, "Initialization failed: ${e.message}", e)
            return false
        }
    }

    override fun requestLoad() {
        printout.info(
            sourceName = sourceName,
            trace = { "requestLoad -> placementId: $placementId" })

        if (initializer()) {
            loadBannerAd()
        }
    }

    private fun loadBannerAd() {
        val bannerView = banner ?: run {
            fail(MsspErrorUnit.EXCEPTION_LOADER_IS_NULL, "Banner object is null")
            return
        }

        val dataClass = adDataClass ?: run {
            fail(MsspErrorUnit.EXCEPTION_LOADER_IS_NULL, "AdData class not found")
            return
        }

        val listenerInterface = adListenerInterface ?: run {
            fail(MsspErrorUnit.EXCEPTION_LOADER_IS_NULL, "AdListener interface not found")
            return
        }

        try {
            val adListenerProxy = Proxy.newProxyInstance(
                listenerInterface.classLoader,
                arrayOf(listenerInterface),
                AdListenerHandler(callback, sourceName)
            )

            invokeMethod(
                bannerView,
                "setData",
                dataClass to adData,
                listenerInterface to adListenerProxy
            )
            invokeMethod(
                bannerView,
                "request",
                Handler::class.java to Handler(Looper.getMainLooper())
            )
        } catch (e: Exception) {
            fail(MsspErrorUnit.SERVER_TIMEOUT, "Ad request failed: ${e.message}", e)
        }
    }

    override fun onResume() {
        */
/* No-op *//*

    }

    override fun onPause() {
        */
/* No-op *//*

    }

    override fun onDestroy() {
        invokeSimpleMethodOnBanner("onDestroy")
        onClear()
    }

    override fun onClear() {
        banner = null
        adData = null
    }

    private fun invokeSimpleMethodOnBanner(methodName: String) {
        banner?.let { invokeMethod(it, methodName) }
    }

    private fun createInstance(clazz: Class<*>?, vararg args: Pair<Class<*>, Any>): Any? {
        return try {
            clazz?.getConstructor(*args.map { it.first }.toTypedArray())
                ?.newInstance(*args.map { it.second }.toTypedArray())
        } catch (e: Exception) {
            fail(
                MsspErrorUnit.EXCEPTION,
                "Instance creation failed for ${clazz?.simpleName}: ${e.message}",
                e
            )
            null
        }
    }

    private fun invokeMethod(
        target: Any?,
        methodName: String,
        vararg params: Pair<Class<*>, Any?>
    ): Any? {
        return try {
            val targetClass = target?.javaClass ?: return null
            val method = targetClass.getMethod(methodName, *params.map { it.first }.toTypedArray())
            method.invoke(target, *params.map { it.second }.toTypedArray())
        } catch (e: Exception) {
            val rootCause = if (e is java.lang.reflect.InvocationTargetException) e.cause else e
            fail(
                MsspErrorUnit.EXCEPTION,
                "Method invocation failed: $methodName, ${rootCause?.message ?: "Unknown cause"}",
                rootCause
            )
            null
        }
    }

    private fun fail(error: MsspErrorUnit, traceMessage: String, throwable: Throwable? = null) {
        if (throwable != null) {
            printout.error(sourceName = sourceName, throwable = throwable, trace = { traceMessage })
        } else {
            printout.warn(sourceName = sourceName, trace = { traceMessage })
        }
        callback.onFailed(MsspErrorUnit.of(error))
    }

    private class AdListenerHandler(
        private val callback: MsspBannerLoaderCallback,
        private val sourceName: String
    ) : InvocationHandler {
        override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any? {
            if (args == null) return null
            try {
                when (method.name) {
                    "onAdSuccessCode" -> {
                        (args[0] as? ViewGroup)?.also {
                            printout.info(
                                sourceName = sourceName,
                                trace = { "AdResult::SUCCESS ${args[3]}" })
                            callback.onLoaded(it, AdsviserProviderUnit.MEZZOMEDIA.providerName)
                        } ?: run {
                            printout.warn(
                                sourceName = sourceName,
                                trace = { "onAdSuccessCode callback failed: arugment is not a ViewGroup." })
                            callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.EXCEPTION))
                        }
                    }

                    "onAdFailCode", "onAdErrorCode" -> {
                        val err = (args.getOrNull(3) as? String)?.let(MsspErrorUnit::of)
                            ?: MsspErrorUnit.of(MsspErrorUnit.EXCEPTION)
                        printout.info(
                            sourceName = sourceName,
                            trace = { "AdResult::FAIL/ERROR $err" })
                        callback.onFailed(err)
                    }

                    "onAdEvent" -> {
                        when (args.getOrNull(2)) {
                            "adclick" -> {
                                printout.info(
                                    sourceName = sourceName,
                                    trace = { "AdResult::CLICK" })
                                callback.onClicked()
                            }

                            "close" -> printout.info(
                                sourceName = sourceName,
                                trace = { "AdResult::CLOSE" })

                            "imp" -> printout.info(
                                sourceName = sourceName,
                                trace = { "AdResult::IMP" })
                        }
                    }
                }
            } catch (e: Exception) {
                printout.error(
                    sourceName = sourceName,
                    trace = { "AdListener proxy error (${method.name}): ${e.message}" })
                callback.onFailed(MsspErrorUnit.of(MsspErrorUnit.EXCEPTION))
            }
            return null
        }
    }
}*/
