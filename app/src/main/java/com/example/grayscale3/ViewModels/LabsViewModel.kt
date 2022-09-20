package com.example.grayscale3.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grayscale.Network.ApiClient
import com.example.grayscale3.Classes.ScreenState
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.Repositories.OperationTwo.LabsRepository
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.net.SocketTimeoutException

class LabsViewModel(private val repository: LabsRepository = LabsRepository(ApiClient().apiService)): ViewModel() {

    private val _labsData = MutableLiveData<ScreenState<List<String>>>()
    val labsData: LiveData<ScreenState<List<String>>>
        get() = _labsData




     fun fetchLabs() {
        _labsData.postValue(ScreenState.Loading(null))
        viewModelScope.launch {
            try {
                val response = repository.fetchLabs(Flags.CITY.toString())
                if(response.isSuccessful){
                    _labsData.postValue(ScreenState.Success(response.body()!!))
                    Log.e("Response not successful", "${response.body()}")
                }else{
                    _labsData.postValue(ScreenState.Error(null, response.code().toString()))
                    Log.e("Response not successful", "${response.body()}")
                    Log.e("Response not successful", "${response.errorBody()}")
                }
            }catch (e: IllegalStateException){
                Flags.stopProgressBarIfNoLabsAreFetched = true
                _labsData.postValue(ScreenState.Error(null, e.message.toString()))
            }catch (e: SocketTimeoutException){
                Flags.stopProgressBarIfSocketTimeoutExceptionAccrued = true
                _labsData.postValue(ScreenState.Error(null, e.message))
            }
        }
    }


}