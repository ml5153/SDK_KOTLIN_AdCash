package com.example.xview_adcash_qa.data

import android.content.Context
import android.util.Log
import com.example.xview_adcash_qa.network.ApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class ConfigRepository(
    private val apiService: ApiService,
    private val context: Context
) {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("config_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CONFIGS = "key_configs"
        private const val KEY_ADS_PREFIX = "key_ads_"
    }

    fun getLocalConfigs(): List<Application> {
        return try {
            val json = prefs.getString(KEY_CONFIGS, null)
            json?.let {
                val type = object : TypeToken<List<Application>>() {}.type
                gson.fromJson(it, type)
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("ConfigRepository", "Error reading configs from prefs", e)
            emptyList()
        }
    }

    private fun saveConfigsToLocal(configs: List<Application>) {
        try {
            val json = gson.toJson(configs)
            prefs.edit().putString(KEY_CONFIGS, json).apply()
        } catch (e: Exception) {
            Log.e("ConfigRepository", "Error saving configs to prefs", e)
        }
    }

    suspend fun refreshConfigs(): List<Application> {
        return try {
            val response = apiService.getApplications(osType = null, environment = null)
            if (response.isSuccessful && response.body() != null) {
                val newConfigs = response.body()!!
                saveConfigsToLocal(newConfigs)
                newConfigs
            } else {
                throw IOException("서버 응답 에러: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("ConfigRepository", "Failed to refresh configs, loading from local.", e)
            getLocalConfigs()
        }
    }

    /**
     * 광고 리스트를 가져옵니다.
     * 네트워크 연결을 먼저 시도하고, 실패하면 로컬에 저장된 캐시 데이터를 반환합니다.
     * @param appId 광고 리스트를 조회할 Application의 ID
     * @return 광고 리스트 Result 객체
     */
    suspend fun getAdvertisements(appId: String): Result<List<Advertisement>> {
        return try {
            // 1. 네트워크를 통해 최신 데이터를 가져오기 시도
            val response = apiService.getAdvertisementsByAppId(appId)
            if (response.isSuccessful && response.body() != null) {
                val freshAds = response.body()!!
                // 2. 성공 시, 새로운 데이터를 로컬에 저장
                saveAdsToLocal(appId, freshAds)
                Log.d("ConfigRepository", "Fetched ${freshAds.size} ads from network for $appId.")
                Result.success(freshAds)
            } else {
                // API는 성공했으나 응답 바디가 비어있는 등의 예외적인 경우
                throw IOException("서버 응답 에러: ${response.code()} for appId: $appId")
            }
        } catch (e: Exception) {
            // 3. 네트워크 실패 시, 로컬 캐시에서 데이터 조회
            Log.e("ConfigRepository", "Failed to fetch ads from network, loading from cache for $appId.", e)
            Result.success(getLocalAds(appId))
        }
    }

    /**
     * 특정 App ID에 해당하는 광고 리스트를 SharedPreferences에서 가져옵니다.
     * @param appId 조회할 Application의 ID
     * @return 저장된 광고 리스트. 없으면 빈 리스트를 반환합니다.
     */
    private fun getLocalAds(appId: String): List<Advertisement> {
        return try {
            val json = prefs.getString(KEY_ADS_PREFIX + appId, null)
            json?.let {
                val type = object : TypeToken<List<Advertisement>>() {}.type
                gson.fromJson(it, type)
            } ?: emptyList<Advertisement>().also {
                Log.d("ConfigRepository", "No local ads cache found for $appId.")
            }
        } catch (e: Exception) {
            Log.e("ConfigRepository", "Error reading ads from prefs for $appId", e)
            emptyList()
        }
    }

    /**
     * 특정 App ID에 해당하는 광고 리스트를 SharedPreferences에 저장합니다.
     * @param appId 저장할 Application의 ID
     * @param ads 저장할 광고 리스트
     */
    private fun saveAdsToLocal(appId: String, ads: List<Advertisement>) {
        try {
            val json = gson.toJson(ads)
            prefs.edit().putString(KEY_ADS_PREFIX + appId, json).apply()
            Log.d("ConfigRepository", "Saved ${ads.size} ads to local cache for $appId.")
        } catch (e: Exception) {
            Log.e("ConfigRepository", "Error saving ads to prefs for $appId", e)
        }
    }
}