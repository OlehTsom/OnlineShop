package com.example.onlineshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.Address
import com.example.onlineshop.util.validatinon.AddressValidation
import com.example.onlineshop.util.validatinon.AddressValidationFailedState
import com.example.onlineshop.util.Constants.COLLECTION_PATH_ADDRESS
import com.example.onlineshop.util.Constants.COLLECTION_PATH_USER
import com.example.onlineshop.util.Resource
import com.example.onlineshop.util.validatinon.validateAddressTitle
import com.example.onlineshop.util.validatinon.validateCity
import com.example.onlineshop.util.validatinon.validateFullName
import com.example.onlineshop.util.validatinon.validatePhoneNumber
import com.example.onlineshop.util.validatinon.validateState
import com.example.onlineshop.util.validatinon.validateStreetName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddressViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asStateFlow()

    private val _deleteAddressResult = MutableSharedFlow<Resource<Unit>>()
    val deleteAddressResult = _deleteAddressResult.asSharedFlow()

    private val _listAddress = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    private val listAddress = _listAddress.asStateFlow()

    private val _validateError = MutableSharedFlow<AddressValidationFailedState>()
    val validateError = _validateError.asSharedFlow()

    private val _updateAddressDate = MutableSharedFlow<Resource<Unit>>()
    val updateAddressDate = _updateAddressDate.asSharedFlow()

    private var addressDocuments = emptyList<DocumentSnapshot>()

    init {
        getAllAddressUser()
    }

    fun addAddressToAccount(address: Address){

        if (validationCheck(address)) {
            viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }
            firestore.collection(COLLECTION_PATH_USER)
                .document(auth.uid!!)
                .collection(COLLECTION_PATH_ADDRESS)
                .document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _addNewAddress.emit(Resource.Success(address))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _addNewAddress.emit(Resource.Error(it.message.toString()))
                    }
                }
        }else{
            val addressValidate = AddressValidationFailedState(
                validateAddressTitle(address.addressTitle),
                validateFullName(address.fullName),
                validateStreetName(address.street),
                validatePhoneNumber(address.phone),
                validateCity(address.city),
                validateState(address.state)
            )

            viewModelScope.launch {
                _validateError.emit(addressValidate)
            }
        }
    }

    private fun getAllAddressUser(){
        viewModelScope.launch { _listAddress.emit(Resource.Loading()) }
        firestore.collection(COLLECTION_PATH_USER).document(auth.uid!!).collection(
            COLLECTION_PATH_ADDRESS).addSnapshotListener{value, error ->
            if (error!= null || value == null){
                viewModelScope.launch { _listAddress.emit(Resource.Error(error?.message.toString())) }
            }else{
                addressDocuments = value.documents
                val addressList = value.toObjects(Address::class.java)
                viewModelScope.launch { _listAddress.emit(Resource.Success(addressList)) }
            }

        }
    }

    fun updateAddressDate(oldAddress : Address, newAddress: Address){
        viewModelScope.launch { _updateAddressDate.emit(Resource.Loading()) }
        val index = listAddress.value.data?.indexOf(oldAddress)
        if (index != null && index != -1) {
            val documentId = addressDocuments[index].id
            firestore.collection(COLLECTION_PATH_USER).document(auth.uid!!).collection(
                COLLECTION_PATH_ADDRESS).document(documentId).set(newAddress)
                .addOnSuccessListener {
                    viewModelScope.launch { _updateAddressDate.emit(Resource.Success(Unit)) }
                }.addOnFailureListener {
                    viewModelScope.launch { _updateAddressDate.emit(Resource.Error(it.message.toString())) }
                }
        }

    }


    fun deleteAddress(address: Address){
        viewModelScope.launch { _deleteAddressResult.emit(Resource.Loading()) }
        val index = listAddress.value.data?.indexOf(address)
        if (index != null && index != -1) {
            val documentId = addressDocuments[index].id
            firestore.collection(COLLECTION_PATH_USER).document(auth.uid!!).collection(
                COLLECTION_PATH_ADDRESS).document(documentId).delete()
                .addOnSuccessListener {
                    viewModelScope.launch { _deleteAddressResult.emit(Resource.Success(Unit)) }
                }.addOnFailureListener {
                    viewModelScope.launch { _deleteAddressResult.emit(Resource.Error(it.message.toString())) }
                }
        }

    }

    private fun validationCheck(address: Address) : Boolean{
        val validAddressTitle = validateAddressTitle(address.addressTitle)
        val validateFullName = validateFullName(address.fullName)
        val validateStreetName = validateStreetName(address.street)
        val validatePhoneNumber = validatePhoneNumber(address.phone)
        val validateCity = validateCity(address.city)
        val validateState = validateState(address.state)

        val validationResult = validAddressTitle is AddressValidation.Success &&
                validateFullName is AddressValidation.Success &&
                validateStreetName is AddressValidation.Success &&
                validatePhoneNumber is AddressValidation.Success &&
                validateCity is AddressValidation.Success &&
                validateState is AddressValidation.Success

        return validationResult

    }

}