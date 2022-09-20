package com.example.grayscale3.Repositories.OperationFour

import com.example.grayscale.Network.ApiService
import com.example.grayscale3.RequestAndResponse.AwbNo.AwbSendDataRequest

class AwbNoDataSendRepository(private val apiService: ApiService) {
    suspend fun sendAwbData(awbRequestData: AwbSendDataRequest ) = apiService.awbPostData(awbRequestData)
}