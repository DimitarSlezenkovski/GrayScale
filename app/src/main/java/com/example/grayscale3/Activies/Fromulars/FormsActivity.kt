package com.example.grayscale3.Activies.Fromulars

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.grayscale.DataHolders.Constants
import com.example.grayscale.DataHolders.Constants.FORMS_TO_FORMS_ACTIVITY_DATA_KEY
import com.example.grayscale.DataHolders.Constants.infertilityTestBarcode
import com.example.grayscale.R

class FormsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forms)
        val barcode = intent.getStringExtra(FORMS_TO_FORMS_ACTIVITY_DATA_KEY)
        supportActionBar?.title = resources.getString(R.string.forms)
        val infertilityTest = findViewById<Button>(R.id.InfertilityTest)
        infertilityTest.setOnClickListener {
            val intent = Intent(this, InfertilityTestActivity::class.java)
            intent.putExtra(infertilityTestBarcode, barcode)
            startActivity(intent)
        }



    }
}