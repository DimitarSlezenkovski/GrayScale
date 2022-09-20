package com.example.grayscale3.RequestAndResponse.IntakeFullKit

import com.google.gson.annotations.SerializedName

data class SendBarcodeIntakeRequest(
    @SerializedName("barcode")
    val barcode: String? = null
)