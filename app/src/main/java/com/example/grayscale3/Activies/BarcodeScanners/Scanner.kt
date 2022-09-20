package com.example.grayscale3.Activies.BarcodeScanners

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.SparseArray
import android.view.MenuItem
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.util.isNotEmpty
import com.example.grayscale.DataHolders.Constants.FORMS_TO_FORMS_ACTIVITY_DATA_KEY
import com.example.grayscale.R
import com.example.grayscale3.Activies.FinishedPostingData
import com.example.grayscale3.Activies.GenerateCode.ShowGeneratedBarcode
import com.example.grayscale3.Activies.AssignAPatient.PatientsActivity
import com.example.grayscale3.Activies.AwdNo.GetAwbRecords
import com.example.grayscale3.Activies.Shipment.BarcodeResultActivity
import com.example.grayscale3.Activies.Fromulars.FormsActivity
import com.example.grayscale3.Classes.OperationState
import com.example.grayscale3.Classes.Vibrate
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.DataHolders.Flags.AWD_NO
import com.example.grayscale3.DataHolders.Flags.AssignPatient
import com.example.grayscale3.DataHolders.Flags.FORMS
import com.example.grayscale3.DataHolders.Flags.GENERATE_CODE
import com.example.grayscale3.DataHolders.Flags.Shipment
import com.example.grayscale3.Network.SessionManager
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.lang.StringBuilder

class Scanner : AppCompatActivity() {
    private lateinit var soundPlayer: MediaPlayer
    private lateinit var _cameraSource: CameraSource
    private lateinit var _detector: BarcodeDetector
    private lateinit var sb: StringBuilder
    private lateinit var cameraView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        _firstDetections = true
        sb = StringBuilder()
        cameraView = findViewById(R.id.surfaceView)
        //Go back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setLogo(R.drawable.logo)

        //For scanner sound
        soundPlayer = MediaPlayer.create(this, R.raw.scanner_sound)

        //Log Details
        Log.e("barcode", "${Flags.BARCODE}")
        Log.e("LAB", "${Flags.LAB}")
        Log.e("name", "${Flags.NAME_OR_EXPIRATION_OR_AWB}")
        Log.e("shipment", "${Flags.Shipment}")
        Log.e("intake", "${Flags.IntakeFullKit}")
        Log.e("assign", "${Flags.AssignPatient}")
        Log.e("awb no", "${Flags.AWD_NO}")
        Log.e("generate", "${Flags.GENERATE_CODE}")
        Log.e("Forms", "${Flags.FORMS}")

        if(AWD_NO){
            //Not in use Flags.PARENT_ACTIVITY = "AWB START NUMBERS RECORDS"
            supportActionBar?.setTitle("AWB NO.")
        }
        if(AssignPatient){
            //Not in use  Flags.PARENT_ACTIVITY = "ASSIGN PATIENT START OPERATIONS"
            supportActionBar?.setTitle("Assign a Patient")
        }
        if(Shipment){
            //Not in use  Flags.PARENT_ACTIVITY = "SHIPMENT START OPERATIONS"
            supportActionBar?.setTitle("Shipment")
        }
        if(GENERATE_CODE){
            //Not in use  Flags.PARENT_ACTIVITY = "SHIPMENT START OPERATIONS"
            supportActionBar?.setTitle("Generate Code")
        }

        if(Flags.TOKEN.isNullOrEmpty()){
            SessionManager(this).fetchToken()?.let { token ->
                Flags.TOKEN = token
            }
        }


        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    scanner()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@Scanner, "You have denied the permission", Toast.LENGTH_SHORT).show()
                    NavUtils.navigateUpFromSameTask(this@Scanner)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    showSettingsDialog(this@Scanner)
                }

            }).onSameThread().check()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(AWD_NO){
            when(item.itemId) {
                android.R.id.home -> {
                    startActivity(Intent(this, GetAwbRecords::class.java))
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(AWD_NO){
            super.onBackPressed()
        }else{
            NavUtils.navigateUpFromSameTask(this)
        }

    }

    private fun scanner() {
        _detector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.CODE_128).build()
        _cameraSource = CameraSource.Builder(this, _detector).setAutoFocusEnabled(true).build()
        cameraView.holder.addCallback(camera)
        _detector.setProcessor(decoder)
    }




    private val camera = object : SurfaceHolder.Callback{
        override fun surfaceCreated(p0: SurfaceHolder) {
            if (ActivityCompat.checkSelfPermission(
                    this@Scanner,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            _cameraSource.start(p0)
        }
        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}
        override fun surfaceDestroyed(p0: SurfaceHolder) {_cameraSource.stop()}
    }

    private var _firstDetections: Boolean = false
    private val decoder = object : Detector.Processor<Barcode>{
        override fun release() {}
        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
           if(detections.detectedItems.isNotEmpty()){
               if(detections?.detectedItems.size() > 0 && _firstDetections){
                   val qrCodes: SparseArray<Barcode> = detections.detectedItems
                   val code = qrCodes.valueAt(0)
                   val displayCodes = code.displayValue
                   if(Shipment){
                       manageOperationStates(OperationState.Export(displayCodes))
                   }
                   if(AssignPatient){
                       manageOperationStates(OperationState.Assign(displayCodes))
                   }
                   if(AWD_NO){
                      manageOperationStates(OperationState.AwbNo(displayCodes))
                   }
                   if(GENERATE_CODE){
                      manageOperationStates(OperationState.GenerateBarcode(displayCodes))
                   }
                   if(FORMS){
                       manageOperationStates(OperationState.Forms(displayCodes))
                   }
               }
           }
        }

    }

    private fun manageOperationStates(operation: OperationState<String>) {
        when(operation){
            is OperationState.Export -> {
                if(!sb.contains(operation.barcode)){
                    sb.append(operation.barcode).append(",")
                }
                if (sb.endsWith(",")) {
                    sb.deleteCharAt( sb.length - 1 )
                    Flags.BARCODE = sb.toString()
                    Log.e("One", "${Flags.BARCODE}")
                }
                this@Scanner.runOnUiThread{
                    changeSubtitle(operation.barcode)
                }
                Log.e("One", "Operation One with barcode ${Flags.BARCODE} and expiration ${Flags.NAME_OR_EXPIRATION_OR_AWB}")
                Log.e("One", "from state ${operation.barcode}")
                val intent = Intent(this@Scanner, BarcodeResultActivity::class.java)
                intent.putExtra("BARCODE", Flags.BARCODE)
                soundPlayer.start()
                Vibrate().vibratePhone(this@Scanner)
                startActivity(intent)
                finish()
                _firstDetections = false
            }
            is OperationState.Import -> {
                Toast.makeText(this, "Not in Use", Toast.LENGTH_SHORT).show()
            }
            is OperationState.Assign -> {
                Flags.BARCODE = operation.barcode
                Log.e("Two", "Operation Three with barcode ${Flags.BARCODE}")
                Log.e("Two", "Operation Three with barcode from state ${operation.barcode}")
                if (_firstDetections){
                    this@Scanner.runOnUiThread{
                        changeSubtitle(operation.barcode)
                    }
                }
                soundPlayer.start()
                Vibrate().vibratePhone(this@Scanner)
                startActivity(Intent(this@Scanner, PatientsActivity::class.java))
                finish()
                _firstDetections = false
            }
            is OperationState.AwbNo -> {
                Flags.BARCODE = operation.barcode
                Log.e("AWB_NO", "${Flags.BARCODE}")
                Log.e("AWB_NO", " from state${operation.barcode}")
                if (_firstDetections){
                    this@Scanner.runOnUiThread{
                        changeSubtitle(operation.barcode)
                    }
                }
                soundPlayer.start()
                Vibrate().vibratePhone(this@Scanner)

                startActivity(Intent(this@Scanner, FinishedPostingData::class.java))
                finish()
                _firstDetections = false
            }
            is OperationState.GenerateBarcode -> {
                Flags.GENERATE_BARCODE_CODE = operation.barcode
                Log.e("Generate code", "${Flags.GENERATE_BARCODE_CODE}")
                Log.e("Generate code", "barcode from state ${operation.barcode}")
                if (_firstDetections){
                    this@Scanner.runOnUiThread{
                        changeSubtitle(operation.barcode)
                    }
                }
                soundPlayer.start()
                Vibrate().vibratePhone(this@Scanner)
                startActivity(Intent(this@Scanner, ShowGeneratedBarcode::class.java))
                finish()
                _firstDetections = false
            }
            is OperationState.Forms -> {

                Log.e("Generate code", "${Flags.GENERATE_BARCODE_CODE}")
                Log.e("Generate code", "barcode from state ${operation.barcode}")
                if (_firstDetections){
                    this@Scanner.runOnUiThread{
                        changeSubtitle(operation.barcode)
                    }
                }
                soundPlayer.start()
                val intent = Intent(this@Scanner, FormsActivity::class.java)
                intent.putExtra(FORMS_TO_FORMS_ACTIVITY_DATA_KEY, operation.barcode)
                Vibrate().vibratePhone(this@Scanner)
                startActivity(intent)
                finish()
                _firstDetections = false
            }


        }
    }

    private fun showSettingsDialog(context: Context){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS"){dialog, _ ->
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel")
        { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }


    private fun changeSubtitle(subTitle: String){
        supportActionBar?.subtitle = subTitle
    }





}