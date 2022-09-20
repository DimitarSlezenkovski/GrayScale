package com.example.grayscale3.Activies.GenerateCode


import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfDocument
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.widget.Toast
import com.example.grayscale.R
import com.example.grayscale3.Activies.GenerateCode.PdfFunctions.drawableToBitmap
import com.example.grayscale3.Activies.GenerateCode.PdfFunctions.setBitmap
import com.example.grayscale3.Activies.GenerateCode.PdfFunctions.setText
import com.example.grayscale3.Classes.GenerateBarcode
import com.example.grayscale3.DataHolders.Flags
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream


class PrintDirectory {

    val pageNumber = 1
    val pageWidth = 1000
    val pageHeight = 1300
    val MATCH_PARRENT_WIDTH = pageWidth
    val MATCH_PARRENT_HEIGHT = pageHeight


    //Not in use, when finished delete it
    fun createPdfFile(path: String, context: Context) {
        if(File(path).exists()){
            File(path).delete()
        }
        try {
            val document = Document()

            //Save
            PdfWriter.getInstance(document, FileOutputStream(path))
            //Write to File
            document.open()
            document.pageSize = PageSize.A4


            //Font settings
            val colorAccent = BaseColor(0,153,204, 255)
            val headingFontSize = 20.0F
            val valueFontSize = 26.0F

            val font = BaseFont.createFont("assets/font/OpenSans-SemiboldItalic.ttf", "UTF-8" ,
                BaseFont.EMBEDDED)

             val titleStyle = Font(font, 20.0f, Font.NORMAL, BaseColor.BLACK)
//            addText(document, "${Flags.GENERATE_BARCODE_CODE}", Element.ALIGN_LEFT, titleStyle)
//            addText(document, "Panorama Mother Blood", Element.ALIGN_RIGHT, titleStyle)
//            addText(document, "Name: Neda Cvetanovska Dimovska", Element.ALIGN_CENTER, titleStyle)
//            addText(document, "DOB: 1986-01-21", Element.ALIGN_CENTER, titleStyle)
//            addText(document, "Date: 2022-06-02", Element.ALIGN_RIGHT, titleStyle)

//            GenerateBarcodeClass().createDataMatrix().let {image ->
//                addImage(image, document, 50, 50)
//            }

            document.close()
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()

            printPdf(context, path)

        }catch (e: Exception){
            Log.e("Exception", "${e.message}")
        }

    }


    fun printBarcode(context: Context, path: String
                     , name:String, birthday: String, bloodDrawn: String, barcode: String){
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        val pageOne = pdfDocument.startPage(pageInfo)
        val canvas = pageOne.canvas

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        titlePaint.textSize = 20F


        setText(canvas, "${Flags.GENERATE_BARCODE_CODE}",90F, 30F, titlePaint)
        setText(canvas, "Panorama Mother Blood",500F, 30F, titlePaint)
        setText(canvas, "Name: $name",250F, 60F, titlePaint)
        setText(canvas, "Birthday: $birthday",170F, 85F, titlePaint)
        setText(canvas, "Blood drawn: $bloodDrawn",470F, 80F, titlePaint)

        GenerateBarcode().generate(barcode).let { bitmap ->
            setBitmap(canvas, bitmap, 70, 70, 20F, 40F, paint)
        }
        pdfDocument.finishPage(pageOne)
        pdfDocument.writeTo(FileOutputStream(path))
        pdfDocument.close()

        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()

        printPdf(context, path)
    }

    fun printInfertilityTest(
        path: String,
        context: Context,
        barcode: String,
        partnerTesting: Boolean
    ){
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()
        val smallerTextPaint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        val pageOne = pdfDocument.startPage(pageInfo)
        val canvas = pageOne.canvas

        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        titlePaint.textSize = 15F

        smallerTextPaint.textAlign = Paint.Align.CENTER
        smallerTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        smallerTextPaint.textSize = 10F

        val bitmapPage = BitmapFactory.decodeResource(context.resources, R.drawable.infertility_test_sif)
        setBitmap(canvas, bitmapPage, MATCH_PARRENT_WIDTH, MATCH_PARRENT_HEIGHT, 0F, 0F, paint)
        setBitmap(canvas, GenerateBarcode().generate(barcode), 70, 70, 800F, 30F, paint)
        //Patient information
        setText(canvas, "John",130F, 205F, titlePaint)
        setText(canvas, "Doelovski",350F, 205F, titlePaint)
        setText(canvas, "1979-5-6",130F, 250F, titlePaint)
        setText(canvas, "Male",350F, 250F, titlePaint)
        setText(canvas, "Christian",130F, 295F, titlePaint)
        setText(canvas, "070 980 489",350F, 295F, titlePaint)
        setText(canvas, "john@gmail.com",130F, 340F, titlePaint)
        setText(canvas, "2022-21-4",350F, 340F, titlePaint)
        setText(canvas, "St.500",60F, 380F, smallerTextPaint)
        setText(canvas, "Skopje",60F, 410F, smallerTextPaint)
        setText(canvas, "1001",215.5F, 415F, titlePaint)
        setText(canvas, "Macedonia",410F, 410F, smallerTextPaint)

        //Ordering physician information
        setText(canvas, "Jessica",650F, 205F, titlePaint)
        setText(canvas, "68",870F, 205F, titlePaint)
        setText(canvas, "Ena Medikal",750F, 250F, titlePaint)
        setText(canvas, "070890789", 650F, 295F, titlePaint)
        setText(canvas, "123456", 870F, 295F, titlePaint)
        setText(canvas, "api@grayscale.mk",750F, 340F, titlePaint)
        setText(canvas, "St.500",550F, 380F, smallerTextPaint)
        setText(canvas, "Skopje",550F, 410F, smallerTextPaint)
        setText(canvas, "1002",715.5F, 415F, titlePaint)
        setText(canvas, "Macedonia",900F, 410F, smallerTextPaint)

        setText(canvas, "John Doelovski-Makedonski",600F, 497F, titlePaint)
        setText(canvas, "1979-5-6",920F, 497F, titlePaint)
        drawableToBitmap(R.drawable.done, context).let {
            if(it != null){
                if(partnerTesting){
                    setBitmap(canvas, it, 20, 20, 40F, 480.10F, paint)
                }else{
                    setBitmap(canvas, it, 20, 20, 250F, 480.10F, paint)
                }
            }
            Log.e("bitmap", "$it")
        }



        pdfDocument.finishPage(pageOne)


        pdfDocument.writeTo(FileOutputStream(path))
        pdfDocument.close()

        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()

        printPdf(context, path)




    }


    private fun printPdf(context: Context, path: String) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        try {
            val printAdapter = PdfDocumentAdapter(context, path)
            printManager.print("Document", printAdapter, PrintAttributes.Builder().build())

        }catch (e: Exception){
            Log.e("Exception in printing at printPdf", "${e.message}")
        }

    }








}


