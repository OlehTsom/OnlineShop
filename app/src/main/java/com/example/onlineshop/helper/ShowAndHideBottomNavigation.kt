package com.example.onlineshop.helper

import android.view.View
import androidx.fragment.app.Fragment
import com.example.onlineshop.R
import com.example.onlineshop.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.showBottomNavigation(){
    val bottomNavigationView = (activity as ShoppingActivity)
        .findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNavigationView.visibility = View.VISIBLE
}

fun Fragment.hideBottomNavigation(){
    val bottomNavigationView = (activity as ShoppingActivity)
        .findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNavigationView.visibility = View.GONE
}