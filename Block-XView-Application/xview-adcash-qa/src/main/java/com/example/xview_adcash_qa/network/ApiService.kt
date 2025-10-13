package com.example.xview_adcash_qa.network

import com.example.xview_adcash_qa.data.Advertisement
import com.example.xview_adcash_qa.data.Application
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("applications")
    suspend fun getApplications(
        @Query("osType") osType: String?,
        @Query("environment") environment: String?
    ): Response<List<Application>>

    /**
     * Call 대신 suspend 키워드를 사용하여 코루틴 방식으로 변경
     */
    @GET("applications/{appId}/advertisements")
    suspend fun getAdvertisementsByAppId(
        @Path("appId") appId: String
    ): Response<List<Advertisement>>
}