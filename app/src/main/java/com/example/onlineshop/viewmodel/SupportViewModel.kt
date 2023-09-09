package com.example.onlineshop.viewmodel

import androidx.lifecycle.ViewModel
import com.example.onlineshop.data.SupportMessage
import com.example.onlineshop.util.Resource
import com.example.onlineshop.util.validatinon.MessageValidation
import com.example.onlineshop.util.validatinon.MessageValidationFailedState
import com.example.onlineshop.util.validatinon.RegisterFailedState
import com.example.onlineshop.util.validatinon.validateEmailInMessageSupport
import com.example.onlineshop.util.validatinon.validateMessInMessageSupport
import com.example.onlineshop.util.validatinon.validateNameInMessageSupport
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking

class SupportViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _sendMessageState = MutableStateFlow<Resource<Unit>>(Resource.Unspecified())
    val sendMessageState = _sendMessageState.asStateFlow()

    private val _validation = Channel<MessageValidationFailedState>()
    val validation = _validation.receiveAsFlow()

    fun sendMessageForSupport(message: SupportMessage){
        if (validateMessage(message)){

        }else{
            val messageFailedState = MessageValidationFailedState(
                validateNameInMessageSupport(message.name),
                validateEmailInMessageSupport(message.email),
                validateMessInMessageSupport(message.message)
            )
            runBlocking {
                _validation.send(messageFailedState)
            }
        }
    }

    private fun validateMessage(mess: SupportMessage) : Boolean{
        val validateName = validateNameInMessageSupport(mess.name)
        val validateEmail = validateEmailInMessageSupport(mess.email)
        val validateMessage = validateMessInMessageSupport(mess.message)

        val shouldValidation = validateName is MessageValidation.Success
                && validateEmail is MessageValidation.Success
                && validateMessage is MessageValidation.Success

        return shouldValidation
    }

}