package com.example.grayscale3.Classes

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.grayscale.Activies.OperationsActivity
import com.example.grayscale.R


class Dialogs{

    private lateinit var mProgressDialog: Dialog

    fun noInternetDialog(context: Context, message: String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setTitle("No Internet Connection")
        builder.setMessage(message)
        builder.setIcon(R.drawable.ic_baseline_wifi_off_24)
        builder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0?.dismiss()
            }
        })
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    fun wrongCredentialsDialog(context: Context, message: String, title: String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK",object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0?.dismiss()
            }
        })
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }



    fun successfullySentData(context: Context, message: String, title: String, activity: Activity){
        val intent = Intent(context, OperationsActivity::class.java)
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(R.drawable.ic_baseline_done_all_24)
        builder.setPositiveButton("OK",object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0?.dismiss()
            }
        })
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    fun initProgressDialog(context: Context){
        mProgressDialog = Dialog(context)
    }

    fun showProgressDialog(){
        mProgressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        mProgressDialog.setContentView(R.layout.progres_dialog_layout)
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }







}