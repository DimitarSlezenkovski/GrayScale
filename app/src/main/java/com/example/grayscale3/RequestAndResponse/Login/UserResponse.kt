package com.example.grayscale.RequestAndResponse.Login

import com.google.gson.annotations.SerializedName

class UserResponse {
    @SerializedName("message")
    val message: String? = null
    @SerializedName("status")
    val status: String? = null

    @SerializedName("access_token")
    val accessToken: String? = null

    @SerializedName("token_type")
    val tokenType: String? = null
}