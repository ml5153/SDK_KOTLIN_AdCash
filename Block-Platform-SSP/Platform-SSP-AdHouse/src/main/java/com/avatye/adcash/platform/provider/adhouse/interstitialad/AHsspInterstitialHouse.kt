package com.avatye.adcash.platform.provider.adhouse.interstitialad

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.avatye.adcash.platform.library.extension.isAlive
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.provider.adhouse.AHsspErrorUnit
import com.avatye.adcash.platform.provider.adhouse.Settings.printout
import com.avatye.adcash.platform.provider.adhouse.interstitialad.loader.AHsspInterstitialHouseLoader
import com.avatye.adcash.platform.provider.adhouse.interstitialad.loader.AHsspInterstitialLoaderBase
import com.avatye.adcash.platform.provider.adhouse.interstitialad.loader.AHsspInterstitialLoaderCallback
import com.avatye.adcash.platform.provider.basement.AdsviserAgeVerifier
import com.avatye.adcash.platform.provider.basement.AdsviserError
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitial
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialCallback
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialLoaderBase
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialProperty
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit
import java.lang.ref.WeakReference
import java.util.LinkedList
import java.util.Queue

class AHsspInterstitialHouse(
    private val ownerActivity: Activity,
    private val properties: List<AdsviserInterstitialProperty>,
    private val placementTimeout: Long,
    private val ageVerifier: AdsviserAgeVerifier,
    private val callback: AdsviserInterstitialCallback
) : AdsviserInterstitial(), AHsspInterstitialLoaderCallback {

    private val sourceName = "AHsspInterstitialHouse"
    private val loaderQueues: Queue<AdsviserInterstitialProperty> = LinkedList()
    private val weakActivity = WeakReference(ownerActivity)
    private var currentLoader: AdsviserInterstitialLoaderBase? = null
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
        // release current loader
        currentLoader?.onDestroy()
        currentLoader = null
        weakActivity.isAvailable {
            // request next loader
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
            "init:makeQueue { queueCount: ${loaderQueues.size}, queue: $loaderQueues }"
        }
    }

    private fun poll() {
        weakActivity.isAvailable {
            if (!ageVerifier.isVerified()) {
                callback.onNeedAgeVerification()
            } else {
                loaderQueues.poll()?.produce {
                    printout.info(sourceName = sourceName) {
                        "poll { type: ${it.unitType.name}, q-count: ${loaderQueues.size}, pid: ${it.placementID}, v-interval: ${it.videoInterval} }"
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
    }

    override fun requestAD() {
        weakActivity.isAvailable {
            poll()
        }
    }

    override fun onResume() {
        weakActivity.isAvailable {
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
        weakActivity.isAvailable {
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
        weakActivity.isAvailable {
            runCatching {
                currentLoader?.onDestroy()
            }.onFailure {
                printout.error(sourceName = sourceName) {
                    "onDestroy::exception '$it'"
                }
            }
        }
    }

    override fun onLoaded(unitType: AdsviserInterstitialUnit, networkUnitName: String) {
        stopTimeout()
        weakActivity.isAvailable {
            currentLoader?.produce {
                printout.info(sourceName = sourceName) {
                    "onLoaded { loader: ${it.loaderName}, unitType: ${unitType.name}, network: $networkUnitName }"
                }
                callback.onLoaded(loader = it, unitType = unitType, networkUnitName = networkUnitName)
            } ?: poll()
        }
    }

    override fun onFailed(error: AdsviserError) {
        printout.info(sourceName = sourceName) {
            "onFailed { loader: ${currentLoader?.loaderName}, error: $error }"
        }
        stopTimeout()
        weakActivity.isAvailable {
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
    }

    override fun onOpened() {
        printout.info(sourceName = sourceName) {
            "onOpened { loader: ${currentLoader?.loaderName} }"
        }
        stopTimeout()
        weakActivity.isAvailable {
            callback.onOpened()
        }
    }

    override fun onClosed(isCompleted: Boolean) {
        printout.info(sourceName = sourceName) {
            "onClosed { loader: ${currentLoader?.loaderName}, isComplete: $isCompleted }"
        }
        weakActivity.isAvailable {
            callback.onComplete(completed = isCompleted)
        }
    }

    override fun onClicked() {
        printout.info(sourceName = sourceName) {
            "onClicked { loader: ${currentLoader?.loaderName} }"
        }
        weakActivity.isAvailable {
            callback.onClicked()
        }
    }

    private fun loaderFactory(property: AdsviserInterstitialProperty): AHsspInterstitialLoaderBase? {
        return if (weakActivity.get() != null && weakActivity.get()?.isAlive == true) {
            AHsspInterstitialHouseLoader(
                activity = ownerActivity,
                imageUrl = property.houseImageUrl,
                landingUrl = property.houseLandingUrl,
                callback = this@AHsspInterstitialHouse
            )
        } else {
            null
        }
    }
}