package com.example.grayscale3.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grayscale.Network.ApiClient
import com.example.grayscale3.Classes.ScreenState
import com.example.grayscale3.Repositories.OperationTwo.CityRepository
import com.example.grayscale3.RequestAndResponse.IntakeFullKit.CitiesRequest
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class CitiesViewModel(private val repository: CityRepository = CityRepository(ApiClient().apiService)) : ViewModel(){

    private val _citiesData = MutableLiveData<ScreenState<List<CitiesRequest>>>()
    val citiesData: LiveData<ScreenState<List<CitiesRequest>>> = _citiesData




    fun fetchCities(){
        _citiesData.postValue(ScreenState.Loading(null))
        viewModelScope.launch {
            try{
                val response = repository.fetchCities()
                if(response.isSuccessful){
                    _citiesData.postValue(ScreenState.Success(response.body()!!))
                }else{
                    _citiesData.postValue(ScreenState.Error(null, response.code().toString()))
                    Log.e("error body", "${response.errorBody()}")
                    Log.e("error body", "${response.body()}")
                }
            }catch (e: SocketTimeoutException){
                _citiesData.postValue(ScreenState.Error(null, e.message))
            }

        }

    }



}