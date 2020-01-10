package com.andersclark.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registration_registerbutton.setOnClickListener{

            val email = registration_email.text.toString()
            val password = registration_password.text.toString()
            Log.d("MainActivity", "EMAIL: $email")
            Log.d("MainActivity", "PASSWORD: $password")

        }
        registration_alreadyhaveaccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
           startActivity(intent)
        }
    }
}
