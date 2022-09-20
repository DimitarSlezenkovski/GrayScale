package com.example.grayscale3.Repositories.OperationFive

import com.example.grayscale.Network.ApiService

class GeneratorBarcodeRepository(private val apiService: ApiService) {
    suspend fun fetchPatientInfo(barcode : String) = apiService.fetchPatientInfo(barcode)
}