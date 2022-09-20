package com.example.grayscale3.Repositories.OperationOne

import com.example.grayscale.Network.ApiService
import com.example.grayscale3.RequestAndResponse.Shipment.ShipmentPanoramaRequest

class ShipmentRepository(private val apiService: ApiService) {
    suspend fun sendShipment(shipmentRequest: ShipmentPanoramaRequest) = apiService.sendShipment(shipmentRequest)
}