package com.example.grayscale.Activies

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grayscale.DataHolders.Constants
import com.example.grayscale.DataHolders.Constants.DATA_STORE_KEY_EMAIL
import com.example.grayscale.DataHolders.Constants.DATA_STORE_KEY_PASSWORD
import com.example.grayscale.R
import com.example.grayscale.RequestAndResponse.Login.UserRequest
import com.example.grayscale.RequestAndResponse.Login.UserResponse
import com.example.grayscale.ViewModels.LoginViewModel
import com.example.grayscale3.Classes.AutomaticallyLogIn
import com.example.grayscale3.Classes.CheckNetwork
import com.example.grayscale3.Classes.Dialogs
import com.example.grayscale3.Classes.ScreenState
import com.example.grayscale3.DataHolders.Flags
import com.example.grayscale3.Network.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var shakeAnim: Animation
    private lateinit var viewModel: LoginViewModel
    private lateinit var email : EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var networkChecker: CheckNetwork
    private lateinit var sessionManager: SessionManager
    private lateinit var autologinManager: AutomaticallyLogIn


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.Email)
        password = findViewById(R.id.Password)
        loginBtn = findViewById(R.id.LoginBtn)
        progressBar = findViewById(R.id.progressBar)
        networkChecker = CheckNetwork()
        progressBar.bringToFront()
        progressBar.visibility = View.INVISIBLE
        sessionManager = SessionManager(this)
        autologinManager = AutomaticallyLogIn(this)
        supportActionBar?.setTitle("Premium Genetics")
        Log.e("token123", "${Flags.TOKEN}")
        shakeAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.vibrate_animation)







        initViewModel()
        hideKeyboard()
        loginBtn.setOnClickListener {
            disableLoginButton()
            if(!networkChecker.isNetworkAvailable(this)){
                Dialogs().noInternetDialog(this, "No Internet connection, turn on internet connection and try again")
                enableLoginButton()
                return@setOnClickListener
            }
            if(email.text.isEmpty() && password.text.isEmpty()){
                email.startAnimation(shakeAnim)
                password.startAnimation(shakeAnim)
                email.setBackgroundResource(R.drawable.outlined_et_wrong)
                password.setBackgroundResource(R.drawable.outlined_et_wrong)
                email.setError("Field is blank")
                password.setError("Field is blank")
                enableLoginButton()
                return@setOnClickListener
            }else{
                email.setBackgroundResource(R.drawable.outlined_et)
                password.setBackgroundResource(R.drawable.outlined_et)
                enableLoginButton()
            }
            if(email.text.isEmpty()){
                email.startAnimation(shakeAnim)
                email.setError("Field is blank")
                email.setBackgroundResource(R.drawable.outlined_et_wrong)
                enableLoginButton()
                return@setOnClickListener
            }
            else{
                email.setBackgroundResource(R.drawable.outlined_et)
                enableLoginButton()
            }
            if(password.text.isEmpty()){
                password.startAnimation(shakeAnim)
                password.setError("Field is blank")
                password.setBackgroundResource(R.drawable.outlined_et_wrong)
                enableLoginButton()
                return@setOnClickListener
            } else{
                password.setBackgroundResource(R.drawable.outlined_et)
            }
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
                email.startAnimation(shakeAnim)
                email.setError("Incorrect email")
                email.setBackgroundResource(R.drawable.outlined_et_wrong)
                enableLoginButton()
                return@setOnClickListener
            }else{
                email.setBackgroundResource(R.drawable.outlined_et)
                enableLoginButton()
            }
            disableLoginButton()
            loginUser(email = email.text.toString(), password = password.text.toString())


        }


    }

    private fun disableLoginButton(){
        loginBtn.isEnabled = false
        loginBtn.isClickable = false
    }
    private fun enableLoginButton(){
        loginBtn.isEnabled = true
        loginBtn.isClickable = true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        hideKeyboard()
        return super.onTouchEvent(event)
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private var _isLoginButtonPressed: Boolean = false
    private fun loginUser(email: String, password:String) {
        val user = UserRequest(email = email, password = password)
        viewModel.loginUser(user, this)
        _isLoginButtonPressed = true
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.loginData.observe(this){response ->
           processResponse(response)
        }


    }
    private fun processResponse(state: ScreenState<UserResponse?>){
        when(state){
            is ScreenState.Loading -> {
                progressBar.visibility = View.VISIBLE
            }
            is ScreenState.Success -> {
                _isLoginButtonPressed = false
                if(state.data != null) {
                    if (state.data.message.isNullOrEmpty()) {
                        progressBar.visibility = View.GONE
                        sessionManager.fetchToken()?.let { token ->
                            Flags.TOKEN = token
                            Log.e("token", "$token")
                        }
                        lifecycleScope.launch {
                            autologinManager.save(DATA_STORE_KEY_EMAIL, email.text.toString())
                            autologinManager.save(DATA_STORE_KEY_PASSWORD, password.text.toString())
                        }
                        Log.e("message", "${state.data.message}")
                        Log.e("status", "${state.data.status}")
                        Log.e("Token", "${Flags.TOKEN}")
                        Log.e("TokenType", "${Flags.TOKEN_TYPE}")
                        disableLoginButton()
                        startActivity(Intent(this, TestActivity::class.java))
                        finish()
                        Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "${state.data.message}", Toast.LENGTH_SHORT).show()
                        enableLoginButton()
                        email.startAnimation(shakeAnim)
                        password.startAnimation(shakeAnim)
                        email.setError(state.data.message)
                        email.setBackgroundResource(R.drawable.outlined_et_wrong)
                        password.setError(state.data.message)
                        password.setBackgroundResource(R.drawable.outlined_et_wrong)
                        //Dialogs().wrongCredentialsDialog(context = this, message = "${state.data.message}", "Incorrect credentials")
                    }
                }
            }
            is ScreenState.Error -> {
                if(state.message != null){
                    enableLoginButton()
                    Log.e("Error from login", "${state.message}, ${state.data}")
                    Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_SHORT).show()
                    _isLoginButtonPressed = false
                    progressBar.visibility = View.GONE
                }

            }
        }

    }
}