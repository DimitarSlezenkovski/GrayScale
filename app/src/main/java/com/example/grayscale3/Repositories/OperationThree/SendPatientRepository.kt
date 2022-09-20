package com.example.grayscale3.Repositories.OperationThree

import com.example.grayscale.Network.ApiService
import com.example.grayscale3.RequestAndResponse.Patients.PatientScanUploadRequest

class SendPatientRepository(private val apiService: ApiService) {
    suspend fun uploadResult(patientReq: PatientScanUploadRequest) =  apiService.sendPatients(patientReq)
}