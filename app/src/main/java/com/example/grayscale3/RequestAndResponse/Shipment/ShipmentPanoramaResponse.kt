package com.example.grayscale3.RequestAndResponse.Shipment

import com.google.gson.annotations.SerializedName

data class ShipmentPanoramaResponse(
    @SerializedName("message")
    val message:String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("success")
    val success: String? = null

)