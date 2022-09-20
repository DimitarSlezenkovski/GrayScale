package com.example.grayscale3.Classes

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.grayscale.Activies.OperationsActivity
import com.example.grayscale.Activies.TestActivity
import com.example.grayscale.R
import com.example.grayscale3.Activies.AwdNo.GetAwbRecords
import com.example.grayscale3.Activies.IntakeFullKit.CitiesActivity


open class InflateMenu : AppCompatActivity(){


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.top_navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

        }

        return super.onOptionsItemSelected(item)
    }

    fun getParentActivity(parent: String?){
        when(parent){
            "Operations Activity" ->{
                startActivity(Intent(this, TestActivity::class.java))
                finish()
            }
            "Cities Activity" -> {
                startActivity(Intent(this, OperationsActivity::class.java))
                finish()
            }
            "Labs Activity" -> {
                startActivity(Intent(this, CitiesActivity::class.java))
                finish()
            }
            "Get Awb Records" -> {
                startActivity(Intent(this, OperationsActivity::class.java))
                finish()
            }
            "AWB START NUMBERS RECORDS" -> {
                startActivity(Intent(this, GetAwbRecords::class.java))
                finish()
            }
            "ASSIGN PATIENT START OPERATIONS" ->{
                startActivity(Intent(this, OperationsActivity::class.java))
                finish()
            }
            "SHIPMENT START OPERATIONS" -> {
                startActivity(Intent(this, OperationsActivity::class.java))
                finish()
            }
        }
    }


}