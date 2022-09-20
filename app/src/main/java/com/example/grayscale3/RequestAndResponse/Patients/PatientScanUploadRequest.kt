package com.example.grayscale3.RequestAndResponse.Patients

import com.google.gson.annotations.SerializedName

data class PatientScanUploadRequest (
    @SerializedName("barcode")
    val barcode: String? = null,

    @SerializedName("name")
    val name: String? = null
    )