package com.avatye.adcash.platform.provider.adhouse.bannerad.loader

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.library.extension.toPX
import com.avatye.adcash.platform.provider.adhouse.AHsspErrorUnit
import com.avatye.adcash.platform.provider.adhouse.R
import com.avatye.adcash.platform.provider.adhouse.Settings.printout
import com.avatye.adcash.platform.provider.basement.AdsviserError
import com.avatye.adcash.platform.provider.basement.AdsviserProviderUnit
import com.avatye.adcash.platform.provider.basement.bannerad.AdsviserBannerUnitSize
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.lang.ref.WeakReference


internal class AHsspBannerLoader(
    private val context: Context,
    private val placementSize: AdsviserBannerUnitSize,
    private val imageUrl: String,
    private val landingUrl: String,
    private val callback: AHsspBannerLoaderCallback
) : AHsspBannerLoaderBase() {

    private val sourceName = "AHsspBannerLoader"
    private val weakContext = WeakReference(context)
    override val loaderName: String get() = "BannerHouseLoader"
    override val bannerUnitSize: AdsviserBannerUnitSize get() = placementSize

    private var imageTarget: CustomTarget<Drawable>? = null

    /*private val requestListener: RequestListener<Drawable> = object : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
            requestErrorCallback()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: com.bumptech.glide.request.target.Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            if (resource != null) {
                makeHouseBannerView(resource = resource)?.produce { adview ->
                    callback.onLoaded(view = adview, networkUnitName = AdsviserProviderUnit.HOUSE.providerName)
                } ?: run {
                    requestErrorCallback()
                }
            } else {
                requestErrorCallback()
            }
            return false
        }
    }*/

    private fun requestErrorCallback(errorUnit: AHsspErrorUnit) {
        callback.onFailed(
            error = AdsviserError(
                code = errorUnit.code,
                message = errorUnit.message,
                isBlocked = false,
                adsviserName = AdsviserProviderUnit.HOUSE.providerName,
                networkUnitName = ""
            )
        )
    }

    /*override fun requestLoad() {
        printout.info(sourceName = sourceName, trace = { "requestLoad" })
        weakContext.get()?.produce {
            Glide.with(it).load(imageUrl).listener(requestListener).preload()
        } ?: run {
            callback.onFailed(
                error = AdsviserError(
                    isBlocked = false,
                    code = AHsspErrorUnit.EXCEPTION_CONTEXT_IS_NULL.code,
                    message = AHsspErrorUnit.EXCEPTION_CONTEXT_IS_NULL.message,
                    adsviserName = AdsviserProviderUnit.HOUSE.providerName,
                    networkUnitName = AdsviserProviderUnit.HOUSE.providerName
                )
            )
        }
    }*/

    override fun requestLoad() {
        printout.info(sourceName = sourceName, trace = { "requestLoad" })

        // Context를 한 번만 안전하게 가져옴
        val currentContext = weakContext.get() ?: run {
            requestErrorCallback(AHsspErrorUnit.INVALID_HOUSE_AD)
            return
        }

        imageTarget = object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                makeHouseBannerView(resource)?.produce { adview ->
                    callback.onLoaded(view = adview, networkUnitName = AdsviserProviderUnit.HOUSE.providerName)
                } ?: requestErrorCallback(AHsspErrorUnit.INVALID_HOUSE_AD)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                requestErrorCallback(AHsspErrorUnit.INVALID_HOUSE_AD)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // 필요 시 리소스 정리
            }
        }

        try {
            Glide.with(currentContext)
                .load(imageUrl)
                .into(imageTarget!!)
        } catch (e: Exception) {
            printout.error(throwable = e, sourceName = sourceName, trace = { "Glide load Failed" })
            requestErrorCallback(AHsspErrorUnit.INVALID_HOUSE_AD)
        }
    }

    override fun onResume() {
        printout.info(sourceName = sourceName, trace = { "onResume" })
    }

    override fun onPause() {
        printout.info(sourceName = sourceName, trace = { "onPause" })
    }

    override fun onDestroy() {
        printout.info(sourceName = sourceName, trace = { "onDestroy" })
        weakContext.get()?.let { context ->
            imageTarget?.let { target ->
                Glide.with(context).clear(target)
            }
        }
    }

    private fun makeHouseBannerView(resource: Drawable): View? {
        return try {
            if (weakContext.get() != null) {
                val bannerView = LayoutInflater.from(weakContext.get()).inflate(R.layout.acb_adcash_ssp_house_container_banner_house, null)
                bannerView.findViewById<ImageView>(R.id.banner_house_content).apply {
                    when (placementSize) {
                        AdsviserBannerUnitSize.W320XH50 -> {
                            layoutParams.width = 320.toPX.toInt()
                            layoutParams.height = 50.toPX.toInt()
                        }

                        AdsviserBannerUnitSize.W320XH100,
                        AdsviserBannerUnitSize.DYNAMIC -> {
                            layoutParams.width = 320.toPX.toInt()
                            layoutParams.height = 100.toPX.toInt()
                        }

                        AdsviserBannerUnitSize.W300XH250 -> {
                            layoutParams.width = 300.toPX.toInt()
                            layoutParams.height = 250.toPX.toInt()
                        }

                        AdsviserBannerUnitSize.W320XH480 -> {
                            layoutParams.width = 300.toPX.toInt()
                            layoutParams.height = 480.toPX.toInt()
                        }
                    }
                    setImageDrawable(resource)
                    setOnClickListener {
                        callback.onClicked()
                        try {
                            val marketIntent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(landingUrl)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            weakContext.get()?.startActivity(marketIntent)
                        } catch (e: ActivityNotFoundException) {
                            printout.error(throwable = e, sourceName = sourceName, trace = { "startActivity Failed" })
                        }
                    }
                }
                bannerView
            } else {
                null
            }
        } catch (e: Exception) {
            printout.error(
                throwable = e,
                sourceName = sourceName,
                trace = { "makeHouseBannerView::LayoutInflate" }
            )
            null
        }
    }
}