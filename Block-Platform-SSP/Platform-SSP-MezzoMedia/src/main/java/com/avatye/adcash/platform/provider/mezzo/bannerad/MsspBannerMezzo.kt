package com.avatye.adcash.platform.provider.mezzo.bannerad

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.mezzo.MsspErrorUnit
import com.avatye.adcash.platform.provider.mezzo.Settings.printout
import com.avatye.adcash.platform.provider.mezzo.bannerad.loader.MsspBannerLoaderBase
import com.avatye.adcash.platform.provider.mezzo.bannerad.loader.MsspBannerLoaderCallback
import com.avatye.adcash.platform.provider.basement.AdsviserAgeVerifier
import com.avatye.adcash.platform.provider.basement.AdsviserError
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBanner
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerCallback
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerProperty
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnit
import com.avatye.adcash.platform.provider.mezzo.bannerad.loader.MsspBannerLoader
import java.lang.ref.WeakReference
import java.util.LinkedList
import java.util.Queue

class MsspBannerMezzo(
    private val context: Context,
    private val placementAppKey: String,
    private val properties: List<AdsviserBannerProperty>,
    private val placementTimeout: Long,
    private val ageVerifier: AdsviserAgeVerifier,
    private val storeUrls: String,
    private val callback: AdsviserBannerCallback
) : AdsviserBanner(), MsspBannerLoaderCallback {

    private val sourceName = "MsspBannerAdsviser"
    private val loaderQueues: Queue<AdsviserBannerProperty> = LinkedList()
    private val weakContext = WeakReference(context)
    private var currentLoader: MsspBannerLoaderBase? = null
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
        onDestroy()
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
                        error = MsspErrorUnit.of(errorUnit = MsspErrorUnit.NOT_EXISTS_QUEUE)
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
            currentLoader = null
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

    private fun loaderFactory(property: AdsviserBannerProperty): MsspBannerLoaderBase? {
        return if (weakContext.get() != null) {
            when (property.unitType) {
                AdsviserBannerUnit.BANNER -> {
                    MsspBannerLoader(
                        context = context,
                        placementAppKey = placementAppKey,
                        placementId = property.placementId,
                        placementSize = property.unitSize,
                        storeUrls = storeUrls,
                        callback = this@MsspBannerMezzo
                    )
                }
                else -> null
            }
        } else {
            null
        }
    }
}