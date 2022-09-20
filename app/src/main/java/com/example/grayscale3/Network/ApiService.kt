package com.example.grayscale.Network

import com.example.grayscale.DataHolders.Constants
import com.example.grayscale.RequestAndResponse.Login.UserRequest
import com.example.grayscale.RequestAndResponse.Login.UserResponse
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.RequestAndResponse.AwbNo.AwbSendDataRequest
import com.example.grayscale3.RequestAndResponse.AwbNo.AwbSendDataResponse
import com.example.grayscale3.RequestAndResponse.GenerateBarcode.PatientBarcodeInfoResponse
import com.example.grayscale3.RequestAndResponse.IntakeFullKit.CitiesRequest
import com.example.grayscale3.RequestAndResponse.IntakeFullKit.SendBarcodeIntakeRequest
import com.example.grayscale3.RequestAndResponse.IntakeFullKit.SendBarcodeIntakeResponse
import com.example.grayscale3.RequestAndResponse.Patients.GetPatientsText
import com.example.grayscale3.RequestAndResponse.Patients.PatientScanUploadRequest
import com.example.grayscale3.RequestAndResponse.Patients.PatientScanUploadResponse
import com.example.grayscale3.RequestAndResponse.Shipment.ShipmentPanoramaRequest
import com.example.grayscale3.RequestAndResponse.Shipment.ShipmentPanoramaResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    //Login request
    @POST(Constants.TOKEN)
    suspend fun login(@Body userRequest: UserRequest): Response<UserResponse>

    //Operation Three Request - Assign Patient
    @GET("patients?")
    suspend fun getPatientsByBarcode(@Query("barcode") barcode: String): Response<List<String>>
    @POST(Constants.SEND_PATIENTS)
    suspend fun sendPatients(@Body PatientScanUploadRequest: PatientScanUploadRequest): Response<PatientScanUploadResponse>

    //Operation Two Request - Intake FullKit
    @GET(Constants.CITIES)
    suspend fun getCities(): Response<List<CitiesRequest>>
    @GET("cities/{city}/labs")
    suspend fun getLabs(@Path("city") city: String): Response<List<String>>
    @POST("panorama/{location}")
    suspend fun sendBarcode(@Path("location") location: String, @Body scanRequest: SendBarcodeIntakeRequest): Response<SendBarcodeIntakeResponse>

    //Operation One Request - Shipment
    @POST(Constants.PANORAMA)
    suspend fun sendShipment(@Body shipmentRequest: ShipmentPanoramaRequest): Response<ShipmentPanoramaResponse>

    //Awb NO.
    @GET(Constants.AWB_GET_NUMBERS)
    suspend fun awbGetNumbers(): Response<List<String>>
    @POST(Constants.AWB_POST)
    suspend fun awbPostData(@Body awbSendDataRequest: AwbSendDataRequest): Response<AwbSendDataResponse>

    //Generate Barcode
    @GET("panorama/{barcode}")
    suspend fun fetchPatientInfo(@Path("barcode") barcode: String): Response<PatientBarcodeInfoResponse>

}