package com.avatye.sample.adcash

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.avatye.sample.adcash.banner.Banner300X250Activity
import com.avatye.sample.adcash.banner.Banner320X100Activity
import com.avatye.sample.adcash.banner.Banner320X50Activity
import com.avatye.sample.adcash.banner.BannerDynamicActivity
import com.avatye.sample.adcash.banner.BannerWidget300X250Activity
import com.avatye.sample.adcash.banner.BannerWidget320X100Activity
import com.avatye.sample.adcash.banner.BannerWidget320X50Activity
import com.avatye.sample.adcash.banner.BannerWidgetDynamicActivity
import com.avatye.sample.adcash.databinding.ActivityMainBinding
import com.avatye.sample.adcash.interstitial.InterstitialActivity

class MainActivity : AppBaseActivity() {

    private val vb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)

        // 인셋 패딩 적용
        applySystemInsets(vb.root)

        vb.banner320X50.setOnClickListener {
            Banner320X50Activity.open(activity = this, PlacementParcel(pid = vb.banner320X50Pid.text.toString()))
        }

        vb.banner320X100.setOnClickListener {
            Banner320X100Activity.open(activity = this, PlacementParcel(pid = vb.banner320X100Pid.text.toString()))
        }

        vb.banner300X250.setOnClickListener {
            Banner300X250Activity.open(activity = this, PlacementParcel(pid = vb.banner300X250Pid.text.toString()))
        }

        vb.bannerDynamic.setOnClickListener {
            BannerDynamicActivity.open(activity = this, PlacementParcel(pid = vb.bannerDynamicPid.text.toString()))
        }

        vb.bannerWidget320X50.setOnClickListener {
            BannerWidget320X50Activity.open(activity = this, PlacementParcel(pid = vb.bannerWidget320X50Pid.text.toString()))
        }

        vb.bannerWidget320X100.setOnClickListener {
            BannerWidget320X100Activity.open(activity = this, PlacementParcel(pid = vb.bannerWidget320X100Pid.text.toString()))
        }

        vb.bannerWidget300X250.setOnClickListener {
            BannerWidget300X250Activity.open(activity = this, PlacementParcel(pid = vb.bannerWidget300X250Pid.text.toString()))
        }

        vb.bannerWidgetDynamic.setOnClickListener {
            BannerWidgetDynamicActivity.open(activity = this, PlacementParcel(pid = vb.bannerWidgetDynamicPid.text.toString()))
        }

        vb.interstitial.setOnClickListener {
            InterstitialActivity.open(activity = this, PlacementParcel(pid = vb.interstitialPid.text.toString()))
        }

        vb.interstitialVideo.setOnClickListener {
            InterstitialActivity.open(activity = this, PlacementParcel(pid = vb.interstitialVideoPid.text.toString()))
        }

    }
}