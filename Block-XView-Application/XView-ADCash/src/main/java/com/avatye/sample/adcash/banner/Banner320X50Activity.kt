package com.avatye.sample.adcash.banner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avatye.adcash.AdError
import com.avatye.adcash.BannerAdSize
import com.avatye.adcash.loader.BannerAdLoader
import com.avatye.sample.adcash.AppBaseActivity
import com.avatye.sample.adcash.PlacementParcel
import com.avatye.sample.adcash.databinding.ActivityBanner320x50Binding
import com.avatye.sample.adcash.extraParcel
import com.avatye.sample.adcash.launch

class Banner320X50Activity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, parcel: PlacementParcel) {
            activity.launch(
                intent = Intent(activity, Banner320X50Activity::class.java).apply {
                    putExtra(PlacementParcel.NAME, parcel)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            )
        }
    }

    private val pid: String by lazy {
        extraParcel<PlacementParcel>(PlacementParcel.NAME)?.pid ?: ""
    }

    private val vb: ActivityBanner320x50Binding by lazy {
        ActivityBanner320x50Binding.inflate(LayoutInflater.from(this))
    }

    private var bannerAdLoader: BannerAdLoader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)

        // 인셋 패딩 적용
        applySystemInsets(vb.root)
        vb.pid.text = pid
        vb.textFormLoader.text = "...."
        vb.buttonLoad.setOnClickListener {
            it.isEnabled = false
            vb.textFormLoader.text = "...."
            bannerAdLoader = BannerAdLoader(
                context = this@Banner320X50Activity,
                placementId = pid,
                bannerAdSize = BannerAdSize.W320XH50,
                listener = object : BannerAdLoader.BannerListener {
                    override fun onLoaded(adView: View, size: BannerAdSize) {
                        vb.viewFormLoader.removeAllViews()
                        vb.viewFormLoader.addView(adView)
                        vb.textFormLoader.text = "onLoaded"
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(this@Banner320X50Activity, "Loader::onLoaded", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailed(error: AdError) {
                        it.isEnabled = true
                        vb.textFormLoader.text = "onFailed(${error.errorMessage})"
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(this@Banner320X50Activity, "onFailed::${error.errorMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onClicked() {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(this@Banner320X50Activity, "onClicked", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
            bannerAdLoader?.requestAd()
        }
    }

    override fun onResume() {
        super.onResume()
        bannerAdLoader?.onResume()
    }

    override fun onPause() {
        super.onPause()
        bannerAdLoader?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerAdLoader?.onDestroy()
    }
}