package com.example.xview_adcash_qa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.avatye.adcash.ADCashSDK
import com.example.xview_adcash_qa.data.ConfigRepository
import com.example.xview_adcash_qa.network.RetrofitClient.apiService
import com.example.xview_adcash_qa.ui.ConfigAdapter
import com.example.xview_adcash_qa.ui.ConfigViewModel
import com.example.xview_adcash_qa.ui.ConfigViewModelFactory
import com.example.xview_adcash_qa.databinding.ActivityMainBinding
import com.example.xview_adcash_qa.ui.SelectableApplication

class MainActivity : AppBaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var configAdapter: ConfigAdapter
    private val viewModel: ConfigViewModel by viewModels {
        ConfigViewModelFactory(ConfigRepository(apiService, this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인셋 패딩 적용
        applySystemInsets(binding.root)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    // RecyclerView 설정
    private fun setupRecyclerView() {
        configAdapter = ConfigAdapter { selectedConfig ->
            viewModel.onConfigSelected(selectedConfig)
        }
        binding.recyclerView.apply {
            adapter = configAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupClickListeners() {
        binding.buttonRefrash.setOnClickListener {
            viewModel.fetchDataFromServer()
        }

        binding.buttonAction.setOnClickListener {
            viewModel.onActionButtonClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            showLoading(state.isLoading)
            val selectableList = state.configs.map { config ->
                SelectableApplication(
                    application = config,
                    isSelected = config.id == state.selectedConfig?.id
                )
            }

            configAdapter.submitList(selectableList)

            state.userMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.sdkInitializationEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { config ->
                initSDK(config)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonRefrash.isEnabled = !isLoading
    }

    private fun initSDK(config: ConfigViewModel.SdkConfig) {
        val builder = ADCashSDK.Builder(
            context = this,
            appId = config.appId,
            appSecret = config.appSecret,
        )

        builder.setStoreUrl(url = "https://www.avatye.com")
        builder.setUserPhoneNumber("1111-1111-1111")
        builder.setUserName("KIM")
        builder.setAppName("ADCASH")
        builder.build()

        val intent = Intent(this, AdcashTestActivity::class.java)
        intent.putExtra(AdcashTestActivity.EXTRA_APP_ID, config.appId)
        startActivity(intent)
    }
}