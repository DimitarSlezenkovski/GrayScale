package com.example.grayscale3.RequestAndResponse.Patients

import com.google.gson.annotations.SerializedName

data class PatientScanUploadResponse (
    @SerializedName("message")
    val message: String?= null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("success")
    val success: String? = null
)