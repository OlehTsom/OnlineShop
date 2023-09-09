package com.example.onlineshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.SupportMessage
import com.example.onlineshop.util.Constants.COLLECTION_PATH_SUPPORT
import com.example.onlineshop.util.Resource
import com.example.onlineshop.util.validatinon.MessageValidation
import com.example.onlineshop.util.validatinon.MessageValidationFailedState
import com.example.onlineshop.util.validatinon.validateEmailInMessageSupport
import com.example.onlineshop.util.validatinon.validateMessInMessageSupport
import com.example.onlineshop.util.validatinon.validateNameInMessageSupport
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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
        viewModelScope.launch{_sendMessageState.emit(Resource.Loading())}
        if (validateMessage(message)){
            firestore.collection(COLLECTION_PATH_SUPPORT)
                .document()
                .set(message)
                .addOnSuccessListener {
                    viewModelScope.launch{_sendMessageState.emit(Resource.Success(Unit))}
                }
                .addOnFailureListener {
                    viewModelScope.launch{_sendMessageState.emit(Resource.Error(it.message.toString()))}
                }
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
        val validateName = validateNameInMessageSupport(mess.name.trim())
        val validateEmail = validateEmailInMessageSupport(mess.email.trim())
        val validateMessage = validateMessInMessageSupport(mess.message.trim())

        val shouldValidation = validateName is MessageValidation.Success
                && validateEmail is MessageValidation.Success
                && validateMessage is MessageValidation.Success

        return shouldValidation
    }

}