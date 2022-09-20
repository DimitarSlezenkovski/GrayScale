package com.example.grayscale3.RequestAndResponse.Shipment

import com.google.gson.annotations.SerializedName

data class ShipmentPanoramaRequest(
    @SerializedName("barcode")
    val barcode: String? = null,

    @SerializedName("expiration")
    val expiration: String? = null
)