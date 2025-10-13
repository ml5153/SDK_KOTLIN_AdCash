package com.avatye.adcash.platform.provider.adhouse.interstitialad.loader

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import com.avatye.adcash.platform.library.extension.isAvailable
import com.avatye.adcash.platform.provider.adhouse.AHsspErrorUnit
import com.avatye.adcash.platform.provider.adhouse.Settings.printout
import com.avatye.adcash.platform.provider.adhouse.databinding.AcbAdcashSspHouseContainerInterstitialHouse320x480Binding
import com.avatye.adcash.platform.provider.adhouse.interstitialad.AHsspInterstitialViewer
import com.avatye.adcash.platform.provider.basement.interstitialad.AdsviserInterstitialUnit
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.lang.ref.WeakReference

internal class AHsspInterstitialHouseLoader(
    private val activity: Activity,
    private val imageUrl: String,
    private val landingUrl: String,
    private val callback: AHsspInterstitialLoaderCallback
) : AHsspInterstitialLoaderBase() {

    private val sourceName = "AHsspInterstitialHouseLoader"
    private val weakActivity = WeakReference(activity)
    private var leakView: AcbAdcashSspHouseContainerInterstitialHouse320x480Binding? = null
    private var customTarget: CustomTarget<Drawable>? = null
    private var isResourceLoaded = false

    init {
        try {
            weakActivity.isAvailable { wActivity ->
                leakView = AcbAdcashSspHouseContainerInterstitialHouse320x480Binding.inflate(
                    LayoutInflater.from(wActivity)
                )
            }
        } catch (e: Exception) {
            printout.error(sourceName = sourceName) { "init binding failed: ${e.message}" }
            requestErrorCallback()
        }
    }

    private fun requestErrorCallback() {
        try {
            weakActivity.isAvailable {
                callback.onFailed(
                    error = AHsspErrorUnit.of(AHsspErrorUnit.INVALID_HOUSE_AD)
                )
            }
        } catch (e: Exception) {
            printout.error(sourceName = sourceName) { "requestErrorCallback failed: ${e.message}" }
        }
    }

    override val loaderName: String get() = "InterstitialHouseLoader"
    override val interstitialUnit: AdsviserInterstitialUnit =
        AdsviserInterstitialUnit.INTERSTITIAL_HOUSE
    override val isLoaded: Boolean get() = isResourceLoaded

    override fun requestLoad() {
        printout.info(sourceName = sourceName) { "requestLoad" }
        weakActivity.isAvailable { wActivity ->
            leakView?.let { lView ->
                try {
                    customTarget = object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            try {
                                isResourceLoaded = true
                                lView.interstitialHouseContent.setImageDrawable(resource)
                                lView.interstitialHouseContent.setOnClickListener {
                                    try {
                                        val intent =
                                            Intent(Intent.ACTION_VIEW, Uri.parse(landingUrl))
                                                .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                                        wActivity.startActivity(intent)
                                        callback.onClicked()
                                    } catch (e: Exception) {
                                        printout.error(sourceName = sourceName) { "click intent failed: ${e.message}" }
                                        isResourceLoaded = false
                                        requestErrorCallback()
                                    }
                                }
                                callback.onLoaded(
                                    unitType = interstitialUnit,
                                    networkUnitName = sourceName
                                )
                            } catch (e: Exception) {
                                printout.error(sourceName = sourceName) { "onResourceReady callback error: ${e.message}" }
                                requestErrorCallback()
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            try {
                                lView.interstitialHouseContent.setImageDrawable(placeholder)
                                lView.interstitialHouseContent.setOnClickListener(null)
                            } catch (e: Exception) {
                                printout.error(sourceName = sourceName) { "onLoadCleared failed: ${e.message}" }
                            }
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            printout.error(sourceName = sourceName) { "Glide load failed" }
                            isResourceLoaded = false
                            requestErrorCallback()
                        }
                    }.also { target ->
                        try {
                            Glide.with(wActivity)
                                .load(imageUrl)
                                .skipMemoryCache(true)
                                .placeholder(null)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(target)
                        } catch (e: Exception) {
                            printout.error(sourceName = sourceName) { "Glide.with(...).into() failed: ${e.message}" }
                            isResourceLoaded = false
                            requestErrorCallback()
                        }
                    }
                } catch (e: Exception) {
                    printout.error(sourceName = sourceName) { "requestLoad setup failed: ${e.message}" }
                    requestErrorCallback()
                }
            } ?: run {
                printout.warn(sourceName = sourceName) { "leakView is null in requestLoad" }
                requestErrorCallback()
            }
        }
    }

    override fun show(blockCallback: (success: Boolean) -> Unit) {
        printout.info(sourceName = sourceName) { "show" }
        weakActivity.isAvailable { wActivity ->
            leakView?.let { lView ->
                if (!isLoaded) {
                    blockCallback(false)
                    return@isAvailable
                }

                try {
                    AHsspInterstitialViewer.create(
                        activity = wActivity,
                        adView = lView.root,
                        actionDismiss = object : AHsspInterstitialViewer.DismissActionCallback {
                            override fun onDismiss() {
                                cleanUp()
                                callback.onClosed(isCompleted = true)
                            }
                        }
                    ).show(cancelable = false) { opened ->
                        try {
                            if (opened) callback.onOpened()
                            blockCallback(opened)
                        } catch (e: Exception) {
                            printout.error(sourceName = sourceName) { "show callback failed: ${e.message}" }
                            blockCallback(false)
                        }
                    }
                } catch (e: Exception) {
                    printout.error(sourceName = sourceName) { "show failed: ${e.message}" }
                    blockCallback(false)
                }
            } ?: run {
                printout.warn(sourceName = sourceName) { "leakView is null in show()" }
                blockCallback(false)
            }
        }
    }

    override fun onResume() {
        printout.info(sourceName = sourceName) { "onResume" }
    }

    override fun onPause() {
        printout.info(sourceName = sourceName) { "onPause" }
    }

    override fun onDestroy() {
        printout.info(sourceName = sourceName) { "onDestroy" }
        cleanUp()
    }

    private fun cleanUp() {
        try {
            weakActivity.isAvailable { act ->
                customTarget?.let { Glide.with(act).clear(it) }
                leakView?.interstitialHouseContent?.let { iv ->
                    Glide.with(iv).clear(iv)
                    iv.setImageDrawable(null)
                    iv.setOnClickListener(null)
                }
            }
        } catch (e: Exception) {
            printout.error(sourceName = sourceName) { "onDestroy clear failed: ${e.message}" }
        } finally {
            customTarget = null
            leakView = null
        }
    }
}