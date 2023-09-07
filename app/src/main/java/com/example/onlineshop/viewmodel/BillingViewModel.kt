package com.example.onlineshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.Address
import com.example.onlineshop.util.Constants.COLLECTION_PATH_ADDRESS
import com.example.onlineshop.util.Constants.COLLECTION_PATH_USER
import com.example.onlineshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    init {
        getUserAddress()
    }

    fun getUserAddress(){
        viewModelScope.launch { _address.emit(Resource.Loading())}
        firestore.collection(COLLECTION_PATH_USER).document(auth.uid!!)
            .collection(COLLECTION_PATH_ADDRESS)
            .addSnapshotListener{value, error ->
                if (error != null) {
                    viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                }
                val address = value?.toObjects(Address::class.java)
                viewModelScope.launch { _address.emit(Resource.Success(address)) }
            }
    }

}