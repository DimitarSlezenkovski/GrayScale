package com.example.grayscale.Repositories

import com.example.grayscale.Network.ApiService
import com.example.grayscale.RequestAndResponse.Login.UserRequest

class LoginRepository(private val apiService: ApiService) {
    suspend fun loginUser(user: UserRequest) = apiService.login(user)
}