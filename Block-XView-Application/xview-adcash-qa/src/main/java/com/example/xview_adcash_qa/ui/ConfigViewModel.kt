package com.example.xview_adcash_qa.ui

import androidx.lifecycle.*
import com.example.xview_adcash_qa.data.AccountType
import com.example.xview_adcash_qa.data.Application
import com.example.xview_adcash_qa.data.ConfigRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

data class UiState(
    val configs: List<Application> = emptyList(),
    val isLoading: Boolean = false,
    val selectedConfig: Application? = null,
    val userMessage: String? = null
)

class ConfigViewModel(private val configRepository: ConfigRepository) : ViewModel() {
    data class SdkConfig(val appId: String, val appSecret: String)

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> get() = _uiState

    private val _sdkInitializationEvent = MutableLiveData<Event<SdkConfig>>()
    val sdkInitializationEvent: LiveData<Event<SdkConfig>> get() = _sdkInitializationEvent

    init {
        _uiState.value = UiState(isLoading = true)
        loadInitialData()
    }

    private fun loadInitialData() {
        val localConfigs = configRepository.getLocalConfigs()
        _uiState.value = _uiState.value?.copy(configs = localConfigs)

        fetchDataFromServer()
    }

    fun fetchDataFromServer() {
        _uiState.value = _uiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val freshConfigs = configRepository.refreshConfigs()
                _uiState.value = _uiState.value?.copy(
                    configs = freshConfigs,
                    userMessage = "데이터를 새로고침 했습니다."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value?.copy(
                    userMessage = "갱신 실패: 네트워크 연결을 확인해주세요."
                )
            } finally {
                _uiState.value = _uiState.value?.copy(isLoading = false)
            }
        }
    }

    fun onConfigSelected(config: Application) {
        val currentSelection = _uiState.value?.selectedConfig

        if (currentSelection == config) {
            _uiState.value = _uiState.value?.copy(selectedConfig = null)
        } else {
            _uiState.value = _uiState.value?.copy(selectedConfig = config)
        }
    }

    // 메인 액션 버튼 클릭 시 호출
    fun onActionButtonClicked() {
        val currentConfig = _uiState.value?.selectedConfig
        if (currentConfig == null) {
            _uiState.value = _uiState.value?.copy(userMessage = "값을 선택해주세요")
            return
        }

        _sdkInitializationEvent.value = Event(
            SdkConfig(appId = currentConfig.appId, appSecret = currentConfig.appSecret)
        )
    }

    open class Event<out T>(private val content: T) {
        private var hasBeenHandled = false
        fun getContentIfNotHandled(): T? {
            return if (hasBeenHandled) {
                null
            } else {
                hasBeenHandled = true
                content
            }
        }
    }
}

// ViewModel에 Repository를 주입하기 위한 Factory 클래스
class ConfigViewModelFactory(private val repository: ConfigRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfigViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConfigViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}