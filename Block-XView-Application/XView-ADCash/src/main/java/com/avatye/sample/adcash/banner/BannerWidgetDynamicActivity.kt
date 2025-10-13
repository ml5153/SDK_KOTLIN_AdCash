package com.avatye.sample.adcash.banner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.avatye.sample.adcash.PlacementParcel
import com.avatye.adcash.AdError
import com.avatye.adcash.view.BannerAdView
import com.avatye.sample.adcash.databinding.ActivityBannerWidgetDynamicBinding
import android.os.Handler
import android.os.Looper
import com.avatye.sample.adcash.AppBaseActivity
import com.avatye.sample.adcash.extraParcel
import com.avatye.sample.adcash.launch

class BannerWidgetDynamicActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, parcel: PlacementParcel) {
            activity.launch(
                intent = Intent(activity, BannerWidgetDynamicActivity::class.java).apply {
                    putExtra(PlacementParcel.NAME, parcel)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            )
        }
    }

    private val pid: String by lazy {
        extraParcel<PlacementParcel>(PlacementParcel.NAME)?.pid ?: ""
    }

    private val vb: ActivityBannerWidgetDynamicBinding by lazy {
        ActivityBannerWidgetDynamicBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)

        // 인셋 패딩 적용
        applySystemInsets(vb.root)
        vb.pid.text = pid
        vb.textFormWidget.text = "...."
        vb.bannerAdView.setPlacementId(placementId = pid)
        vb.bannerAdView.listener = object : BannerAdView.Listener {
            override fun onLoaded() {
                vb.textFormWidget.text = "onLoaded"
            }

            override fun onFailed(adError: AdError) {
                vb.textFormWidget.text = "onFailed { adError: $adError }"
            }

            override fun onClicked() {
                vb.textFormWidget.text = "onClicked"
            }
        }
        vb.bannerAdView.requestAd()
    }

    override fun onResume() {
        super.onResume()
        vb.bannerAdView.onResume()
    }

    override fun onPause() {
        super.onPause()
        vb.bannerAdView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        vb.bannerAdView.onDestroy()
    }

}