package com.example.grayscale3.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grayscale.Network.ApiClient
import com.example.grayscale3.Classes.ScreenState
import com.example.grayscale3.Repositories.OperationOne.ShipmentRepository
import com.example.grayscale3.RequestAndResponse.Shipment.ShipmentPanoramaRequest
import com.example.grayscale3.RequestAndResponse.Shipment.ShipmentPanoramaResponse
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class SendShipmentOperationOneViewModel(private val repository: ShipmentRepository = ShipmentRepository(ApiClient().apiService)) : ViewModel() {

    private val _shipmentData = MutableLiveData<ScreenState<ShipmentPanoramaResponse>>()
    val shipmentData: LiveData<ScreenState<ShipmentPanoramaResponse>> = _shipmentData

    fun uploadScannedData(shipmentRequest: ShipmentPanoramaRequest){
        _shipmentData.postValue(ScreenState.Loading(null))
        viewModelScope.launch {
           try {
               val response = repository.sendShipment(shipmentRequest)
               if(response.isSuccessful){
                   _shipmentData.postValue(ScreenState.Success(response.body()!!))
               }else{
                   _shipmentData.postValue(ScreenState.Error(null, response.toString()))
                   Log.e("Response not successful from shipment upload 1", "${response.body()}")
                   Log.e("Response not successful from shipment upload 2", "${response.errorBody().toString()}")
                   Log.e("Response not successful from shipment upload 3", "${response.message().toString()}")
                   Log.e("Response not successful from shipment upload 4", "${response.code().toString()}")
                   Log.e("Response not successful from shipment upload 5", "${response.toString()}")
               }
           }catch (e: SocketTimeoutException){
               _shipmentData.postValue(ScreenState.Error(null, e.message))
           }
        }
    }










}