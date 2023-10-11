package com.example.onlineshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.CartProduct
import com.example.onlineshop.firebase.FirebaseCommon
import com.example.onlineshop.util.Constants.COLLECTION_PATH_CART
import com.example.onlineshop.util.Constants.COLLECTION_PATH_USER
import com.example.onlineshop.util.Constants.FIELD_PATH_PRODUCT_ID
import com.example.onlineshop.util.Constants.FIELD_PATH_SELECT_COLOR
import com.example.onlineshop.util.Constants.FIELD_PATH_SELECT_SIZE
import com.example.onlineshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon : FirebaseCommon
) : ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    private val _amountCheck = MutableSharedFlow<Resource<Unit>>()
    val amountCheck = _amountCheck.asSharedFlow()


    fun addUpdateProductInCart(cartProduct: CartProduct) {
        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())

            val userCartRef = firestore.collection(COLLECTION_PATH_USER).document(auth.uid!!).collection(COLLECTION_PATH_CART)
            val query = userCartRef.whereEqualTo(FIELD_PATH_PRODUCT_ID, cartProduct.product.id)
                .whereEqualTo(FIELD_PATH_SELECT_COLOR, cartProduct.selectedColor)
                .whereEqualTo(FIELD_PATH_SELECT_SIZE, cartProduct.selectedSize)

            query.get().addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    addNewProduct(cartProduct)
                } else {
                    val documentSnapshot = querySnapshot.documents.first()
                    val existingCartProduct = documentSnapshot.toObject(CartProduct::class.java)
                    if (existingCartProduct != null && existingCartProduct.amount < cartProduct.product.amountProduct) {
                        increaseAmount(documentSnapshot.id, existingCartProduct)
                        Log.d("DetailsViewModel","${existingCartProduct.amount}")
                    } else if(!(cartProduct.amount >= cartProduct.product.amountProduct)) {
                        amountIsMax()
                    } else{
                        addNewProduct(cartProduct)
                    }
                }
            }.addOnFailureListener { exception ->
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun amountIsMax() {
        viewModelScope.launch {
            _amountCheck.emit(Resource.Success(Unit))
        }
    }


    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { addedProduct, error ->
            viewModelScope.launch {
                if (error == null) {
                    _addToCart.emit(Resource.Success(addedProduct!!))
                } else {
                    _addToCart.emit(Resource.Error(error.message.toString()))
                }
            }
        }
    }

    private fun increaseAmount(documentId: String, cardProduct: CartProduct) {
        firebaseCommon.increaseAmount(documentId) { _, error ->
            viewModelScope.launch {
                if (error == null) {
                    _addToCart.emit(Resource.Success(cardProduct))
                } else {
                    _addToCart.emit(Resource.Error(error.message.toString()))
                }
            }
        }

    }
}
