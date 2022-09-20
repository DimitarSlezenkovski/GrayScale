package com.example.grayscale3.Repositories.OperationTwo

import com.example.grayscale.Network.ApiService
import com.example.grayscale3.RequestAndResponse.IntakeFullKit.SendBarcodeIntakeRequest

class SendBarcodeRepository(private val apiService: ApiService) {
    suspend fun sendBarcode(location: String, scanRequest: SendBarcodeIntakeRequest) = apiService.sendBarcode(location, scanRequest)
}