package com.example.grayscale3.ViewModels.AwbNo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grayscale.Network.ApiClient
import com.example.grayscale3.Repositories.OperationFour.AwbNoDataSendRepository
import com.example.grayscale3.RequestAndResponse.AwbNo.AwbSendDataRequest
import com.example.grayscale3.RequestAndResponse.AwbNo.AwbSendDataResponse
import com.example.grayscale3.Classes.ScreenState
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class AwbNoDataSendViewModel(private val repository: AwbNoDataSendRepository = AwbNoDataSendRepository(ApiClient().apiService)): ViewModel() {

    private val _awbData = MutableLiveData<ScreenState<AwbSendDataResponse>?>()
    var awbdata: LiveData<ScreenState<AwbSendDataResponse>?> = _awbData

    fun awbSendData(awbScanRequest: AwbSendDataRequest){
        _awbData.postValue(ScreenState.Loading(null))
        viewModelScope.launch {
            try {
                val response = repository.sendAwbData(awbScanRequest)
                if(response.isSuccessful){
                    _awbData.postValue(ScreenState.Success(response.body()!!))
                    Log.e("response body", "${response.body()}")
                    Log.e("message", "${response.body()?.message}")
                    Log.e("status", "${response.body()?.status}")
                    Log.e("success", "${response.body()?.success}")
                    Log.e("message", "${response.message()}")
                }else{
                    _awbData.postValue(ScreenState.Error(null, response.errorBody().toString()))
                    Log.e("errors", "${response.errorBody()}")
                    Log.e("message", "${response.message()}")
                }
            }catch (e: SocketTimeoutException){
                _awbData.postValue(ScreenState.Error(null, e.message))
            }
        }

    }

}