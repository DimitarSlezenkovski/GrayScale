package com.example.grayscale.Activies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.NavUtils
import com.example.grayscale.R
import com.example.grayscale3.Activies.AwdNo.GetAwbRecords
import com.example.grayscale3.Activies.BarcodeScanners.Scanner
import com.example.grayscale3.Activies.Fromulars.FormsActivity
import com.example.grayscale3.Activies.IntakeFullKit.CitiesActivity
import com.example.grayscale3.Classes.CancelButton
import com.example.grayscale3.Classes.CheckNetwork
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.DataHolders.Flags.AWD_NO
import com.example.grayscale3.DataHolders.Flags.AssignPatient
import com.example.grayscale3.DataHolders.Flags.FORMS
import com.example.grayscale3.DataHolders.Flags.GENERATE_CODE
import com.example.grayscale3.DataHolders.Flags.IntakeFullKit
import com.example.grayscale3.DataHolders.Flags.Shipment
import com.example.grayscale3.Network.SessionManager

class OperationsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var shipment: Button
    private lateinit var intakeFullKit: Button
    private lateinit var assignPatient: Button
    private lateinit var networkChecker: CheckNetwork
    private lateinit var awbNumber: Button
    private lateinit var generateCode: Button
    private lateinit var forms: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operations)
        networkChecker = CheckNetwork()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Operations")
        //Not in use Flags.PARENT_ACTIVITY = "Operations Activity"


        CancelButton().cancelButton()
        Flags.LAB = null
        Flags.GENERATE_BARCODE_CODE = null

        if(Flags.TOKEN.isNullOrEmpty()){
            SessionManager(this).fetchToken()?.let { token ->
                Flags.TOKEN = token
            }
        }

        Log.e("barcode", "${Flags.BARCODE}")
        Log.e("name", "${Flags.NAME_OR_EXPIRATION_OR_AWB}")
        Log.e("shipment", "${Flags.Shipment}")
        Log.e("intake", "${Flags.IntakeFullKit}")
        Log.e("assign", "${Flags.AssignPatient}")
        Log.e("awb no", "${Flags.AWD_NO}")
        Log.e("generate", "${Flags.GENERATE_CODE}")

        shipment = findViewById(R.id.Shipment)
        intakeFullKit = findViewById(R.id.IntakeFullKit)
        assignPatient = findViewById(R.id.AssignPatient)
        awbNumber = findViewById(R.id.awbNumber)
        generateCode = findViewById(R.id.GenerateCode)
        forms = findViewById(R.id.Forms)

        awbNumber.setOnClickListener(this)
        shipment.setOnClickListener(this)
        intakeFullKit.setOnClickListener(this)
        assignPatient.setOnClickListener(this)
        generateCode.setOnClickListener(this)
        forms.setOnClickListener(this)

    }

    override fun onClick(view: View) {
       when(view.id){
           R.id.Shipment -> {
               Shipment = true
               IntakeFullKit = false
               AssignPatient = false
               AWD_NO = false
               GENERATE_CODE = false
               FORMS = false
               startActivity(Intent(this, Scanner::class.java))
               Toast.makeText(this, "Shipment", Toast.LENGTH_SHORT).show()
           }
           R.id.IntakeFullKit ->{
               IntakeFullKit = true
               Shipment = false
               AssignPatient = false
               AWD_NO = false
               GENERATE_CODE = false
               FORMS = false
               startActivity(Intent(this, CitiesActivity::class.java))
               Toast.makeText(this, "Intake FullKit", Toast.LENGTH_SHORT).show()
           }
           R.id.AssignPatient -> {
               AssignPatient = true
               Shipment = false
               IntakeFullKit = false
               AWD_NO = false
               GENERATE_CODE = false
               FORMS = false
               startActivity(Intent(this, Scanner::class.java))
               Toast.makeText(this, "Assign Patient", Toast.LENGTH_SHORT).show()
           }
           R.id.awbNumber ->{
               AWD_NO = true
               AssignPatient = false
               Shipment = false
               IntakeFullKit = false
               GENERATE_CODE = false
               FORMS = false
               startActivity(Intent(this, GetAwbRecords::class.java))
               Toast.makeText(this, "AWB NO.", Toast.LENGTH_SHORT).show()
           }
           R.id.GenerateCode ->{
               GENERATE_CODE = true
               AWD_NO = false
               AssignPatient = false
               Shipment = false
               IntakeFullKit = false
               FORMS = false
               startActivity(Intent(this, Scanner::class.java))
               Toast.makeText(this, "Generate Code", Toast.LENGTH_SHORT).show()
           }
           R.id.Forms ->{
               FORMS = true
               GENERATE_CODE = false
               AWD_NO = false
               AssignPatient = false
               Shipment = false
               IntakeFullKit = false
               startActivity(Intent(this, Scanner::class.java))
               Toast.makeText(this, "Forms", Toast.LENGTH_SHORT).show()
           }
       }

    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }

    override fun onResume() {
        super.onResume()
        CancelButton().cancelButton()
        Flags.LAB = null
        Flags.GENERATE_BARCODE_CODE = null

        if(Flags.TOKEN.isNullOrEmpty()){
            SessionManager(this).fetchToken()?.let { token ->
                Flags.TOKEN = token
            }
        }

        Log.e("barcode", "${Flags.BARCODE}")
        Log.e("LAB", "${Flags.LAB}")
        Log.e("name", "${Flags.NAME_OR_EXPIRATION_OR_AWB}")
        Log.e("shipment", "${Flags.Shipment}")
        Log.e("intake", "${Flags.IntakeFullKit}")
        Log.e("assign", "${Flags.AssignPatient}")
        Log.e("awb no", "${Flags.AWD_NO}")
        Log.e("generate", "${Flags.GENERATE_CODE}")


    }
}