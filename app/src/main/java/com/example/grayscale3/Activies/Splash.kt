package com.example.grayscale3.Activies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bhargavms.dotloader.DotLoader
import com.example.grayscale.Activies.LoginActivity
import com.example.grayscale.Activies.TestActivity
import com.example.grayscale.DataHolders.Constants
import com.example.grayscale.R
import com.example.grayscale.RequestAndResponse.Login.UserRequest
import com.example.grayscale.ViewModels.LoginViewModel
import com.example.grayscale3.Classes.AutomaticallyLogIn
import com.example.grayscale3.Classes.CheckNetwork
import com.example.grayscale3.Classes.Dialogs
import com.example.grayscale3.Classes.ScreenState
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var automaticallyLogIn: AutomaticallyLogIn
    private lateinit var networkChecker: CheckNetwork
    private lateinit var dotLoader: DotLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        networkChecker = CheckNetwork()
        automaticallyLogIn = AutomaticallyLogIn(this)
        lifecycleScope.launch {
            val savedEmail = automaticallyLogIn.read(Constants.DATA_STORE_KEY_EMAIL)
            val savedPassword = automaticallyLogIn.read(Constants.DATA_STORE_KEY_PASSWORD)
            dotLoader = findViewById<DotLoader>(R.id.dotLoader)


            if (!networkChecker.isNetworkAvailable(this@Splash)) {
                Dialogs().noInternetDialog(this@Splash, "No internet connection, turn on internet connection and try again.")
                return@launch
            }
            Handler().postDelayed(
                {
                    if(savedEmail != null && savedPassword != null) {
                        loginUser(savedEmail, savedPassword)
                    }else{
                        val intent = Intent(this@Splash, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }, 2000
            )
        }

    }

    private fun loginUser(savedEmail: String, savedPassword: String) {
        val user = UserRequest(savedEmail, savedPassword)
        viewModel.loginUser(user, this)
        viewModel.loginData.observe(this){ state->
            when(state){
                is ScreenState.Success -> {
                    dotLoader.clearAnimation()
                    val intent = Intent(this@Splash, TestActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is ScreenState.Error -> {
                    dotLoader.clearAnimation()
                    val intent = Intent(this@Splash, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is ScreenState.Loading -> {
                    dotLoader.initAnimation()
                }


            }
        }
    }
}