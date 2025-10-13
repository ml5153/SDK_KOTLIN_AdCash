package com.example.xview_adcash_qa.ui

import androidx.lifecycle.*
import com.example.xview_adcash_qa.data.AdType
import com.example.xview_adcash_qa.data.Advertisement
import com.example.xview_adcash_qa.data.ConfigRepository
import com.example.xview_adcash_qa.ui.ConfigViewModel.Event
import com.example.xview_adcash_qa.ui.ConfigViewModel.SdkConfig
import kotlinx.coroutines.launch

data class DetailUiState(
    val advertisements: List<Advertisement> = emptyList(),
    val selectedAdvertisements: Advertisement? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DetailViewModel(
    private val configRepository: ConfigRepository,
    private val appId: String
) : ViewModel() {
    data class PIDConfig(val pid: String, val type: AdType)

    private val _uiState = MutableLiveData<DetailUiState>()
    val uiState: LiveData<DetailUiState> get() = _uiState

    private val _sdkCallEvent = MutableLiveData<Event<PIDConfig>>()
    val sdkCallEvent: LiveData<Event<PIDConfig>> get() = _sdkCallEvent

    init {
        fetchAdvertisements()
    }

    private fun fetchAdvertisements() {
        _uiState.value = DetailUiState(isLoading = true)

        viewModelScope.launch {
            val result = configRepository.getAdvertisements(appId)
            result.onSuccess { ads ->
                _uiState.value = DetailUiState(advertisements = ads)
            }.onFailure { error ->
                _uiState.value = DetailUiState(errorMessage = error.message)
            }
        }
    }

    fun onConfigSelected(advertisement: Advertisement) {
        val currentSelection = _uiState.value?.selectedAdvertisements

        if (currentSelection == advertisement) {
            _uiState.value = _uiState.value?.copy(selectedAdvertisements = null)
        } else {
            _uiState.value = _uiState.value?.copy(selectedAdvertisements = advertisement)
        }
    }

    fun onActionButtonClicked() {
        val currentConfig = _uiState.value?.selectedAdvertisements
        if (currentConfig == null) {
            //_uiState.value = _uiState.value?.copy(userMessage = "값을 선택해주세요")
            return
        }

        _sdkCallEvent.value = Event(PIDConfig(pid = currentConfig.pid, type = currentConfig.type))
    }
}

class DetailViewModelFactory(
    private val repository: ConfigRepository,
    private val appId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(repository, appId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}