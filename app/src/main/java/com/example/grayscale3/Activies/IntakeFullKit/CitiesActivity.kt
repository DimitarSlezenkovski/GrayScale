package com.example.grayscale3.Activies.IntakeFullKit

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.grayscale.R
import com.example.grayscale3.Adapters.CityAdapter
import com.example.grayscale3.Classes.CheckNetwork
import com.example.grayscale3.Classes.Dialogs
import com.example.grayscale3.Classes.ScreenState
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.Network.SessionManager
import com.example.grayscale3.RequestAndResponse.IntakeFullKit.CitiesRequest
import com.example.grayscale3.ViewModels.CitiesViewModel
import java.util.*
import kotlin.collections.ArrayList


class CitiesActivity : AppCompatActivity(), CityAdapter.ItemClicked {
    private val viewModel: CitiesViewModel by lazy {
        ViewModelProvider(this).get(CitiesViewModel::class.java)
    }
    private lateinit var networkChecker: CheckNetwork
    private lateinit var refreshCities: SwipeRefreshLayout
    private lateinit var somethingWhenWrongDisplay: TextView
    //private lateinit var cancelCity: Button
    private lateinit var layout: LinearLayoutManager
    private lateinit var pb: ProgressBar
    private lateinit var cityRecyclerView: RecyclerView
    private lateinit var adapter: CityAdapter
    private var data: List<CitiesRequest> = listOf()
    private var citiesList: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities)
        Flags.Shipment = false
        Flags.AssignPatient = false
        Flags.AWD_NO = false

        supportActionBar?.setTitle("Cities")
        supportActionBar?.setSubtitle("Select a city")


        //Log Details
        Log.e("barcode", "${Flags.BARCODE}")
        Log.e("LAB", "${Flags.LAB}")
        Log.e("name", "${Flags.NAME_OR_EXPIRATION_OR_AWB}")
        Log.e("shipment", "${Flags.Shipment}")
        Log.e("intake", "${Flags.IntakeFullKit}")
        Log.e("assign", "${Flags.AssignPatient}")
        Log.e("awb no", "${Flags.AWD_NO}")
        Log.e("generate", "${Flags.GENERATE_CODE}")

        if(Flags.TOKEN.isNullOrEmpty()){
            SessionManager(this).fetchToken()?.let { token ->
                Flags.TOKEN = token
            }
        }


        //cancelCity = findViewById(R.id.CancelCity)
        refreshCities = findViewById(R.id.refreshCities)
        layout = LinearLayoutManager(this)

        networkChecker = CheckNetwork()
        pb = findViewById(R.id.progressBar2)
        cityRecyclerView = findViewById(R.id.CityRecyclerView)
        cityRecyclerView.isNestedScrollingEnabled = false
        cityRecyclerView.isVerticalScrollBarEnabled = false
        somethingWhenWrongDisplay = findViewById(R.id.SomethingWhenWrongDisplay)

        //Go back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(!networkChecker.isNetworkAvailable(this)){
            Dialogs().noInternetDialog(this, "No internet connection, turn it on and try again")
            pb.visibility = View.GONE
            somethingWhenWrongDisplay.text = "No Internet"
        }

        refreshCities.setOnChildScrollUpCallback(object : SwipeRefreshLayout.OnChildScrollUpCallback {
            override fun canChildScrollUp(parent: SwipeRefreshLayout, child: View?): Boolean {
                if (cityRecyclerView != null) {
                    return cityRecyclerView.canScrollVertically(-1)
                }
                return false
            }
        })

        if(networkChecker.isNetworkAvailable(this)){
            viewModel.fetchCities()
            viewModel.citiesData.observe(this){state ->
                processResponse(state)
            }
        }

        cityRecyclerView.addItemDecoration(
            DividerItemDecoration(
                baseContext,
                layout.orientation
            )
        )

//        cancelCity.setOnClickListener {
//            CancelButton().cancelButton()
//            startActivity(Intent(this, OperationsActivity::class.java))
//            finish()
//        }

        refreshCities.setOnRefreshListener {
            if(networkChecker.isNetworkAvailable(this)){
                viewModel.fetchCities()
                viewModel.citiesData.observe(this){
                    Log.e("Refreshed", "Cities, ${it.data}")
                    //adapter.removeData()
                    processResponse(it)
                }
            }else{
                Dialogs().noInternetDialog(this, "No internet connection, turn it on and try again")
                refreshCities.isRefreshing = false
                pb.visibility = View.GONE
                return@setOnRefreshListener
            }
        }





    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return true
    }



    private fun processResponse(state: ScreenState<List<CitiesRequest>>){
        when(state){
            is ScreenState.Loading -> {
                pb.visibility = View.VISIBLE
            }
            is ScreenState.Success -> {
                pb.visibility = View.GONE
                if(state.data != null) {
                    data = state.data
                    adapter = CityAdapter(state.data, this, this)
                    cityRecyclerView.layoutManager = layout
                    cityRecyclerView.adapter = adapter
                    cityRecyclerView.isNestedScrollingEnabled = false
                    refreshCities.isRefreshing = false
                    somethingWhenWrongDisplay.text = null
                }
            }
            is ScreenState.Error -> {
                if(state.message != null){
                    pb.visibility = View.GONE
                    Toast.makeText(this,  "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                    Log.e("error from cities", "${state.message}")
                    refreshCities.isRefreshing = false
                }

            }
        }
    }

    override fun cityClick(position: Int, items: List<String>) {
        val item = items.get(position)
        Flags.CITY = item
        val intent = Intent(this, LabsActivity::class.java)
        startActivity(intent)
    }


}
