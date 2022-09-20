package com.example.grayscale.Activies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grayscale.Adapters.TestAdapter
import com.example.grayscale.R
import com.example.grayscale3.Classes.AutomaticallyLogIn
import com.example.grayscale3.Classes.CheckNetwork
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.Network.SessionManager
import kotlinx.coroutines.launch


class TestActivity :  AppCompatActivity() {
    private lateinit var networkChecker: CheckNetwork
    private lateinit var panorama: Button
    private lateinit var horizon: Button
    private lateinit var sessionManager: SessionManager
    private lateinit var automaticallyLogIn: AutomaticallyLogIn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        networkChecker = CheckNetwork()

        panorama = findViewById(R.id.Panorama)
        horizon= findViewById(R.id.Horizon)
        sessionManager = SessionManager(this)
        automaticallyLogIn = AutomaticallyLogIn(this)

        supportActionBar?.setTitle("Premium Genetics")



        Flags.BARCODE = null
        Flags.NAME_OR_EXPIRATION_OR_AWB = null

        Flags.Shipment = false
        Flags.IntakeFullKit = false
        Flags.AssignPatient = false


        if(Flags.TOKEN.isNullOrEmpty()){
            SessionManager(this).fetchToken()?.let { token ->
                Flags.TOKEN = token
            }
        }

        panorama.setOnClickListener {
            Toast.makeText(this, "Panorama", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, OperationsActivity::class.java))
        }

        horizon.setOnClickListener {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.LogOut -> {
                logOutDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logOutDialog(){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("LOG OUT")
        alertDialog.setMessage("Are you sure you want to log out?")
        alertDialog.setPositiveButton("LOG OUT"){dialog, _ ->
            logOutUser()
        }
        alertDialog.setNegativeButton("NO"){dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.create().show()
    }

    private fun logOutUser() {
        Flags.TOKEN =  null
        lifecycleScope.launch {
            automaticallyLogIn.delete()
        }
        sessionManager.deleteToken()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


}