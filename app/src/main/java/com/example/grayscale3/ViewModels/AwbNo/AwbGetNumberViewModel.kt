package com.example.grayscale3.ViewModels.AwbNo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grayscale.Network.ApiClient
import com.example.grayscale3.Repositories.OperationFour.AwbNoRepository
import com.example.grayscale3.Classes.ScreenState
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class AwbGetNumberViewModel(private val repository: AwbNoRepository = AwbNoRepository(ApiClient().apiService)): ViewModel() {

    private val _numbersData = MutableLiveData<ScreenState<List<String>?>>()
    val numbersData: LiveData<ScreenState<List<String>?>>
        get() = _numbersData


    fun awbGetNumbers(){
        _numbersData.postValue(ScreenState.Loading(null))
        viewModelScope.launch {
            try{
                val response = repository.awbGetNumbers()
                if(response.isSuccessful){
                    _numbersData.postValue(ScreenState.Success(response.body()))
                    Log.e("response", "${response.body().toString()}")
                    Log.e("response", "${response.code()}")

                }else{
                    _numbersData.postValue(ScreenState.Error(null, response.code().toString(), message2 = response.errorBody().toString()))
                    Log.e("Failed response", "${response.errorBody()}")
                    Log.e("Failed response 2", "${response.message()}")

                }
            }catch (e: IllegalStateException){
                _numbersData.postValue(ScreenState.Error(null, e.message))
                Log.e("Exeption", "${e.message}")
            }

        }


    }
}
