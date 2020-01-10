package com.andersclark.kotlinmessenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        login_button.setOnClickListener {
            //DO SOMETHING'
            val email = login_email.text.toString()
            val password = login_password.text.toString()
            Log.d("LoginActivity", "EMAIL: $email")
            Log.d("LoginActivity", "PASSWORD: $password")
        }
        login_returntoregistration.setOnClickListener {
            finish()
        }
    }

}
