package com.example.xview_adcash_qa.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// 앱 전체에서 API 서비스를 제공하는 싱글톤 객체
object RetrofitClient {

    // 💡 중요: 서버의 IP 주소와 포트로 변경해야 합니다.
    private const val BASE_URL = "http://192.168.0.37:3000/"

    // API 서비스 인터페이스의 구현체를 생성 (Lazy 초기화로 처음 사용할 때 한 번만 생성)
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Retrofit 인스턴스 생성
    private val retrofit: Retrofit by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃: 30초
            .readTimeout(30, TimeUnit.SECONDS)    // 읽기 타임아웃: 30초
            .writeTimeout(30, TimeUnit.SECONDS)   // 쓰기 타임아웃: 30초
            .build()

        // Retrofit 빌더
        Retrofit.Builder()
            .baseUrl(BASE_URL) // 1. 기본 서버 URL 설정
            .client(okHttpClient) // 2. (선택) 상세 설정을 위한 OkHttpClient 연결
            .addConverterFactory(GsonConverterFactory.create()) // 3. JSON <-> Kotlin 데이터 클래스 변환기 설정
            .build()
    }
}