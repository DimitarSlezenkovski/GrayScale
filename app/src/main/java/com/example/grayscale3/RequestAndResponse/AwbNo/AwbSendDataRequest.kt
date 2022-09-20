package com.example.grayscale3.RequestAndResponse.AwbNo

import com.google.gson.annotations.SerializedName

data class AwbSendDataRequest (
    @SerializedName("barcode")
    val barcode: String? = null,

    @SerializedName("awb")
    val awb: String? = null

    )