package com.example.onlineshop.util.validatinon

sealed class MessageValidation{
    object Success : MessageValidation()

    data class Failed(val messError : String) : MessageValidation()
}

data class MessageValidationFailedState(
    val name : MessageValidation,
    val email : MessageValidation,
    val message : MessageValidation
)
