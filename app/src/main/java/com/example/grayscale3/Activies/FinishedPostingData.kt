package com.example.grayscale3.Activies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.grayscale.Activies.OperationsActivity
import com.example.grayscale.R
import com.example.grayscale3.Activies.BarcodeScanners.OperationTwoBarcodeScanner
import com.example.grayscale3.Activies.BarcodeScanners.Scanner
import com.example.grayscale3.Classes.*
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.DataHolders.Flags.AWD_NO
import com.example.grayscale3.DataHolders.Flags.AssignPatient
import com.example.grayscale3.DataHolders.Flags.IntakeFullKit
import com.example.grayscale3.DataHolders.Flags.Shipment
import com.example.grayscale3.Network.SessionManager
import com.example.grayscale3.RequestAndResponse.AwbNo.AwbSendDataRequest
import com.example.grayscale3.RequestAndResponse.AwbNo.AwbSendDataResponse
import com.example.grayscale3.RequestAndResponse.IntakeFullKit.SendBarcodeIntakeRequest
import com.example.grayscale3.RequestAndResponse.IntakeFullKit.SendBarcodeIntakeResponse
import com.example.grayscale3.RequestAndResponse.Patients.PatientScanUploadRequest
import com.example.grayscale3.RequestAndResponse.Patients.PatientScanUploadResponse
import com.example.grayscale3.RequestAndResponse.Shipment.ShipmentPanoramaRequest
import com.example.grayscale3.RequestAndResponse.Shipment.ShipmentPanoramaResponse
import com.example.grayscale3.ViewModels.AwbNo.AwbNoDataSendViewModel
import com.example.grayscale3.Classes.ScreenState
import com.example.grayscale3.ViewModels.SendBarcodeOperationTwoViewModel
import com.example.grayscale3.ViewModels.SendResultOperationThreeViewModel
import com.example.grayscale3.ViewModels.SendShipmentOperationOneViewModel

class FinishedPostingData : AppCompatActivity(), CustomCancellations {

    private lateinit var viewModelOperationThree: SendResultOperationThreeViewModel
    private lateinit var viewModelOperationTwo: SendBarcodeOperationTwoViewModel
    private lateinit var viewModelOperationOne: SendShipmentOperationOneViewModel
    private lateinit var viewModelOperationFour: AwbNoDataSendViewModel
    private lateinit var name_And_Expiration: TextView
    private lateinit var codes: TextView
    private lateinit var send: Button
    private lateinit var NextScan: TextView
    private lateinit var CancelUpload: TextView
    private lateinit var progressBarScanResult: ProgressBar
    private lateinit var networkChecker: CheckNetwork
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finished_posting_data)
        codes = findViewById(R.id.Codes)
        name_And_Expiration = findViewById(R.id.Name_And_Expiration)
        send = findViewById(R.id.Send)
        NextScan = findViewById(R.id.NextScan)
        progressBarScanResult = findViewById(R.id.progressBarScanResult)
        progressBarScanResult.visibility = View.INVISIBLE
        CancelUpload = findViewById(R.id.CancelUpload)
        networkChecker = CheckNetwork()
        supportActionBar?.setTitle("Send Data")

        //Log Details
        Log.e("barcode", "${Flags.BARCODE}")
        Log.e("LAB", "${Flags.LAB}")
        Log.e("name", "${Flags.NAME_OR_EXPIRATION_OR_AWB}")
        Log.e("shipment", "${Flags.Shipment}")
        Log.e("intake", "${Flags.IntakeFullKit}")
        Log.e("assign", "${Flags.AssignPatient}")
        Log.e("awb no", "${Flags.AWD_NO}")
        Log.e("generate", "${Flags.GENERATE_CODE}")




        //To get the token when the token is lost
        if(Flags.TOKEN.isNullOrEmpty()){
            SessionManager(this).fetchToken()?.let { token ->
                Flags.TOKEN = token
            }
        }



        CancelUpload.setOnClickListener {
            CancelButton().cancelButton()
            customCancellation(cancellationValue3 = false)
            backDialog()
        }

        codes.setText("Barcode: ${Flags.BARCODE.toString()}")


        if(IntakeFullKit){
            //next scan attributes
            NextScan.visibility = View.VISIBLE
            NextScan.text = "Next Scan"
            NextScan.setTextColor(resources.getColor(R.color.white))
            NextScan.setBackgroundResource(R.drawable.button_design)

            name_And_Expiration.text = ""
            Shipment = false
            AssignPatient = false
            AWD_NO = false
        }
        if(AssignPatient){
            NextScan.visibility = View.GONE
            name_And_Expiration.text = "Names: ${Flags.NAME_OR_EXPIRATION_OR_AWB.toString()}"
            Shipment = false
            IntakeFullKit = false
            AWD_NO = false
        }
        if(Shipment){
            //next scan attributes
            NextScan.visibility = View.VISIBLE
            NextScan.text = "Next Scan"
            NextScan.setBackgroundResource(R.drawable.button_design)
            name_And_Expiration.text = "Expirations: ${Flags.NAME_OR_EXPIRATION_OR_AWB.toString()}"
            AssignPatient = false
            IntakeFullKit = false
            AWD_NO = false
        }
        if(AWD_NO){
            NextScan.visibility = View.GONE
            name_And_Expiration.text = "AWB: ${Flags.NAME_OR_EXPIRATION_OR_AWB.toString()}"
            AssignPatient = false
            IntakeFullKit = false
            Shipment = false
        }


        Log.e("Barcode", "${Flags.BARCODE}")
        Log.e("Name", "${Flags.NAME_OR_EXPIRATION_OR_AWB}")


        NextScan.setOnClickListener {
            disableNextScanButton()
            if(!networkChecker.isNetworkAvailable(this)){
                Dialogs().noInternetDialog(this, "No Internet connection, turn on internet connection and try again")
                enableNextScanButton()
                return@setOnClickListener
            }
            //to start scanner again
            _isNextScanPressed = true
            //to check if the button is pressed
            _isNextScanPressedTwice = true
            if(Shipment){
                viewModelOperationOne = ViewModelProvider(this).get(SendShipmentOperationOneViewModel::class.java)
                viewModelOperationOne.shipmentData.observe(this){requestResponse ->
                    checkOperationOneState(requestResponse)
                }
                assignResultOperationOne()
            }
            if(IntakeFullKit){
                viewModelOperationTwo = ViewModelProvider(this).get(SendBarcodeOperationTwoViewModel::class.java)
                viewModelOperationTwo.scanData.observe(this){requestResponse->
                    checkOperationTwoState(requestResponse)
                }
                assignResultOperationTwo()
            }

        }



        send.setOnClickListener {
            disableSendButton()
            if(!networkChecker.isNetworkAvailable(this)){
                Dialogs().noInternetDialog(this, "No Internet connection, turn on internet connection and try again")
                enableSendButton()
                return@setOnClickListener
            }
            _isScanButtonPressedTwice = true
            if(Shipment){
                viewModelOperationOne = ViewModelProvider(this).get(SendShipmentOperationOneViewModel::class.java)
                viewModelOperationOne.shipmentData.observe(this){requestResponse ->
                    checkOperationOneState(requestResponse)
                }
                isScanButtonPressed = true
                assignResultOperationOne()
            }
            if(IntakeFullKit){
                viewModelOperationTwo = ViewModelProvider(this).get(SendBarcodeOperationTwoViewModel::class.java)
                viewModelOperationTwo.scanData.observe(this){requestResponse->
                    checkOperationTwoState(requestResponse)
                }
                isScanButtonPressed = true
                assignResultOperationTwo()
            }
            if(AssignPatient){
                viewModelOperationThree = ViewModelProvider(this).get(SendResultOperationThreeViewModel::class.java)
                viewModelOperationThree.scanData.observe(this){requestResponse ->
                    checkOperationThreeState(requestResponse)
                }
                isScanButtonPressed = true
                assignResult()
            }
            if(AWD_NO){
                viewModelOperationFour = ViewModelProvider(this).get(AwbNoDataSendViewModel::class.java)
                viewModelOperationFour.awbdata.observe(this){ requsetReposnse ->
                    menageOperationState(requsetReposnse)
                }
                isScanButtonPressed = true
                assignResultOperationFour()
            }
        }
    }

    private fun manageOperationState(operation: OperationStateWithoutData){
        when(operation){
           is OperationStateWithoutData.Export -> {

           }
            OperationStateWithoutData.Import -> {

            }
            OperationStateWithoutData.Assign -> {

            }
            OperationStateWithoutData.AwbNo -> {

            }
            OperationStateWithoutData.GenerateCode -> {

            }
        }

    }

    private fun disableSendButton(){
        send.isEnabled = false
        send.isClickable = false
    }
    private fun disableNextScanButton(){
        NextScan.isEnabled = false
        NextScan.isClickable = false
    }
    private fun enableSendButton(){
        send.isEnabled = true
        send.isClickable = true
    }
    private fun enableNextScanButton(){
        NextScan.isEnabled = true
        NextScan.isClickable = true
    }





    private var isScanButtonPressed: Boolean = false
    private var _isNextScanPressed : Boolean = false
    private var _isNextScanPressedTwice: Boolean = false
    private var _isScanButtonPressedTwice: Boolean = false

    //Operation One
    private fun assignResultOperationOne() {
        val barcode = Flags.BARCODE
        val expiration = Flags.NAME_OR_EXPIRATION_OR_AWB
        val shipmentRequest = ShipmentPanoramaRequest(barcode, expiration)
        viewModelOperationOne.uploadScannedData(shipmentRequest)
        Log.e("Send the data from operation Export", "$barcode, $expiration")
    }
    //Operation one
    private fun checkOperationOneState(state: ScreenState<ShipmentPanoramaResponse>){
        when(state){
            is ScreenState.Loading ->{
                progressBarScanResult.visibility = View.VISIBLE
            }
            is ScreenState.Success -> {
                if(state.data != null){
                    progressBarScanResult.visibility = View.INVISIBLE
                    CancelButton().successChecking(state.data.success, this, state.data.message)
                    if(_isNextScanPressed){
                        startActivity(Intent(this, Scanner::class.java))
                        finish()
                        Flags.BARCODE = null
                        Flags.NAME_OR_EXPIRATION_OR_AWB = null
                        _isNextScanPressed = false
                        _isNextScanPressedTwice = false

                    }else{
                        startActivity(Intent(this, OperationsActivity::class.java))
                        finish()
                    }
                }

            }
            is ScreenState.Error ->{
                if(state.message != null){
                    progressBarScanResult.visibility = View.INVISIBLE
                    Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                    Log.e("Message from Operation One or Shipment", "${state.message}")
                }
            }
        }
    }
    //Operation One code ends

    //Operation Two
    private fun assignResultOperationTwo() {
        val barcode = Flags.BARCODE
        val intakeRequest = SendBarcodeIntakeRequest(barcode)
        viewModelOperationTwo.sendData(intakeRequest)
        Log.e("Send the data from operation Import", "$barcode")
    }
    //Operation Two
    private fun checkOperationTwoState(state: ScreenState<SendBarcodeIntakeResponse>){
        when(state){
            is ScreenState.Loading ->{
                progressBarScanResult.visibility =  View.VISIBLE
            }
            is ScreenState.Success ->{
                if(state.data != null){
                    progressBarScanResult.visibility =  View.INVISIBLE
                    CancelButton().successChecking(state.data.success, this, state.data.message)
                    if(_isNextScanPressed){
                        startActivity(Intent(this, OperationTwoBarcodeScanner::class.java))
                        finish()
                        Flags.BARCODE = null
                        Flags.NAME_OR_EXPIRATION_OR_AWB = null
                        _isNextScanPressed = false
                        _isNextScanPressedTwice = false
                    }else{
                        startActivity(Intent(this, OperationsActivity::class.java))
                        finish()
                    }
                }
            }
            is ScreenState.Error ->{
              if(state.message != null){
                  progressBarScanResult.visibility =  View.INVISIBLE
                  Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                  Log.e("Message from Operation Two or Intake Fullkit", "${state.message}")
              }
            }
        }
    }
    //Operation Two code ends





    //Operation Three
    private fun assignResult() {
        val barcode = Flags.BARCODE
        val name = Flags.NAME_OR_EXPIRATION_OR_AWB
        val patientsRequest = PatientScanUploadRequest(barcode, name)
        viewModelOperationThree.uploadScanData(patientsRequest)
        Log.e("Send the data from operation Assign", "$barcode, $name")
    }
    //Operation Three
    private fun checkOperationThreeState(state: ScreenState<PatientScanUploadResponse>){
        when(state){
            is ScreenState.Loading -> {
                progressBarScanResult.visibility =  View.VISIBLE
            }
            is ScreenState.Success ->{
                if(state.data != null){
                    progressBarScanResult.visibility = View.INVISIBLE
                    Log.e("Token", "${Flags.TOKEN}")
                    CancelButton().successChecking(state.data.success, this, message = state.data.message)
                        startActivity(Intent(this, OperationsActivity::class.java))
                        finish()
                }
            }
            is ScreenState.Error ->{
                if(state.message != null){
                    progressBarScanResult.visibility = View.INVISIBLE
                    Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                    Log.e("Message from Operation Three or Assign", "${state.message}")
                }
            }
        }
    }
    //Operation Three code ends


    //Operation Four Code Starts
    //assign result
    private fun assignResultOperationFour() {
        val barcode = Flags.BARCODE
        val awb = Flags.NAME_OR_EXPIRATION_OR_AWB
        val awbRequest = AwbSendDataRequest(barcode = barcode, awb = awb)
        viewModelOperationFour.awbSendData(awbRequest)
        Log.e("Send the data from operation AWB NO.", "$barcode, $awb")
    }
    //menage states
    fun menageOperationState(state: ScreenState<AwbSendDataResponse>?){
       when(state){
           is ScreenState.Loading ->{
               progressBarScanResult.visibility =  View.VISIBLE
           }
           is ScreenState.Success ->{
               if(state.data != null){
                   progressBarScanResult.visibility = View.INVISIBLE
                   CancelButton().successChecking(state.data.success, this, message = state.data.message)
                       startActivity(Intent(this, OperationsActivity::class.java))
                       finish()
               }
           }
           is ScreenState.Error -> {
              if(state.message != null){
                  progressBarScanResult.visibility = View.INVISIBLE
                  Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                  Log.e("Message from Operation Four or AWB No", "${state.message}")
              }
           }
       }
    }
    //Operation code ends here




    //Custom Cancellations
    override fun customCancellation(cancellationValue1: String?, cancellationValue2: Int?, cancellationValue3: Boolean?, ) {
        if (cancellationValue3 != null) {
            if(cancellationValue3 == false){
                isScanButtonPressed = cancellationValue3
                _isNextScanPressed = cancellationValue3
                _isScanButtonPressedTwice = cancellationValue3
                _isNextScanPressedTwice = cancellationValue3
            }
            if(cancellationValue3 == true){
                //true Values
            }

        }
    }

    //When pressed back on phone
    override fun onBackPressed() {
        //NavUtils.navigateUpFromSameTask(this)
        backDialog()
    }


    fun backDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure?")
        builder.setMessage("Going back would cancel the whole operation, are you sure you want to end it?")
        builder.setPositiveButton("YES"){dialog, _ ->
            startActivity(Intent(this, OperationsActivity::class.java))
            finish()
            dialog.dismiss()
        }
        builder.setNegativeButton("NO"){dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }


}