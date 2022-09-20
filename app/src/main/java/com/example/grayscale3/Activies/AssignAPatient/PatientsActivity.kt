package com.example.grayscale3.Activies.AssignAPatient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.grayscale.Activies.OperationsActivity
import com.example.grayscale.R
import com.example.grayscale3.Activies.FinishedPostingData
import com.example.grayscale3.Adapters.PatientAdapter
import com.example.grayscale3.Classes.*
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.Network.SessionManager
import com.example.grayscale3.RequestAndResponse.Patients.BarcodeAndName
import com.example.grayscale3.ViewModels.PatientsViewModel
import com.example.grayscale3.Classes.ScreenState

class PatientsActivity : AppCompatActivity(), PatientAdapter.ItemClickedPatients{

    private val viewModel: PatientsViewModel by lazy {
        ViewModelProvider(this).get(PatientsViewModel::class.java)
    }

    private lateinit var noPatientsTV: TextView
    private lateinit var cancelPatientsUpload: Button
    private lateinit var layout: LinearLayoutManager
    private lateinit var refreshPatients: SwipeRefreshLayout
    private lateinit var networkChecker: CheckNetwork
    private lateinit var pb: ProgressBar
    private lateinit var patientsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patients)
        //Go back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Patients")


        if(Flags.TOKEN.isNullOrEmpty()){
            SessionManager(this).fetchToken()?.let { token ->
                Flags.TOKEN = token
            }
        }


        noPatientsTV = findViewById(R.id.NoPatientsTV)
        cancelPatientsUpload = findViewById(R.id.CancelPatientsUpload)
        refreshPatients = findViewById(R.id.refreshPatients)
        patientsRecyclerView = findViewById(R.id.PatientsRecyclerView)
        layout = LinearLayoutManager(this)
        pb = findViewById(R.id.PBPatients)
        networkChecker = CheckNetwork()


        cancelPatientsUpload.setOnClickListener {
           CancelButton().cancelButton()
            startActivity(Intent(this, OperationsActivity::class.java))
            finish()
        }

        if(!networkChecker.isNetworkAvailable(this)){
            Dialogs().noInternetDialog(this, "No internet connection, please turn it on and try again")
            noPatientsTV.text = "No internet"
            pb.visibility = View.GONE
        }

        refreshPatients.setOnChildScrollUpCallback(object : SwipeRefreshLayout.OnChildScrollUpCallback {
            override fun canChildScrollUp(parent: SwipeRefreshLayout, child: View?): Boolean {
                if (patientsRecyclerView != null) {
                    return patientsRecyclerView.canScrollVertically(-1)
                }
                return false
            }
        })


        if(networkChecker.isNetworkAvailable(this)){
            viewModel.patients.observe(this){ _patients ->
                processResponse(_patients)
            }
        }

        patientsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                baseContext,
                layout.orientation
            )
        )



        refreshPatients.setOnRefreshListener {
            if(!networkChecker.isNetworkAvailable(this)){
                Dialogs().noInternetDialog(this, "No internet connection, please turn it on and try again")
                refreshPatients.isRefreshing = false
                pb.visibility = View.GONE
                return@setOnRefreshListener
            }else{
                viewModel.patients.observe(this){ _patients ->
                    processResponse(_patients)
                }
            }


        }


    }

    private fun processResponse(state: ScreenState<List<String>>){
        when(state){
            is ScreenState.Loading -> {
                pb.visibility = View.VISIBLE
            }
            is ScreenState.Success -> {
                pb.visibility = View.GONE
                if(state.data != null){
                    val adapter = PatientAdapter(state.data, this)
                    patientsRecyclerView.layoutManager = layout
                    patientsRecyclerView.adapter = adapter
                    refreshPatients.isRefreshing = false
                    noPatientsTV.text = null
                }
            }
            is ScreenState.Error ->{
                if(Flags.stopProgressBarIfNoPatientsLoaded){
                    cancelPatientsUpload.text = "Go back"
                    noPatientsTV.text = "No Patients"
                    Log.e("error", "${state.message}")
                    refreshPatients.isRefreshing = false
                    pb.visibility = View.GONE
                }
                if(Flags.stopProgressBarIfSocketTimeoutExceptionAccruedPatients){
                    cancelPatientsUpload.text = "Try again"
                    noPatientsTV.text = "Something went wrong, try again"
                    Log.e("error", "${state.message}")
                    refreshPatients.isRefreshing = false
                    pb.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemClicked(position: Int, patients: List<String>) {
        if(!networkChecker.isNetworkAvailable(this)){
            Dialogs().noInternetDialog(this, "No internet connection, please turn it on and try again")
            return
        }
        val patient = patients.get(position)
        Flags.NAME_OR_EXPIRATION_OR_AWB = patient
        BarcodeAndName(Flags.BARCODE.toString(), Flags.NAME_OR_EXPIRATION_OR_AWB.toString())
        startActivity(Intent(this, FinishedPostingData::class.java))
        finish()
        Toast.makeText(this, "$patient", Toast.LENGTH_SHORT).show()
    }




}