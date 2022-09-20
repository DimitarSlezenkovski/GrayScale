package com.example.grayscale3.Network

import android.content.Context
import com.example.grayscale3.DataHolders.Flags
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()


        //Adds Token to header
        requestBuilder.addHeader("Authorization", "Bearer ${Flags.TOKEN}")
        return chain.proceed(requestBuilder.build())
    }

}