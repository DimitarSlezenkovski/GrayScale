package com.example.grayscale3.Activies.Fromulars

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import com.airbnb.lottie.LottieAnimationView
import com.example.grayscale.DataHolders.Constants
import com.example.grayscale.R
import com.example.grayscale3.Activies.GenerateCode.Common
import com.example.grayscale3.Activies.GenerateCode.PrintDirectory
import com.example.grayscale3.Classes.GenerateBarcode
import com.example.grayscale3.DataHolders.Flags
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class InfertilityTestActivity : AppCompatActivity() {
    private lateinit var barcode: String
    private lateinit var animationsInfertilityTest: LottieAnimationView
    private lateinit var noContentTV_InfertilityTest: TextView
    private lateinit var showInfertilityBarcode: TextView
    private lateinit var printInfertilityTest: Button
    private lateinit var printDirectory: PrintDirectory
    private val NO_CONTENT: String = "No Content"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infertlity_test)
        val barcodeReceived = intent.getStringExtra(Constants.infertilityTestBarcode)
        if(barcodeReceived != null){
            barcode = barcodeReceived
        }else{
            barcode = resources.getString(R.string.please_scan_again)
            Toast.makeText(this, resources.getString(R.string.please_scan_again), Toast.LENGTH_SHORT).show()
        }

        supportActionBar?.setTitle(resources.getString(R.string.infertility_test_activity))
        animationsInfertilityTest = findViewById(R.id.AnimationsInfertilityTest)
        noContentTV_InfertilityTest = findViewById(R.id.NoContentTV_InfertilityTest)
        showInfertilityBarcode = findViewById(R.id.ShowInfertilityBarcode)
        printInfertilityTest = findViewById(R.id.printInfertilityTest)
        printDirectory = PrintDirectory()
        noContentTV_InfertilityTest.text = resources.getString(R.string.tap_to_see_information)
        showInfertilityBarcode.text = barcode

        animationsInfertilityTest.setOnClickListener {
            bottomSheetDialog()
        }
        printInfertilityTest.setOnClickListener {
            askPermission()
        }
        GenerateBarcode().generate(barcode).let {
            animationsInfertilityTest.setImageBitmap(it)
        }

    }

    private fun askPermission() {
        Dexter.withActivity(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    printFrom()
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

    private fun printFrom() {
        printDirectory.printInfertilityTest(
            Common.getAppPath(this)+Flags.GENERATE_BARCODE_CODE,
            this,
            barcode,
            false
        )
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

    private fun bottomSheetDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.generate_barcode_bottom_sheet_view)
        val name = dialog.findViewById<TextView>(R.id.nameBottomSheetView)
        val birthday = dialog.findViewById<TextView>(R.id.birthdayBottomSheetView)
        val bloodDrawn = dialog.findViewById<TextView>(R.id.bloodDrawnBottomSheetView)
        val close = dialog.findViewById<ImageButton>(R.id.close)
        close.setOnClickListener {
            dialog.dismiss()
        }
        name.text = NO_CONTENT
        birthday.text =  NO_CONTENT
        bloodDrawn.text = NO_CONTENT

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.window?.attributes?.windowAnimations = R.style.BottomSheetViewAnim
        dialog.window?.setGravity(Gravity.BOTTOM)
    }
}