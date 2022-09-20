package com.example.grayscale3.Repositories.OperationTwo

import com.example.grayscale.Network.ApiService

class LabsRepository(private val apiService: ApiService) {
    suspend fun fetchLabs(city: String) = apiService.getLabs(city)
}