package com.example.xview_adcash_qa

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.avatye.adcash.AdError
import com.avatye.adcash.BannerAdSize
import com.avatye.adcash.InterstitialAdType
import com.avatye.adcash.loader.BannerAdLoader
import com.avatye.adcash.loader.InterstitialAdLoader
import com.example.xview_adcash_qa.data.AdType
import com.example.xview_adcash_qa.data.ConfigRepository
import com.example.xview_adcash_qa.databinding.ActivityAdcashTestBinding
import com.example.xview_adcash_qa.network.RetrofitClient
import com.example.xview_adcash_qa.ui.AdAdapter
import com.example.xview_adcash_qa.ui.ConfigAdapter
import com.example.xview_adcash_qa.ui.DetailViewModel
import com.example.xview_adcash_qa.ui.DetailViewModelFactory
import com.example.xview_adcash_qa.ui.SelectableAdvertisement
import com.example.xview_adcash_qa.ui.SelectableApplication

class AdcashTestActivity : AppBaseActivity() {

    companion object {
        const val EXTRA_APP_ID = "extra_app_id"
    }

    private lateinit var binding: ActivityAdcashTestBinding
    private lateinit var adAdapter: AdAdapter
    private val receivedAppId: String by lazy {
        intent.getStringExtra(EXTRA_APP_ID) ?: ""
    }

    private val viewModel: DetailViewModel by viewModels {
        val repository = ConfigRepository(RetrofitClient.apiService, applicationContext)
        DetailViewModelFactory(repository, receivedAppId)
    }

    private var interstitialAdLoader: InterstitialAdLoader? = null
    private var bannerAdLoader: BannerAdLoader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdcashTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인셋 패딩 적용
        applySystemInsets(binding.root)

        binding.textViewAppId.text = "App ID: $receivedAppId"
        setupRecyclerView()
        observeViewModel()

        binding.buttonAction.setOnClickListener {
            viewModel.onActionButtonClicked()
        }
    }

    private fun loadBaner(pid: String, bannerAdSize: BannerAdSize) {
        bannerAdLoader = BannerAdLoader(
            context = this,
            placementId = pid,
            bannerAdSize = bannerAdSize,
            listener = object : BannerAdLoader.BannerListener {
                override fun onLoaded(adView: View, size: BannerAdSize) {
                    binding.adview.removeAllViews()
                    binding.adview.addView(adView)
                }

                override fun onFailed(error: AdError) {
                    Toast.makeText(this@AdcashTestActivity, error.errorMessage, Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onClicked() {
                }
            }
        )
        bannerAdLoader?.requestAd()
    }


    private fun loadInter(pid: String) {
        interstitialAdLoader = InterstitialAdLoader(
            ownerActivity = this,
            placementId = pid,
            listener = object : InterstitialAdLoader.InterstitialListener {
                override fun onLoaded(
                    executor: InterstitialAdLoader.InterstitialExecutor,
                    adType: InterstitialAdType
                ) {

                    executor.show()
                }

                override fun onOpened() {
                }

                override fun onClosed(completed: Boolean) {
                }

                override fun onFailed(error: AdError) {
                    Toast.makeText(this@AdcashTestActivity, error.errorMessage, Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onClicked() {
                }
            }
        )
        interstitialAdLoader?.requestAd()
    }

    private fun setupRecyclerView() {
        adAdapter = AdAdapter { clickedAdvertisement ->
            viewModel.onConfigSelected(clickedAdvertisement)
        }
        binding.recyclerViewAds.apply {
            adapter = adAdapter
            layoutManager = LinearLayoutManager(this@AdcashTestActivity)
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            val selectableList = state.advertisements.map { advertisement ->
                SelectableAdvertisement(
                    advertisement = advertisement,
                    isSelected = advertisement.id == state.selectedAdvertisements?.id
                )
            }

            adAdapter.submitList(selectableList)
            state.errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.sdkCallEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { config ->
                val adSize: BannerAdSize? = when (config.type) {
                    AdType.BANNER_50 -> BannerAdSize.W320XH50
                    AdType.BANNER_100 -> BannerAdSize.W320XH100
                    AdType.BANNER_250 -> BannerAdSize.W300XH250
                    AdType.DYNAMIC -> BannerAdSize.DYNAMIC
                    else -> null
                }
                if (adSize != null) {
                    loadBaner(config.pid, adSize)
                } else {
                    loadInter(config.pid)
                }
            }
        }
    }
}