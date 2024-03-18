package com.example.got2go.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.got2go.R
import com.example.got2go.fragments.SignUpDialogFragment

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

        btnSignUp.setOnClickListener {
            Log.i(TAG, "Sign-up button tapped")
            //login fragment
            val signUpDialogFragment = SignUpDialogFragment()
            signUpDialogFragment.show(supportFragmentManager, SignUpDialogFragment.TAG)
        }

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
