package com.example.grayscale3.RequestAndResponse.IntakeFullKit

import android.util.Log
import com.google.gson.annotations.SerializedName

data class CitiesRequest(
    @SerializedName("name") var city: String?,
    @SerializedName("country") var country: String?
)