package com.example.onlineshop.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.R
import com.example.onlineshop.util.Constants.INTRODUCTION_KEY
import com.example.onlineshop.util.Constants.INTRODUCTION_SP
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IntroductionViewModel(
    private val app : Application,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val sharedPreferences : SharedPreferences =
        app.getSharedPreferences(INTRODUCTION_SP,Context.MODE_PRIVATE)

    private val _navigate = MutableStateFlow(0)
    val navigate : StateFlow<Int> = _navigate


    init {
        val isButtonClick = sharedPreferences.getBoolean(INTRODUCTION_KEY,false)
        val user = firebaseAuth.currentUser

        if (user != null){
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        }else if (isButtonClick){
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTION_FRAGMENT)
            }
        }else{
            Unit
        }
    }

    fun startButtonClick(){
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY,true).apply()
    }

    companion object{
        const val SHOPPING_ACTIVITY = 23
        const val ACCOUNT_OPTION_FRAGMENT = R.id.action_introductionFragment_to_accountOptionsFragment
    }



}