package com.example.grayscale3.Repositories.OperationThree

import com.example.grayscale.Network.ApiService

class PatientsRepository(private val apiService: ApiService) {
    suspend fun fetchPatients(barcode: String) = apiService.getPatientsByBarcode(barcode)
}