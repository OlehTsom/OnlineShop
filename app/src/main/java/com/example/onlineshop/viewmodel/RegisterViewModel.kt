package com.example.onlineshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.User
import com.example.onlineshop.util.Constants.USER_COLLECTION
import com.example.onlineshop.util.validatinon.RegisterFailedState
import com.example.onlineshop.util.validatinon.RegisterValidation
import com.example.onlineshop.util.Resource
import com.example.onlineshop.util.validatinon.validateEmail
import com.example.onlineshop.util.validatinon.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class RegisterViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFailedState>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid,user)
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch{
                        _register.emit(Resource.Error(it.message.toString()))
                    }
                }
        } else {
            val registerFailedState = RegisterFailedState(
                validateEmail(user.email),
                validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFailedState)
            }
        }
    }

    fun saveUserInfo(userUid: String,user: User) {
        val userDocRef = db.collection(USER_COLLECTION).document(userUid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userDocRef)
            if (!snapshot.exists()) {
                // Документ не існує, тому ми можемо його створити
                transaction.set(userDocRef, user)
            } else {
                Log.d("RegisterViewModel","No created")
            }
        }
            .addOnSuccessListener {
                viewModelScope.launch{
                    _register.emit(Resource.Success(user))
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch { _register.emit(Resource.Error(exception.message.toString()))  }
            }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success

        return shouldRegister
    }

}