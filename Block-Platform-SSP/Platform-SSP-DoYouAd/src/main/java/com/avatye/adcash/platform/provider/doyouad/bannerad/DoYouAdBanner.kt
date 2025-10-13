package com.avatye.adcash.platform.provider.doyouad.bannerad

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.basement.AdsviserAgeVerifier
import com.avatye.adcash.platform.provider.basement.AdsviserError
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBanner
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerCallback
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerProperty
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnit
import com.avatye.adcash.platform.provider.doyouad.DoYouAdErrorUnit
import com.avatye.adcash.platform.provider.doyouad.Settings.printout
import com.avatye.adcash.platform.provider.doyouad.bannerad.loader.DoYouAdBannerLoader
import com.avatye.adcash.platform.provider.doyouad.bannerad.loader.DoYouAdBannerLoaderBase
import com.avatye.adcash.platform.provider.doyouad.bannerad.loader.DoYouAdBannerLoaderCallback
import java.lang.ref.WeakReference
import java.util.LinkedList
import java.util.Queue

class DoYouAdBanner(
    private val context: Context,
    private val placementAppKey: String,
    private val properties: List<AdsviserBannerProperty>,
    private val placementTimeout: Long,
    private val ageVerifier: AdsviserAgeVerifier,
    private val callback: AdsviserBannerCallback
) : AdsviserBanner(), DoYouAdBannerLoaderCallback {

    private val sourceName = "DoYouAdBanner"
    private val loaderQueues: Queue<AdsviserBannerProperty> = LinkedList()
    private val weakContext = WeakReference(context)
    private var currentLoader: DoYouAdBannerLoaderBase? = null
    override val propertySize: Int = properties.size

    // region # leak-handler
    private val leakHandler = LeakHandler()

    private class LeakHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
        }
    }

    private val timeoutRunnable: Runnable = Runnable {
        printout.info(sourceName = sourceName) {
            "onTimeout { loader: ${currentLoader?.loaderName}, timeout: $placementTimeout }"
        }
        // release current loader
        currentLoader?.onDestroy()
        currentLoader = null
        // request next loader
        weakContext.isAvailable {
            poll()
        }
    }

    private fun startTimeout() {
        kotlin.runCatching {
            leakHandler.removeCallbacks(timeoutRunnable)
        }.onFailure {
            printout.error(sourceName = sourceName, throwable = it) {
                "startTimeout::leakHandler.removeCallbacks::exception"
            }
        }
        printout.info(sourceName = sourceName) {
            "startTimeout 'LeakHandler started($placementTimeout)'"
        }
        leakHandler.postDelayed(timeoutRunnable, placementTimeout)
    }

    private fun stopTimeout() {
        printout.info(sourceName = sourceName) {
            "stopTimeout 'LeakHandler removed'"
        }
        kotlin.runCatching {
            leakHandler.removeCallbacks(timeoutRunnable)
        }.onFailure {
            printout.error(sourceName = sourceName, throwable = it) {
                "stopTimeout::leakHandler.removeCallbacks::exception"
            }
        }
    }
    // endregion

    init {
        properties.forEach {
            loaderQueues.add(it)
        }
        printout.info(sourceName = sourceName) {
            "init::makeQueue { queueCount: ${loaderQueues.size}, queue: $loaderQueues }"
        }
    }

    private fun poll() {
        weakContext.isAvailable {
            if (!ageVerifier.isVerified()) {
                callback.onNeedAgeVerification()
            } else {
                loaderQueues.poll()?.produce {
                    printout.info(sourceName = sourceName) {
                        "poll { queueCount: ${loaderQueues.size}, bannerSize: ${it.unitSize.name}, placementID: ${it.placementId} }"
                    }
                    startTimeout()
                    currentLoader = loaderFactory(it)
                    currentLoader?.requestLoad()
                } ?: run {
                    printout.info(sourceName = sourceName) {
                        "poll { queue: empty }"
                    }
                    stopTimeout()
                    callback.onFailed(
                        error = DoYouAdErrorUnit.of(errorUnit = DoYouAdErrorUnit.ERROR)
                    )
                }
            }
        }
    }

    override fun requestAD() {
        weakContext.isAvailable {
            poll()
        }
    }

    override fun onResume() {
        weakContext.isAvailable {
            runCatching {
                currentLoader?.onResume()
            }.onFailure {
                printout.error(sourceName = sourceName) {
                    "onResume::exception '$it'"
                }
            }
        }
    }

    override fun onPause() {
        weakContext.isAvailable {
            runCatching {
                currentLoader?.onPause()
            }.onFailure {
                printout.error(sourceName = sourceName) {
                    "onPause::exception '$it'"
                }
            }
        }
    }

    override fun onDestroy() {
        runCatching {
            currentLoader?.onDestroy()
        }.onFailure {
            printout.error(sourceName = sourceName) {
                "onDestroy::exception '$it'"
            }
        }
    }

    override fun onLoaded(view: View, networkUnitName: String) {
        stopTimeout()
        weakContext.isAvailable {
            currentLoader?.produce {
                printout.info(sourceName = sourceName) {
                    "onLoaded { loader: ${it.loaderName} }"
                }
                callback.onLoaded(adView = view, unitSize = it.bannerUnitSize, networkUnitName = "")
            } ?: poll()
        }
    }

    override fun onReLoaded(view: View, networkUnitName: String) {
        printout.info(sourceName = sourceName) {
            "onReLoaded"
        }
    }

    override fun onFailed(error: AdsviserError) {
        printout.info(sourceName = sourceName) {
            "onFailed { loader: ${currentLoader?.loaderName}, error: $error }"
        }
        stopTimeout()
        weakContext.isAvailable {
            currentLoader?.onDestroy()
            currentLoader = null
            poll()
        }
    }


    override fun onClicked() {
        printout.info(sourceName = sourceName) {
            "onClicked"
        }
        callback.onClicked()
    }

    private fun loaderFactory(property: AdsviserBannerProperty): DoYouAdBannerLoaderBase? {
        return if (weakContext.get() != null) {
            when (property.unitType) {
                AdsviserBannerUnit.BANNER -> {
                    DoYouAdBannerLoader(
                        context = context,
                        placementAppKey = placementAppKey,
                        placementId = property.placementId,
                        placementSize = property.unitSize,
                        callback = this@DoYouAdBanner
                    )
                }

                else -> null
            }
        } else {
            null
        }
    }
}