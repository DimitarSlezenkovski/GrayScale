package com.example.grayscale3.Activies.GenerateCode

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.grayscale.R
import com.example.grayscale3.Activies.BarcodeScanners.Scanner
import com.example.grayscale3.Classes.CheckNetwork
import com.example.grayscale3.Classes.Dialogs
import com.example.grayscale3.Classes.GenerateBarcode
import com.example.grayscale3.Classes.ScreenState
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.RequestAndResponse.GenerateBarcode.PatientBarcodeInfoResponse
import com.example.grayscale3.ViewModels.GenerateBarcode.GeneratorViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


class ShowGeneratedBarcode : AppCompatActivity() {

    private val viewModel: GeneratorViewModel by lazy {
        ViewModelProvider(this)[GeneratorViewModel::class.java]
    }


    private lateinit var animations: LottieAnimationView
    private lateinit var showBarcodeCode: TextView
    private lateinit var pb: ProgressBar
    private lateinit var preview: Button
    private lateinit var barcodeData: String
    private lateinit var createPdf: PrintDirectory
    private lateinit var networkCheck: CheckNetwork
    private lateinit var getGeneratedBarcode: GenerateBarcode
    private lateinit var noContentTV: TextView

    private val NO_CONTENT: String = "No Content"
    private val NOT_VALIDATED: String = "Invalid Barcode Scanned"
    private var nameFetch: String? = null
    private var birthDayFetch: String? = null
    private var bloodDrownFetch: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_generated_barcode)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Generate Barcode"
        networkCheck = CheckNetwork()
        showBarcodeCode = findViewById(R.id.ShowBarcodeCode)
        noContentTV = findViewById(R.id.NoContentTV)
        preview = findViewById(R.id.print)
        pb = findViewById(R.id.pb)
        animations = findViewById(R.id.Animations)
        getGeneratedBarcode = GenerateBarcode()
        createPdf = PrintDirectory()
        barcodeData = Flags.GENERATE_BARCODE_CODE.toString()

        showBarcodeCode.text = "${barcodeData}"

        if(!networkCheck.isNetworkAvailable(this)){
            checkForNetworkNotAvailable()
        }

        if(networkCheck.isNetworkAvailable(this)){
            viewModel.fetchPatientInfo(barcodeData)
            viewModel.patientData.observe(this){receivedState ->
                manageState(receivedState)
            }
        }






    }

    private fun retryRequest() {
        if(networkCheck.isNetworkAvailable(this)){
            viewModel.fetchPatientInfo(barcodeData)
            viewModel.patientData.observe(this){receivedState ->
                manageState(receivedState)
            }
        }else{
            checkForNetworkNotAvailable()
        }

    }

    private fun checkForNetworkNotAvailable(){
        disablePrintButton()
        disableBarcodeImage()
        pb.visibility = View.GONE
        animations.setAnimation(R.raw.no_internet)
        animations.playAnimation()
        Dialogs().noInternetDialog(this, "No internet connection, turn it on and try again")
        buttonTextView("RETRY")
        removePrintButtonFromScreen()
        showBarcodeCode.setOnClickListener {
            retryRequest()
        }
    }

    private fun manageState(receivedState: ScreenState<PatientBarcodeInfoResponse>?) {
        when(receivedState){
            is ScreenState.Loading -> {
                pb.visibility = View.VISIBLE
                disablePrintButton()
                disableBarcodeImage()
            }
            is ScreenState.Success -> {
                pb.visibility = View.GONE
                if(receivedState.data != null){
                    when(receivedState.data.status){
                        null -> {
                            Log.e("Successful response", "${receivedState.data.status}")
                            enablePrintButton()
                            addPrintButtonToScreen()
                            enableBarcodeImage()
                            disableTextView()
                            normalTextView()
                            noContentTV.text = resources.getString(R.string.tap_to_see_information)
                            animations.setAnimation(R.raw.done)
                            animations.playAnimation()
                            Handler().postDelayed({
                                getGeneratedBarcode.generate(barcodeData).let {
                                    animations.setImageBitmap(it)
                                }
                            }, 2000)
                            nameFetch = receivedState.data.name
                            birthDayFetch = receivedState.data.birthday
                            bloodDrownFetch = receivedState.data.bloodDrawn
                            preview.setOnClickListener {
                                printButtonClicked(receivedState.data.name!!, receivedState.data.birthday!!, receivedState.data.bloodDrawn!!)
                            }
                            animations.setOnClickListener {
                                disableBarcodeImage()
                                bottomSheetDialog(receivedState.data.name!!, receivedState.data.birthday!!, receivedState.data.bloodDrawn!!)
                            }
                        }
                        "204" -> {
                            enablePrintButton()
                            addPrintButtonToScreen()
                            enableBarcodeImage()
                            Log.e("No Content response", "${receivedState.data.status}")
                            showBarcodeCode.text = receivedState.data.message
                            buttonTextView("Scan Again")
                            animations.setAnimation(R.raw.no_content)
                            animations.playAnimation()
                            noContentTV.text = NO_CONTENT
                            nameFetch = NO_CONTENT
                            birthDayFetch = NO_CONTENT
                            bloodDrownFetch = NO_CONTENT
                            showBarcodeCode.setOnClickListener {
                               startScanner()
                            }
                            preview.setOnClickListener {
                                Toast.makeText(this, "${receivedState.data.message}", Toast.LENGTH_SHORT).show()
                            }
                            animations.setOnClickListener {
                                disableBarcodeImage()
                                bottomSheetDialog(nameFetch!!, birthDayFetch!!, bloodDrownFetch!!)
                            }
                        }
                        "400" -> {
                            Log.e("Bad response", "${receivedState.data.status}")
                            enablePrintButton()
                            removePrintButtonFromScreen()
                            enableBarcodeImage()
                            animations.setAnimation(R.raw.wrong_barcodejson)
                            animations.playAnimation()
                            nameFetch = NOT_VALIDATED
                            birthDayFetch = NOT_VALIDATED
                            bloodDrownFetch = NOT_VALIDATED
                            noContentTV.text = NOT_VALIDATED
                            buttonTextView("Scan Again")
                            showBarcodeCode.setOnClickListener {
                                startScanner()
                            }
                            animations.setOnClickListener {
                                disableBarcodeImage()
                                bottomSheetDialog(nameFetch!!, birthDayFetch!!, bloodDrownFetch!!)
                            }
                        }
                    }

                }

            }
            is ScreenState.Error -> {
                pb.visibility = View.GONE
                Log.e("Error status response", "${receivedState.data?.status}")
                Log.e("Error message response", "${receivedState.data?.message}")
            }
        }
    }

    private fun printButtonClicked(name: String, birthday: String, bloodDrawn:String){
        disablePrintButton()
        Dexter.withActivity(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    printPdf(name, birthday, bloodDrawn)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    showSettingsDialog()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).onSameThread().check()
    }

    private fun buttonTextView(text: String){
        showBarcodeCode.text = text
        showBarcodeCode.textSize = 15F
        showBarcodeCode.setBackgroundResource(R.drawable.button_design)
        showBarcodeCode.setTextColor(resources.getColor(R.color.white))
    }

    private fun normalTextView() {
        showBarcodeCode.text = barcodeData
        showBarcodeCode.textSize = 25F
        showBarcodeCode.setBackgroundColor(resources.getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color))
        showBarcodeCode.setTextColor(resources.getColor(R.color.black))
    }

    private fun printPdf(nameFromApi: String, birthDayFromApi: String, bloodDrawnFromApi: String){
        createPdf.printBarcode(this@ShowGeneratedBarcode,
            Common.getAppPath(this@ShowGeneratedBarcode)+Flags.GENERATE_BARCODE_CODE,
            nameFromApi, birthDayFromApi, bloodDrawnFromApi, barcodeData)
    }

    private fun startScanner(){
        val intent = Intent(this, Scanner::class.java)
        Flags.GENERATE_CODE = true
        startActivity(intent)
        finish()
    }

    private fun removePrintButtonFromScreen(){
        preview.visibility = View.GONE
        preview.isEnabled = false
        preview.isClickable = false
    }

    private fun addPrintButtonToScreen(){
        preview.visibility = View.VISIBLE
        preview.isEnabled = true
        preview.isClickable = true
    }

    private fun disablePrintButton(){
        preview.isEnabled = false
        preview.isClickable = false
    }
    fun enablePrintButton(){
        preview.isEnabled = true
        preview.isClickable = true
    }
    private fun disableBarcodeImage(){
        animations.isEnabled = false
        animations.isClickable = false
    }
    private fun enableBarcodeImage(){
        animations.isEnabled = true
        animations.isClickable = true
    }
    private fun disableTextView(){
        showBarcodeCode.isEnabled = false
        showBarcodeCode.isClickable = false
    }
    private fun enableTextView(){
        showBarcodeCode.isEnabled = false
        showBarcodeCode.isClickable = false
    }


    private fun showSettingsDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS"){dialog, _ ->
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        builder.show();
    }

    private fun bottomSheetDialog(nameFromApi: String, birthDayFromApi: String, bloodDrawnFromApi: String){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.generate_barcode_bottom_sheet_view)
        val name = dialog.findViewById<TextView>(R.id.nameBottomSheetView)
        val birthday = dialog.findViewById<TextView>(R.id.birthdayBottomSheetView)
        val bloodDrawn = dialog.findViewById<TextView>(R.id.bloodDrawnBottomSheetView)
        val close = dialog.findViewById<ImageButton>(R.id.close)
        close.setOnClickListener {
            enableBarcodeImage()
            dialog.dismiss()
        }
        name.text = nameFromApi
        birthday.text =  birthDayFromApi
        bloodDrawn.text = bloodDrawnFromApi

        dialog.show()
        enableBarcodeImage()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.window?.attributes?.windowAnimations = R.style.BottomSheetViewAnim
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onResume() {
        enablePrintButton()
        enableBarcodeImage()
        super.onResume()
    }






}