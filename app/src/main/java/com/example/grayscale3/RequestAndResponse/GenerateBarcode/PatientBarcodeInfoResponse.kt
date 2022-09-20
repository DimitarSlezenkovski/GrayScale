package com.example.grayscale3.RequestAndResponse.GenerateBarcode

import com.google.gson.annotations.SerializedName

data class PatientBarcodeInfoResponse (

    @SerializedName("message")
    val message: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("birthday")
    val birthday: String? =  null,
    @SerializedName("bloodDrawn")
    val bloodDrawn: String? = null
        )