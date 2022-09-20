package com.example.grayscale3.Activies.BarcodeScanners.AditionalScanners

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.MenuItem
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.util.isNotEmpty
import com.example.grayscale.R
import com.example.grayscale3.Activies.AwdNo.GetAwbRecords
import com.example.grayscale3.Activies.FinishedPostingData
import com.example.grayscale3.Classes.Vibrate
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.Network.SessionManager
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.lang.StringBuilder

class ExpirationScannerActivity : AppCompatActivity(){
    private lateinit var soundPlayer: MediaPlayer
    private lateinit var _cameraSource: CameraSource
    private lateinit var _detector: BarcodeDetector
    private lateinit var cameraView: SurfaceView
    private lateinit var sb: StringBuilder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expiration_scanner)
        //Back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //First detection
        _firstDetections = true


        //For scanner sound
        soundPlayer = MediaPlayer.create(this, R.raw.scanner_sound)
        changeSubtitle("Expiration Scanner")

        //Log Details
        Log.e("barcode", "${Flags.BARCODE}")
        Log.e("LAB", "${Flags.LAB}")
        Log.e("name", "${Flags.NAME_OR_EXPIRATION_OR_AWB}")
        Log.e("shipment", "${Flags.Shipment}")
        Log.e("intake", "${Flags.IntakeFullKit}")
        Log.e("assign", "${Flags.AssignPatient}")
        Log.e("awb no", "${Flags.AWD_NO}")
        Log.e("generate", "${Flags.GENERATE_CODE}")

        sb = StringBuilder()
        //Tittle
        supportActionBar?.setTitle("Expirations")


        cameraView = findViewById(R.id.CameraView)

        if(Flags.TOKEN.isNullOrEmpty()){
            SessionManager(this).fetchToken()?.let { token ->
                Flags.TOKEN = token
            }
        }

        expirationScanner()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(Flags.AWD_NO){
            when(item.itemId) {
                android.R.id.home -> {
                    NavUtils.navigateUpFromSameTask(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }

    private fun expirationScanner() {
        _detector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.CODE_128).build()
        _cameraSource = CameraSource.Builder(this, _detector).setAutoFocusEnabled(true).build()
        cameraView.holder.addCallback(camera)
        _detector.setProcessor(decoder)
    }

    private val camera = object : SurfaceHolder.Callback{
        override fun surfaceCreated(p0: SurfaceHolder) {
            if(ActivityCompat.checkSelfPermission(this@ExpirationScannerActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                return
            }else{_cameraSource.start(p0)}
        }
        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}
        override fun surfaceDestroyed(p0: SurfaceHolder) {_cameraSource.stop()}
    }

    private var _firstDetections: Boolean = false


    private val decoder = object : Detector.Processor<Barcode>{
        override fun release(){}

        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
            if(detections.detectedItems.isNotEmpty()){
                if(detections.detectedItems.size() > 0 && _firstDetections){
                    val qrCodes: SparseArray<Barcode> = detections.detectedItems
                    val code = qrCodes.valueAt(0)
                    val displayCodes = code.displayValue
                    if(!sb.contains(displayCodes)){
                        sb.append(displayCodes).append(",")

                    }
                    this@ExpirationScannerActivity.runOnUiThread {
                        changeSubtitle(displayCodes)
                    }
                    if (sb.endsWith(",")) {
                        sb.deleteCharAt( sb.length - 1 )
                        Flags.NAME_OR_EXPIRATION_OR_AWB = sb.toString()
                        Log.e("Exp", "sb, ${Flags.NAME_OR_EXPIRATION_OR_AWB }")
                    }
                    soundPlayer.start()
                    Vibrate().vibratePhone(this@ExpirationScannerActivity)
                    startActivity(Intent(this@ExpirationScannerActivity, FinishedPostingData::class.java))
                    finish()
                    _firstDetections = false
                }
            }
        }
    }

    private fun changeSubtitle(subTitle: String){
        supportActionBar?.subtitle = subTitle
    }




}