package com.example.onlineshop.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.example.onlineshop.R
import com.example.onlineshop.fragments.loginRegister.AccountOptionsFragment

class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        val navController = navHostFragment?.findNavController()

        val currentFragment = navController?.currentDestination

        if (currentFragment?.id == R.id.accountOptionsFragment) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}
