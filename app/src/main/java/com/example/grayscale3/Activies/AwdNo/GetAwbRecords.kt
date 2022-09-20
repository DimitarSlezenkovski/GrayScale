package com.example.grayscale3.Activies.AwdNo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.grayscale.R
import com.example.grayscale3.Activies.BarcodeScanners.Scanner
import com.example.grayscale3.Adapters.AwbNumbersAdapter
import com.example.grayscale3.Classes.CheckNetwork
import com.example.grayscale3.Classes.Dialogs
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.Network.SessionManager
import com.example.grayscale3.ViewModels.AwbNo.AwbGetNumberViewModel
import com.example.grayscale3.Classes.ScreenState

class GetAwbRecords : AppCompatActivity(), AwbNumbersAdapter.GetItemThatIsClicked{

    private val viewModel: AwbGetNumberViewModel by lazy {
        ViewModelProvider(this).get(AwbGetNumberViewModel::class.java)
    }

    private lateinit var awbProgressBar: ProgressBar
    private lateinit var refreshAwb: SwipeRefreshLayout
    private lateinit var inCaseAError: TextView
    private lateinit var awbRecyclerView: RecyclerView
    private lateinit var networkCheck: CheckNetwork
    private lateinit var layout: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.get_awb_records_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("AWB NO.")
        supportActionBar?.setSubtitle("Select a AWB NO")
        awbProgressBar = findViewById(R.id.AwbProgressBar)
        refreshAwb = findViewById(R.id.RefreshAwb)
        inCaseAError = findViewById(R.id.InCaseAError)
        awbRecyclerView = findViewById(R.id.AwbRecyclerView)
        layout = LinearLayoutManager(this)
        networkCheck = CheckNetwork()
        //Not in use Flags.PARENT_ACTIVITY = "Get Awb Records"


        if(Flags.TOKEN.isNullOrEmpty()){
            SessionManager(this).fetchToken()?.let { token ->
                Flags.TOKEN = token
            }
        }


        if(!networkCheck.isNetworkAvailable(this)){
            Dialogs().noInternetDialog(this, "No internet connection, please turn it on and try again")
            awbProgressBar.visibility = View.GONE
            inCaseAError.text = "No Internet"
        }
        if(networkCheck.isNetworkAvailable(this)){
            viewModel.awbGetNumbers()
            viewModel.numbersData.observe(this){state ->
                manageStates(state = state)
            }
        }


        refreshAwb.setOnChildScrollUpCallback(object : SwipeRefreshLayout.OnChildScrollUpCallback {
            override fun canChildScrollUp(parent: SwipeRefreshLayout, child: View?): Boolean {
                if (awbRecyclerView != null) {
                    return awbRecyclerView.canScrollVertically(-1)
                }
                return false
            }
        })

        awbRecyclerView.addItemDecoration(
            DividerItemDecoration(
                baseContext,
                layout.orientation
            )
        )


        refreshAwb.setOnRefreshListener {
            if(!networkCheck.isNetworkAvailable(this)){
                Dialogs().noInternetDialog(this, "No internet connection, please turn it on and try again")
                refreshAwb.isRefreshing = false
                awbProgressBar.visibility = View.GONE
                return@setOnRefreshListener
            }else{
                viewModel.awbGetNumbers()
                viewModel.numbersData.observe(this){state ->
                    Log.e("Refreshed", "AWB NO., ${state.data}")
                    manageStates(state = state)
                }
            }
        }



    }

    private fun manageStates(state: ScreenState<List<String>?>){
        when(state){
            is ScreenState.Loading ->{
                awbProgressBar.visibility = View.VISIBLE
                refreshAwb.isRefreshing = false
            }
            is ScreenState.Success -> {
                awbProgressBar.visibility = View.GONE
                if(state.data != null){
                    val adapter = AwbNumbersAdapter(state.data, this)
                    inCaseAError.text = null
                    awbRecyclerView.layoutManager = layout
                    awbRecyclerView.adapter = adapter
                    refreshAwb.isRefreshing = false
                    inCaseAError.text = null
                }else{
                    inCaseAError.text = "No Data"
                    refreshAwb.isRefreshing = false
                }

            }
            is ScreenState.Error -> {
                if(state.message != null){
                    inCaseAError.text = "No AWB Numbers"
                    Log.e("errror", "${state.message}")
                    awbProgressBar.visibility = View.GONE
                    refreshAwb.isRefreshing = false
                }
                if(state.message == "400"){
                    //inCaseAError.text = state.message2.toString()
                    Toast.makeText(this, "${state.message2.toString()}", Toast.LENGTH_SHORT).show()
                    awbProgressBar.visibility = View.GONE
                    refreshAwb.isRefreshing = false
                }
                if(state.message == "401"){
                    //inCaseAError.text = state.message2.toString()
                    Toast.makeText(this, "${state.message2.toString()}", Toast.LENGTH_SHORT).show()
                    awbProgressBar.visibility = View.GONE
                    refreshAwb.isRefreshing = false
                }
                if(state.message == "500"){
                    //inCaseAError.text = state.message2.toString()
                    Toast.makeText(this, "${state.message2.toString()}", Toast.LENGTH_SHORT).show()
                    awbProgressBar.visibility = View.GONE
                    refreshAwb.isRefreshing = false
                }

            }

        }
    }

    override fun getItemClicked(position: Int, items: List<String>) {
        if(!networkCheck.isNetworkAvailable(this)){
            Dialogs().noInternetDialog(this, "No internet connection, please turn it on and try again")
            return
        }else{
            val item = items.get(position)
            Flags.NAME_OR_EXPIRATION_OR_AWB = item
            Log.e("AWB_NO", "${Flags.NAME_OR_EXPIRATION_OR_AWB}")
            val intent =  Intent(this, Scanner::class.java)
            startActivity(intent)
        }

    }

}