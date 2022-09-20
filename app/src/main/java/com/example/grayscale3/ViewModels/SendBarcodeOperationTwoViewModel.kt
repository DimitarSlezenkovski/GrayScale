package com.example.grayscale3.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grayscale.Network.ApiClient
import com.example.grayscale3.Classes.ScreenState
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.Repositories.OperationTwo.SendBarcodeRepository
import com.example.grayscale3.RequestAndResponse.IntakeFullKit.SendBarcodeIntakeRequest
import com.example.grayscale3.RequestAndResponse.IntakeFullKit.SendBarcodeIntakeResponse
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class SendBarcodeOperationTwoViewModel(private val repository: SendBarcodeRepository = SendBarcodeRepository(ApiClient().apiService)) : ViewModel() {

    private val _scanData = MutableLiveData<ScreenState<SendBarcodeIntakeResponse>>()
    var scanData: LiveData<ScreenState<SendBarcodeIntakeResponse>> = _scanData

    fun sendData(scanRequest: SendBarcodeIntakeRequest){
        _scanData.postValue(ScreenState.Loading(null))
        viewModelScope.launch {
          try {
              val response = repository.sendBarcode(Flags.LAB.toString(), scanRequest)
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