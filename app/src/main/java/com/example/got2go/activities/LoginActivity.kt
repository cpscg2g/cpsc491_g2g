package com.example.got2go.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.got2go.R
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var etUsername: EditText = findViewById(R.id.etUsername)
        var etPassword: EditText = findViewById(R.id.etPassword)
        var btnLogin: Button = findViewById(R.id.btnLogin)
        var btnSignUp: Button = findViewById(R.id.btnSignUp)
        btnLogin.setOnClickListener(View.OnClickListener {
            Log.i(TAG, "Login button tapped")
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
        })
    }

    private fun goMainActivity() {
        val i = Intent(this, HomeActivity::class.java)
        startActivity(i)
        finish() //makes main activity the "default" page, closes login activity for access
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}
