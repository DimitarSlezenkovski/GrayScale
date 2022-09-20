package com.example.grayscale.ViewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grayscale.Network.ApiClient
import com.example.grayscale.Repositories.LoginRepository
import com.example.grayscale.RequestAndResponse.Login.UserRequest
import com.example.grayscale.RequestAndResponse.Login.UserResponse
import com.example.grayscale3.Network.SessionManager
import com.example.grayscale3.Classes.ScreenState
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class LoginViewModel(private val repository: LoginRepository =  LoginRepository(ApiClient().apiService)): ViewModel() {
    private val _loginData = MutableLiveData<ScreenState<UserResponse?>>()
    val loginData: LiveData<ScreenState<UserResponse?>>
          get() = _loginData


    fun loginUser(user: UserRequest, context: Context){
       val sessionManager = SessionManager(context)
        _loginData.postValue(ScreenState.Loading(null))
        viewModelScope.launch {
            try{
                val response =  repository.loginUser(user)
                if(response.isSuccessful){
                    _loginData.postValue(ScreenState.Success(response.body()))
                    Log.e("response code", "${response.code()}")
                    Log.e("response message", "${response.message()}")
                    sessionManager.saveToken(response.body()?.accessToken.toString())
                }
                else{
                    _loginData.postValue(ScreenState.Error(response.body(), response.errorBody().toString()))
                    Log.e("response code", "${response.code()}")
                }
            }catch (e: SocketTimeoutException){
                _loginData.postValue(ScreenState.Error(null, e.message))
            }

        }
    }

}