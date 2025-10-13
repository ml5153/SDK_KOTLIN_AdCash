package com.avatye.adcash.platform.provider.adhouse.bannerad

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.adhouse.AHsspErrorUnit
import com.avatye.adcash.platform.provider.adhouse.Settings.printout
import com.avatye.adcash.platform.provider.adhouse.bannerad.loader.AHsspBannerLoader
import com.avatye.adcash.platform.provider.adhouse.bannerad.loader.AHsspBannerLoaderBase
import com.avatye.adcash.platform.provider.adhouse.bannerad.loader.AHsspBannerLoaderCallback
import com.avatye.adcash.platform.provider.basement.AdsviserAgeVerifier
import com.avatye.adcash.platform.provider.basement.AdsviserError
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBanner
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerCallback
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerProperty
import java.lang.ref.WeakReference
import java.util.LinkedList
import java.util.Queue

class AHsspBannerHouse(
    private val context: Context,
    private val placementTimeout: Long,
    private val properties: List<AdsviserBannerProperty>,
    private val ageVerifier: AdsviserAgeVerifier,
    private val callback: AdsviserBannerCallback
): AdsviserBanner(), AHsspBannerLoaderCallback {

    private val sourceName = "AHsspBannerHouse"
    private val weakContext = WeakReference(context)
    private val loaderQueues: Queue<AdsviserBannerProperty> = LinkedList()
    private var currentLoader: AHsspBannerLoaderBase? = null
    override val propertySize = properties.size

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
        stopTimeout()
        // release current loader
        currentLoader?.onDestroy()
        currentLoader = null
        callback.onFailed(
            error = AHsspErrorUnit.of(
                errorUnit = AHsspErrorUnit.NOT_EXISTS_QUEUE
            )
        )
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
                    error = AHsspErrorUnit.of(
                        errorUnit = AHsspErrorUnit.NOT_EXISTS_QUEUE
                    )
                )
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
        currentLoader?.produce {
            printout.info(sourceName = sourceName) {
                "onLoaded { loader: ${it.loaderName} }"
            }
            callback.onLoaded(adView = view, unitSize = it.bannerUnitSize, networkUnitName = "")
        }?: poll()
    }

    override fun onFailed(error: AdsviserError) {
        printout.info(sourceName = sourceName) {
            "onFailed { loader: ${currentLoader?.loaderName}, error: $error }"
        }
        stopTimeout()
        if (error.isBlocked) {
            currentLoader?.onDestroy()
            currentLoader = null
            loaderQueues.clear()
            callback.onFailed(
                error = AHsspErrorUnit.of(
                    errorUnit = AHsspErrorUnit.BLOCKED
                )
            )
        } else {
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

    private fun loaderFactory(property: AdsviserBannerProperty): AHsspBannerLoaderBase? {
        return if (weakContext.get() != null) {
            AHsspBannerLoader(
                context = context,
                placementSize = property.unitSize,
                imageUrl = property.houseImageUrl,
                landingUrl = property.houseLandingUrl,
                callback = this@AHsspBannerHouse
            )
        } else {
            null
        }
    }
}