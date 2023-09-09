package com.example.onlineshop.viewmodel

import androidx.lifecycle.ViewModel
import com.example.onlineshop.data.SupportMessage
import com.example.onlineshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SupportViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _sendMessageState = MutableStateFlow<Resource<Unit>>(Resource.Unspecified())
    val sendMessageState = _sendMessageState.asStateFlow()

    fun sendMessageForSupport(message: SupportMessage){

    }

}