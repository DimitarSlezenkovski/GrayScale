package com.example.grayscale3.Repositories.OperationTwo

import com.example.grayscale.Network.ApiService

class CityRepository(private val apiService: ApiService) {
    suspend fun fetchCities() = apiService.getCities()
}