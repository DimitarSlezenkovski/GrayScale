package com.example.grayscale3.Activies.BarcodeScanners

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import com.example.grayscale.R
import com.example.grayscale3.Activies.FinishedPostingData
import com.example.grayscale3.Classes.Vibrate
import com.example.grayscale3.DataHolders.Flags
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

class OperationTwoBarcodeScanner : AppCompatActivity() {

    private val _REQUEST_CODE_PERMISSION: Int = 1001
    private lateinit var _cameraSource: CameraSource
    private lateinit var _detector: BarcodeDetector
    private lateinit var sb: StringBuilder
    private lateinit var cameraView: SurfaceView
    private lateinit var soundPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operation_two_barcode_scanner)
        supportActionBar?.setTitle("Intake FullKit")
        changeSubtitle("Barcode Scanner")


        _firstDetections = true
        sb = StringBuilder()

        //For scanner sound
        soundPlayer = MediaPlayer.create(this, R.raw.scanner_sound)

        cameraView = findViewById(R.id.CameraViewOperationTwo)

        //Log Details
        Log.e("barcode", "${Flags.BARCODE}")
        Log.e("LAB", "${Flags.LAB}")
        Log.e("name", "${Flags.NAME_OR_EXPIRATION_OR_AWB}")
        Log.e("shipment", "${Flags.Shipment}")
        Log.e("intake", "${Flags.IntakeFullKit}")
        Log.e("assign", "${Flags.AssignPatient}")
        Log.e("awb no", "${Flags.AWD_NO}")
        Log.e("generate", "${Flags.GENERATE_CODE}")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if(Flags.TOKEN.isNullOrEmpty()){
            SessionManager(this).fetchToken()?.let { token ->
                Flags.TOKEN = token
            }
        }


        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    scanner()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@OperationTwoBarcodeScanner, "You have denied the permission", Toast.LENGTH_SHORT).show()
                    NavUtils.navigateUpFromSameTask(this@OperationTwoBarcodeScanner)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    showSettingsDialog(this@OperationTwoBarcodeScanner)
                }

            }).onSameThread().check()



    }

    private var _firstDetections: Boolean = false


    private fun scanner() {
        _detector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.CODE_128).build()
        _cameraSource = CameraSource.Builder(this, _detector).setAutoFocusEnabled(true).build()
        cameraView.holder.addCallback(camera)
        _detector.setProcessor(decoder)
    }
    private val camera = object : SurfaceHolder.Callback{
        override fun surfaceCreated(p0: SurfaceHolder) {
            if (ActivityCompat.checkSelfPermission(
                    this@OperationTwoBarcodeScanner,
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

    private val decoder = object : Detector.Processor<Barcode>{
        override fun release() {}

        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
            if(detections?.detectedItems.size() > 0 && _firstDetections){
                //ScanResults
                val qrCodes: SparseArray<Barcode> = detections.detectedItems
                val code = qrCodes.valueAt(0)
                val displayCodes = code.displayValue

                if(Flags.IntakeFullKit){
                    if(!sb.contains(displayCodes)){
                        sb.append(displayCodes).append(",")
                    }

                    Log.e("Two", "Operation Two with barcode ${Flags.BARCODE}")
                    this@OperationTwoBarcodeScanner.runOnUiThread{
                        changeSubtitle(displayCodes)
                    }
                    if (sb.endsWith(",")) {
                        sb.deleteCharAt( sb.length - 1 )
                        Flags.BARCODE = sb.toString()
                        Log.e("Two", "sb, ${Flags.BARCODE}")
                    }
                    soundPlayer.start()
                    Vibrate().vibratePhone(this@OperationTwoBarcodeScanner)
                    startActivity(Intent(this@OperationTwoBarcodeScanner, FinishedPostingData::class.java))
                    _firstDetections = false
                }
            }

        }

    }
    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
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
        builder.show();
    }

    private fun changeSubtitle(subTitle: String){
        supportActionBar?.subtitle = subTitle
    }


}