package com.example.grayscale3.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grayscale.Network.ApiClient
import com.example.grayscale3.Classes.ScreenState
import com.example.grayscale3.Repositories.OperationThree.SendPatientRepository
import com.example.grayscale3.RequestAndResponse.Patients.PatientScanUploadRequest
import com.example.grayscale3.RequestAndResponse.Patients.PatientScanUploadResponse
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class SendResultOperationThreeViewModel(private val repository: SendPatientRepository = SendPatientRepository(ApiClient().apiService)): ViewModel() {

    private val _scanData = MutableLiveData<ScreenState<PatientScanUploadResponse>>()
    var scanData: LiveData<ScreenState<PatientScanUploadResponse>> = _scanData

    fun uploadScanData(patientReq: PatientScanUploadRequest){
        _scanData.postValue(ScreenState.Loading(null))
        viewModelScope.launch {
           try {
               val response =  repository.uploadResult(patientReq)
               if(response.isSuccessful){
                   _scanData.postValue(ScreenState.Success(response.body()!!))
               }else{
                   _scanData.postValue(ScreenState.Error(response.body(), response.errorBody().toString()))
                   Log.e("Response not successful", "${response.body()}")
               }
           }catch (e: SocketTimeoutException){
               _scanData.postValue(ScreenState.Error(null, e.message))
           }
        }
    }

}