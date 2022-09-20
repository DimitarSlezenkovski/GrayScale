package com.example.grayscale3.Activies.GenerateCode

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.util.Log
import java.io.*
import java.lang.Exception

class PdfDocumentAdapter(private var context: Context, private var path: String): PrintDocumentAdapter() {

    override fun onLayout(
        p0: PrintAttributes?,
        p1: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        layoutResultCallback: LayoutResultCallback?,
        p4: Bundle?
    ) {
        if(cancellationSignal!!.isCanceled){
            layoutResultCallback?.onLayoutCancelled()
        }else{
            val builder = PrintDocumentInfo.Builder("Your file name here")
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .build()
            layoutResultCallback?.onLayoutFinished(builder.build(), p1 != p0)
        }
    }

    override fun onWrite(
        pageRanges: Array<out PageRange>?,
        parcelFileDescriptor: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        writeResultCallback: WriteResultCallback?
    ) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            val file = File(path)
            inputStream = FileInputStream(file)
            outputStream = FileOutputStream(parcelFileDescriptor!!.fileDescriptor)

            if(!cancellationSignal!!.isCanceled){
                inputStream.copyTo(outputStream)
                writeResultCallback!!.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            }else{
                writeResultCallback!!.onWriteCancelled()
            }
        }catch (e: Exception){
            writeResultCallback!!.onWriteFailed(e.message)
            Log.e("error EX", "${e.message}")
        }finally {
            try {
                inputStream!!.close()
                outputStream!!.close()
            }catch (e: IOException){
                Log.e("error IO", "${e.message}")
            }
        }





    }
}