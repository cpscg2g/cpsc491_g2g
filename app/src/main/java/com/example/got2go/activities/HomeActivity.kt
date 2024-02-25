package com.example.got2go.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.core.app.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.got2go.R
import com.example.got2go.fragments.HomeFragment
import com.example.got2go.fragments.WikiFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val actionBar: ActionBar? = supportActionBar
        var bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val fragment: Fragment? = null
            when (item.itemId) {
                R.id.action_home -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.flContainer, HomeFragment()).commit()
                    true
                }

                R.id.action_wiki -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.flContainer, WikiFragment()).commit()
                    true
                }

                else -> false
            }
        })
        bottomNavigationView.selectedItemId = R.id.action_home
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.Logout) {
            goLoginActivity()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goLoginActivity() {
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()
    }
}