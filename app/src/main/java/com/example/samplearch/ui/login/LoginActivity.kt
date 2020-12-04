package com.example.samplearch.ui.login

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels

import com.example.samplearch.R

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels { LoginViewModelFactory() }

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var login: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)

        loginViewModel.uiState.observe(this@LoginActivity, Observer(::updateUi))

        login.setOnClickListener {
            onLogin()
        }
    }

    private fun onLogin() {
        var isDataValid = true
        if (!loginViewModel.isPasswordValid(password.text.toString())) {
            isDataValid = false
            password.error = getString(R.string.invalid_password)
        }

        if (!loginViewModel.isUserNameValid(username.text.toString())) {
            isDataValid = false
            username.error = getString(R.string.invalid_username)
        }

        if (isDataValid) loginViewModel.login(username.text.toString(), password.text.toString())
    }

    private fun updateUi(uiState: LoginUiState) {
        onLoading(false)
        when (uiState) {
            LoginUiState.Loading -> onLoading(true)
            is LoginUiState.Success -> onSuccess(uiState.loggedInUser)
            is LoginUiState.Error -> onError(uiState.error)
        }
    }

    private fun onError(error: Throwable) {
        Toast.makeText(applicationContext, getString(R.string.login_failed), Toast.LENGTH_SHORT)
            .show()
    }

    private fun onSuccess(loggedInUser: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = loggedInUser.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun onLoading(isLoading: Boolean) {
        val loading = findViewById<ProgressBar>(R.id.loading)

        loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}