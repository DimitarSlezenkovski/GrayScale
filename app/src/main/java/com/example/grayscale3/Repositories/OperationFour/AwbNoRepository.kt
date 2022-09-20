package com.example.grayscale3.Repositories.OperationFour

import com.example.grayscale.Network.ApiService

class AwbNoRepository(private val apiService: ApiService) {
    suspend fun awbGetNumbers() = apiService.awbGetNumbers()
}