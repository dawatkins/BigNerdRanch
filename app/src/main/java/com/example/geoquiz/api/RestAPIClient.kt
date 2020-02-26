package com.example.geoquiz.api

import androidx.annotation.VisibleForTesting
import com.example.geoquiz.api.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RestAPIClient(var apiURL: String) {
    private val TAG = RestAPIClient::class.java.simpleName

    private var apiService: ApiService
    private var retrofit: Retrofit
    private val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    init {


        retrofit = Retrofit.Builder()
            .baseUrl(apiURL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    fun getApiService(): ApiService {
        return apiService
    }

    @VisibleForTesting
    fun getOkHttpClient(): OkHttpClient {
        return client
    }
}