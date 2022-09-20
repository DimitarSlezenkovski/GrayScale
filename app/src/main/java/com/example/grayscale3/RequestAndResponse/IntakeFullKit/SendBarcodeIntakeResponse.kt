package com.example.grayscale3.RequestAndResponse.IntakeFullKit

import com.google.gson.annotations.SerializedName

data class SendBarcodeIntakeResponse(

    @SerializedName("message")
    val message: String?= null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("success")
    val success: String? = null
)