package com.avatye.adcash.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.avatye.adcash.ADCashSettings.printout
import com.avatye.adcash.AdError
import com.avatye.adcash.AdErrorUnit
import com.avatye.adcash.AppKeySetting
import com.avatye.adcash.BannerAdSize
import com.avatye.adcash.R
import com.avatye.adcash.databinding.AcbAdcashBannerViewBinding
import com.avatye.adcash.platform.library.extension.useRecycle
import com.avatye.adcash.loader.BannerAdLoader

@SuppressLint("Recycle")
class BannerAdView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    interface Listener {
        fun onLoaded()
        fun onFailed(adError: AdError)
        fun onClicked()
    }

    var listener: Listener? = null
    private val sourceName: String = "BannerAdView"
    private var allowSkip: Boolean = false

    //private var isResumed: Boolean = false
    private var placementId: String = ""
    private var bannerAdSize: BannerAdSize = BannerAdSize.DYNAMIC
    private var bannerLoader: BannerAdLoader? = null
    private var mediationExtra: HashMap<String, Any>? = null

    private val vb by lazy {
        AcbAdcashBannerViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.BannerAdView).useRecycle {
            this.bannerAdSize =
                when (it.getInt(R.styleable.BannerAdView_adCashBannerAdViewSize, 0)) {
                    1 -> BannerAdSize.W320XH50
                    2 -> BannerAdSize.W320XH100
                    3 -> BannerAdSize.W300XH250
                    4 -> BannerAdSize.DYNAMIC
                    else -> BannerAdSize.DYNAMIC
                }
            this.placementId =
                it.getString(R.styleable.BannerAdView_adCashBannerAdViewPlacementId) ?: ""
        }
    }

    private var unitAppKeySetting: AppKeySetting? = null

    fun setAppKeySetting(appKeySetting: AppKeySetting) {
        this.unitAppKeySetting = appKeySetting
    }

    fun setSkip(allowSkip: Boolean) {
        this.allowSkip = allowSkip
    }

    fun setBannerAdSize(size: BannerAdSize) {
        this.bannerAdSize = size
    }

    fun setPlacementId(placementId: String) {
        this.placementId = placementId
    }

    fun setMediationExtra(extra: HashMap<String, Any>?) {
        this.mediationExtra = extra
    }

    private val callback = object : BannerAdLoader.BannerListener {
        override fun onLoaded(adView: View, size: BannerAdSize) {
            vb.adcashBannerContainer.removeAllViews()
            vb.adcashBannerContainer.addView(adView)
            listener?.onLoaded()
        }

        override fun onFailed(error: AdError) {
            listener?.onFailed(adError = error)
        }

        override fun onClicked() {
            listener?.onClicked()
        }
    }

    private fun executeADLoader() {
        if (allowSkip) {
            printout.error(
                sourceName = sourceName,
                trace = { "$sourceName: execute ad load -> skipped" }
            )
            return
        }

        if (placementId.isEmpty()) {
            printout.error(
                sourceName = sourceName,
                trace = { "$sourceName: placement id is empty. placement id must be not empty or null." }
            )
            listener?.onFailed(adError = AdError.of(AdErrorUnit.NOT_EXISTS_APID_CAMPAIGN))
            return
        }

        try {
            printout.info(
                sourceName = sourceName,
                trace = { "requestAd { placementId: $placementId, bannerAdSize: ${bannerAdSize.name} }" }
            )
            if (unitAppKeySetting != null) {
                bannerLoader = BannerAdLoader(
                    context = context,
                    placementId = this.placementId,
                    bannerAdSize = this.bannerAdSize,
                    listener = callback
                ).apply {
                    setAppKeySetting(appKeySetting = unitAppKeySetting!!)
                }
            } else {
                bannerLoader = BannerAdLoader(
                    context = context,
                    placementId = this.placementId,
                    bannerAdSize = this.bannerAdSize,
                    listener = callback
                )
            }
            bannerLoader?.setMediationExtra(extra = mediationExtra)
            bannerLoader?.requestAd()
        } catch (e: Exception) {
            printout.error(sourceName = sourceName, throwable = e)
            listener?.onFailed(
                adError = AdError(
                    errorCode = AdErrorUnit.EXCEPTION.code,
                    errorMessage = "${AdErrorUnit.EXCEPTION.message} { ${e.message} }"
                )
            )
        }
    }

    fun requestAd() = executeADLoader()

    fun onResume() {
        if (allowSkip) {
            printout.error(
                sourceName = sourceName,
                trace = { "$sourceName: onResume -> skipped" }
            )
            return
        }
        /*// loader
        if (bannerLoader == null) {
            printout.info(
                sourceName = sourceName,
                trace = { "onResume -> requestAd" }
            )
            if (placementId.isNotEmpty()) {
                requestAd()
            }
        }
        // resume
        if (isResumed) {
            bannerLoader?.onResume()
        } else {
            isResumed = true
        }
         */
        if (bannerLoader == null) {
            printout.info(
                sourceName = sourceName,
                trace = { "onResume -> bannerLoader is null" }
            )
        }
        bannerLoader?.onResume()
    }

    fun onPause() {
        bannerLoader?.onPause()
    }

    fun onDestroy() {
        bannerLoader?.onDestroy()
    }

}