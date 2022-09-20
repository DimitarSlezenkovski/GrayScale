package com.example.grayscale3.ViewModels.GenerateBarcode

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grayscale.Network.ApiClient
import com.example.grayscale3.Classes.ScreenState
import com.example.grayscale3.Repositories.OperationFive.GeneratorBarcodeRepository
import com.example.grayscale3.RequestAndResponse.GenerateBarcode.PatientBarcodeInfoResponse
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException

class GeneratorViewModel(private val repository: GeneratorBarcodeRepository = GeneratorBarcodeRepository(ApiClient().apiService)): ViewModel() {

    private var _patientData = MutableLiveData<ScreenState<PatientBarcodeInfoResponse>?>()
    val patientData : LiveData<ScreenState<PatientBarcodeInfoResponse>?>
    get() = _patientData



    fun fetchPatientInfo(barcode: String){
      viewModelScope.launch {
          try {
              _patientData.postValue(ScreenState.Loading(null))
              val response = repository.fetchPatientInfo(barcode)
              if(response.isSuccessful){
                  _patientData.postValue(ScreenState.Success(response.body()!!))
                  Log.e("In view model class", "Success ${response.body()}")
              }else{
                  Log.e("In view model class", "Error ${response.errorBody()}")
                  _patientData.postValue(ScreenState.Error(null, response.errorBody().toString(), response.code().toString()))
              }
          }catch (s: SocketTimeoutException){
              _patientData.postValue(ScreenState.Error(null, s.message))
          }catch (i: ConnectException){
              _patientData.postValue(ScreenState.Error(null, i.message))
          }

      }

    }


}