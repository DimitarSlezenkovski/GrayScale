package com.example.grayscale3.Classes

import android.graphics.Bitmap
import android.util.Log
import com.example.grayscale3.DataHolders.Flags
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class GenerateBarcode {

    fun generate(barcodeData: String?): Bitmap {
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(
            barcodeData,
            BarcodeFormat.DATA_MATRIX,
            500,
            500
        )
        val barcodeEncoder = BarcodeEncoder()
        val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
        return bitmap
    }



}