package com.example.onlineshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.Order
import com.example.onlineshop.util.Constants.COLLECTION_PATH_ORDERS
import com.example.onlineshop.util.Constants.COLLECTION_PATH_USER
import com.example.onlineshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AllOrdersViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) :ViewModel() {

    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrders = _allOrders.asStateFlow()

    init {
        getAllOrders()
    }

    private fun getAllOrders(){
        viewModelScope.launch { _allOrders.emit(Resource.Loading()) }

        firestore.collection(COLLECTION_PATH_USER).document(auth.uid!!).collection(COLLECTION_PATH_ORDERS).get()
            .addOnSuccessListener {
            val orders = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _allOrders.emit(Resource.Success(orders))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _allOrders.emit(Resource.Error(it.message.toString())) }
            }
    }

}