package com.example.grayscale3.Classes


import android.content.Context
import android.widget.Toast
import com.example.grayscale3.DataHolders.Flags

class CancelButton{
    fun cancelButton(){
        //Cancel Utils
        Flags.BARCODE = null
        Flags.NAME_OR_EXPIRATION_OR_AWB = null
        //Cancel Operations
        Flags.AssignPatient = false
        Flags.Shipment = false
        Flags.IntakeFullKit = false
        Flags.AWD_NO = false
        Flags.GENERATE_CODE = false
    }

    fun successChecking(success: String?, context: Context?, message: String? = null){
        when(success){
            "false" -> Toast.makeText(context, "Scanned but not assigned", Toast.LENGTH_SHORT).show()
            "true" -> Toast.makeText(context, "Added to the database", Toast.LENGTH_SHORT).show()
            "-" -> Toast.makeText(context, "Already Assigned", Toast.LENGTH_SHORT).show()
            null -> Toast.makeText(context, "Something went wrong, message: $message", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(context, "$message", Toast.LENGTH_SHORT).show()
        }
    }

}