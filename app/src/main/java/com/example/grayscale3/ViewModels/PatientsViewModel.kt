package com.example.grayscale3.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grayscale.Network.ApiClient
import com.example.grayscale3.Classes.ScreenState
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.Repositories.OperationThree.PatientsRepository
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.net.SocketTimeoutException

class PatientsViewModel(private val repository: PatientsRepository =  PatientsRepository(ApiClient().apiService)): ViewModel() {
    private val _patients = MutableLiveData<ScreenState<List<String>>>()
    val patients: LiveData<ScreenState<List<String>>>
    get() = _patients

    init {
        fetchPatients()
    }

  private fun fetchPatients() {
        _patients.postValue(ScreenState.Loading(null))
        viewModelScope.launch {
            try {
                val response = repository.fetchPatients(Flags.BARCODE.toString())
                if(response.isSuccessful){
                    _patients.postValue(ScreenState.Success(data = response.body()!!))
                    Log.e("token", "${Flags.TOKEN}")
                }else{
                    _patients.postValue(ScreenState.Error(null , response.errorBody().toString()))
                    Log.e("response body", "${response.body()}")
                }
            }catch (e: IllegalStateException){
                Flags.stopProgressBarIfNoPatientsLoaded = true
                _patients.postValue(ScreenState.Error(data = null, message = e.message.toString()))
              }catch (e: SocketTimeoutException){
                  Flags.stopProgressBarIfSocketTimeoutExceptionAccruedPatients = true
                  _patients.postValue(ScreenState.Error(null, e.message))
              }
            }

        }

    }
