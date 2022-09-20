package com.example.grayscale3.Activies.IntakeFullKit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.example.grayscale.R
import com.example.grayscale3.Activies.BarcodeScanners.OperationTwoBarcodeScanner
import com.example.grayscale3.Adapters.LabsAdapter
import com.example.grayscale3.Classes.CheckNetwork
import com.example.grayscale3.Classes.Dialogs
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.Network.SessionManager
import com.example.grayscale3.ViewModels.LabsViewModel
import com.example.grayscale3.Classes.ScreenState
import java.util.*
import kotlin.collections.ArrayList

class LabsActivity : AppCompatActivity(), LabsAdapter.ClickedItemLabs {
    private val viewModel: LabsViewModel by lazy {
        ViewModelProvider(this).get(LabsViewModel::class.java)
    }

    private lateinit var layout: LinearLayoutManager
    private lateinit var noLabsTV: TextView
    private lateinit var refreshLabs: SwipeRefreshLayout
    private lateinit var networkChecker: CheckNetwork
    //private lateinit var cancelLabs: Button
    private lateinit var pb: ProgressBar
    private lateinit var labsRecyclerView: RecyclerView
    private var labs : List<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_labs)
        Flags.Shipment = false
        Flags.AssignPatient = false
        supportActionBar?.setTitle("Laboratories")
        supportActionBar?.setSubtitle("Select a laboratory")
        //Not in use Flags.PARENT_ACTIVITY = "Labs Activity"

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



        layout = LinearLayoutManager(this)
        noLabsTV = findViewById(R.id.NoLabsTV)
        networkChecker = CheckNetwork()
        networkChecker.checkNetwork(this)
       // cancelLabs = findViewById(R.id.CancelLabs)
        refreshLabs = findViewById(R.id.refreshLabs)
        labsRecyclerView = findViewById(R.id.LabsRecyclerView)
        pb = findViewById(R.id.PBLabs)

        //Go back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if(!networkChecker.isNetworkAvailable(this)){
            Dialogs().noInternetDialog(this, "No internet connection, turn it on and try again")
            pb.visibility = View.GONE
            noLabsTV.text = "No Internet"
        }

        if(networkChecker.isNetworkAvailable(this)){
            viewModel.fetchLabs()
            viewModel.labsData.observe(this){state ->
                processResponse(state)
            }
        }

        refreshLabs.setOnChildScrollUpCallback(object : SwipeRefreshLayout.OnChildScrollUpCallback {
            override fun canChildScrollUp(parent: SwipeRefreshLayout, child: View?): Boolean {
                if (labsRecyclerView != null) {
                    return labsRecyclerView.canScrollVertically(-1)
                }
                return false
            }
        })


//        cancelLabs.setOnClickListener {
//            CancelButton().cancelButton()
//            startActivity(Intent(this, OperationsActivity::class.java))
//            finish()
//        }

        labsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                baseContext,
                layout.orientation
            )
        )

        refreshLabs.setOnRefreshListener {
            if(!networkChecker.isNetworkAvailable(this)){
                Dialogs().noInternetDialog(this, "No internet connection, please turn it on and try again")
                refreshLabs.isRefreshing = false
                pb.visibility = View.GONE
                return@setOnRefreshListener
            }else{
                viewModel.fetchLabs()
                viewModel.labsData.observe(this){state ->
                    Log.e("Refreshed", "Labs, ${state.data}")
                    processResponse(state)
                }
            }
        }



    }

    private fun processResponse(state: ScreenState<List<String>>){
        when(state){
            is ScreenState.Loading ->{
                pb.visibility = View.VISIBLE
                refreshLabs.isRefreshing = false
            }
            is ScreenState.Success -> {
                pb.visibility = View.GONE
                if(state.data != null){
                    val adapter = LabsAdapter(state.data, this)
                    labs = state.data
                    labsRecyclerView.layoutManager = layout
                    labsRecyclerView.adapter = adapter
                    refreshLabs.isRefreshing = false
                    noLabsTV.text = null
                }
            }
            is ScreenState.Error -> {
                if(Flags.stopProgressBarIfNoLabsAreFetched){
                    noLabsTV.text = "No Laboratories"
                    pb.visibility = View.GONE
                    refreshLabs.isRefreshing = false
                }
                if(Flags.stopProgressBarIfSocketTimeoutExceptionAccrued){
                    noLabsTV.text = "Something went wrong, try again"
                    pb.visibility = View.GONE
                    refreshLabs.isRefreshing = false
                }
                refreshLabs.isRefreshing = false
                pb.visibility = View.GONE
                Log.e("error from labs", "${state.message}")
                Toast.makeText(this,  "No Laboratories", Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun getItemClick(position: Int, items: List<String>) {
        networkChecker.checkNetwork(this)
        if(!networkChecker.isNetworkAvailable(this)){
            Dialogs().noInternetDialog(this, "No internet connection, please turn it on and try again")
            return
        }
        val item = items.get(position)
        Flags.LAB = item
        Toast.makeText(this, "${Flags.LAB}", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, OperationTwoBarcodeScanner::class.java)

        startActivity(intent)


    }
}