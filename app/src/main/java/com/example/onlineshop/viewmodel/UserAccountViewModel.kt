package com.example.onlineshop.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.ShopApp
import com.example.onlineshop.data.User
import com.example.onlineshop.util.Constants.COLLECTION_PATH_USER
import com.example.onlineshop.util.validatinon.RegisterValidation
import com.example.onlineshop.util.Resource
import com.example.onlineshop.util.validatinon.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.UUID

class UserAccountViewModel(
    private val firestore: FirebaseFirestore,
    private val aunt: FirebaseAuth,
    private val storage: StorageReference,
    app : Application
) : AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    private val _resetPassword = MutableSharedFlow<Resource<Unit>>()
    val resetPassword = _resetPassword.asSharedFlow()

    init {
        getUser()
    }

    private fun getUser(){
        viewModelScope.launch { _user.emit(Resource.Loading())}
        firestore.collection(COLLECTION_PATH_USER).document(aunt.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        _user.emit(Resource.Success(user))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    viewModelScope.launch { _user.emit(Resource.Error(it.message.toString())) }
                }
            }
    }

    fun updateUserInfo(user: User,imageUri : Uri?){
        val areInputValidation = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()
        if (!areInputValidation){
            viewModelScope.launch {
                viewModelScope.launch { _updateInfo.emit(Resource.Error("Check your inputs"))}
            }
            return
        }

        viewModelScope.launch { _updateInfo.emit(Resource.Loading())}

        if (imageUri == null){
            saveUserInformation(user,true)
        }else{
            saveUserInformationWithNewImage(user,imageUri)
        }

    }

    private fun saveUserInformationWithNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    getApplication<ShopApp>().contentResolver,
                    imageUri
                )
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)

                val imageByteArray = byteArrayOutputStream.toByteArray()
                val imageDirectory =
                    storage.child("profileImages/${aunt.uid!!}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()

                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = imageUrl),false)
            } catch (e: Exception) {
                viewModelScope.launch {
                    viewModelScope.launch { _updateInfo.emit(Resource.Error(e.message.toString())) }
                }
            }
        }
    }

    private fun saveUserInformation(user: User, shouldRetrievedOldImage: Boolean) {
        firestore.runTransaction{transaction ->
            val documentRef = firestore.collection(COLLECTION_PATH_USER).document(aunt.uid!!)
            if (shouldRetrievedOldImage){
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                transaction.set(documentRef,newUser)
            }else{
                transaction.set(documentRef,user)
            }

        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Success(user))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                viewModelScope.launch { _updateInfo.emit(Resource.Error(it.message.toString())) }
            }
        }
    }

    fun resetUserPassword(email : String){
        viewModelScope.launch { _resetPassword.emit(Resource.Loading()) }

        aunt.sendPasswordResetEmail(email.trim())
            .addOnSuccessListener {
                viewModelScope.launch { _resetPassword.emit(Resource.Success(Unit)) }
            }
            .addOnFailureListener {
                viewModelScope.launch { _resetPassword.emit(Resource.Error(it.message.toString())) }
            }
    }

}