package com.example.grayscale3.Activies.Shipment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NavUtils
import com.example.grayscale.R
import com.example.grayscale3.Activies.BarcodeScanners.AditionalScanners.ExpirationScannerActivity
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.Network.SessionManager

class BarcodeResultActivity : AppCompatActivity() {

    private lateinit var barcodeResultShow: TextView
    private lateinit var cancel: Button
    private lateinit var scanExpiration2:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_result)
        supportActionBar?.setTitle("Result")
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        barcodeResultShow = findViewById(R.id.BarcodeResultShow)
        cancel = findViewById(R.id.Cancel)
        scanExpiration2 = findViewById(R.id.ScanExpiration2)
        val barcode = intent.getStringExtra("BARCODE")
        barcodeResultShow.text = Flags.BARCODE

        //Log Details
        Log.e("barcode", "${Flags.BARCODE}")
        Log.e("LAB", "${Flags.LAB}")
        Log.e("name", "${Flags.NAME_OR_EXPIRATION_OR_AWB}")
        Log.e("shipment", "${Flags.Shipment}")
        Log.e("intake", "${Flags.IntakeFullKit}")
        Log.e("assign", "${Flags.AssignPatient}")
        Log.e("awb no", "${Flags.AWD_NO}")
        Log.e("generate", "${Flags.GENERATE_CODE}")


        if(Flags.TOKEN.isNullOrEmpty()){
            SessionManager(this).fetchToken()?.let { token ->
                Flags.TOKEN = token
            }
        }



        cancel.setOnClickListener {
            backDialog()
        }

        scanExpiration2.setOnClickListener {
            launchExpirationScanner()
        }

    }

    private fun launchExpirationScanner() {
        startActivity(Intent(this, ExpirationScannerActivity::class.java))
    }

    override fun onBackPressed() {
        backDialog()
    }

    private fun cancelOperation() {
        Flags.BARCODE = null
        Flags.Shipment = false
        Flags.IntakeFullKit = false
        Flags.AssignPatient = false
        Flags.AWD_NO = false
        Flags.GENERATE_CODE = false
    }

    private fun backDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure?")
        builder.setMessage("Going back would cancel the whole operation, are you sure you want to end it?")
        builder.setPositiveButton("YES"){dialog, _ ->
            cancelOperation()
            NavUtils.navigateUpFromSameTask(this)
            //startActivity(Intent(this, OperationsActivity::class.java))
            finish()
            dialog.dismiss()
        }
        builder.setNegativeButton("NO"){dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }


}