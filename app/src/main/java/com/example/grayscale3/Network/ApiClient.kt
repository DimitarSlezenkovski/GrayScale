package com.example.grayscale.Network

import android.os.Looper
import com.example.grayscale.DataHolders.Constants
import com.example.grayscale3.Network.AuthInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiClient() {

    private fun okHttp(): OkHttpClient{
        val client = OkHttpClient.Builder()
        client.connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor())
        return client.build()
    }



    private val buildRequest: Retrofit by lazy {
      Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttp()).build()

    }

    val apiService: ApiService = buildRequest.create(ApiService::class.java)




}