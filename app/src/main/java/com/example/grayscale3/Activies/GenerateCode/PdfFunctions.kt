package com.example.grayscale3.Activies.GenerateCode

import android.content.Context
import android.graphics.*
import com.itextpdf.text.Chunk
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import java.io.ByteArrayOutputStream


object PdfFunctions {
    private lateinit var scaledBitmap: Bitmap

    //Not in use
    fun addText(document: Document, text: String, alignment: Int, style: Font){
        val chunk = Chunk(text, style)
        val paragraph = Paragraph(chunk)
        paragraph.alignment = alignment
        document.add(paragraph)
    }

    //Not in use
    fun addImage(image: Bitmap, document: Document, imageWidth: Int, imageHeight: Int){
        val scaledBitmap = Bitmap.createScaledBitmap(image, imageWidth, imageHeight, false)
        val stream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bitmapData = stream.toByteArray()
        document.setMargins(30F, 30F, 30F, 30F)
        document.add(com.itextpdf.text.Image.getInstance(bitmapData))
    }


    fun setText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint){
        canvas.drawText(text, x, y, paint)
    }

    fun setBitmap(canvas: Canvas, bitmap: Bitmap, bitmapWidth: Int, bitmapHeight: Int, left: Float, top: Float, paint: Paint ){
        scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, false)
        canvas.drawBitmap(scaledBitmap, left, top, paint)
    }

    fun drawableToBitmap(drawable: Int, context: Context): Bitmap? {
        return BitmapFactory.decodeResource(
            context.resources,
            drawable
        )
    }



}