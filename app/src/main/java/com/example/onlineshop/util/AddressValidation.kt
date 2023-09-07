package com.example.onlineshop.util

sealed class AddressValidation(){
    object Success : AddressValidation()
    data class Failed(val message : String) : AddressValidation()
}

data class AddressValidationFailedState(
    val addressTitle : AddressValidation,
    val fullName : AddressValidation,
    val street : AddressValidation,
    val phone : AddressValidation,
    val city : AddressValidation,
    val state : AddressValidation
)