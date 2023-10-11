package com.example.onlineshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.Order
import com.example.onlineshop.util.Constants.COLLECTION_PATH_CART
import com.example.onlineshop.util.Constants.COLLECTION_PATH_ORDERS
import com.example.onlineshop.util.Constants.COLLECTION_PATH_USER
import com.example.onlineshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

     private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
     val order = _order.asStateFlow()

    fun placeOrder(order: Order){
        viewModelScope.launch { _order.emit(Resource.Loading()) }
        firestore.runBatch {batch ->
            firestore.collection(COLLECTION_PATH_USER)
                .document(auth.uid!!)
                .collection(COLLECTION_PATH_ORDERS)
                .document()
                .set(order)

            val orderFroAdmin = order.copy(userUid = auth.uid!!)

            firestore.collection(COLLECTION_PATH_ORDERS)
                .document()
                .set(orderFroAdmin)

            firestore.collection(COLLECTION_PATH_USER).document(auth.uid!!).collection(COLLECTION_PATH_CART).get()
                .addOnSuccessListener {
                    it.documents.forEach {
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resource.Success(order))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _order.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}