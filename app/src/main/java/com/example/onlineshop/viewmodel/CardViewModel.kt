package com.example.onlineshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.CartProduct
import com.example.onlineshop.firebase.FirebaseCommon
import com.example.onlineshop.helper.getProductPrice
import com.example.onlineshop.helper.getProductPriceFloat
import com.example.onlineshop.util.Constants.COLLECTION_PATH_CART
import com.example.onlineshop.util.Constants.COLLECTION_PATH_USER
import com.example.onlineshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CardViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel(){

    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    private val _amountCheck = MutableSharedFlow<Resource<Unit>>()
    val amountCheck = _amountCheck.asSharedFlow()

    private var cardProductDocuments = emptyList<DocumentSnapshot>()

    val productsPrice = cartProducts.map {
        when(it){
            is Resource.Success ->{
               String.format("%.1f",calculatePricer(it.data!!))
            }
            else -> null
        }
    }

    private val _deleteProductDialog = MutableSharedFlow<CartProduct>()
    val deleteroductDialog = _deleteProductDialog.asSharedFlow()

    private fun calculatePricer(data: List<CartProduct>): Float {
        return data.sumByDouble {cartProduct ->
            (cartProduct.product.offerPercentage.getProductPriceFloat(cartProduct.product.price) * cartProduct.amount).toDouble()
        }.toFloat()
    }

    init {
        getCartProducts()
    }


    fun deleteItemFromCart(cartProduct: CartProduct){
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cardProductDocuments[index].id
            firestore.collection(COLLECTION_PATH_USER).document(auth.uid!!).collection(COLLECTION_PATH_CART)
                .document(documentId).delete()
        }
    }

    private fun getCartProducts(){
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }
        firestore.collection(COLLECTION_PATH_USER).document(auth.uid!!).collection(COLLECTION_PATH_CART)
            .addSnapshotListener{value, error ->
                if(error != null || value == null){
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString()))  }
                }else{
                    cardProductDocuments = value.documents
                    val cardProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Success(cardProducts))
                    }
                }
            }
    }


    fun changeAmountProduct(
        cartProduct: CartProduct,
        amountChanging: FirebaseCommon.AmountChanging
    ){
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cardProductDocuments[index].id
            val newAmount = if (amountChanging == FirebaseCommon.AmountChanging.INCREASE) {
                cartProduct.amount + 1
            } else {
                cartProduct.amount - 1
            }


            if (newAmount > 0 && newAmount <= cartProduct.product.amountProduct && amountChanging == FirebaseCommon.AmountChanging.INCREASE) {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Loading())
                }
                increaseAmount(documentId)
            } else if (amountChanging == FirebaseCommon.AmountChanging.DECREASE) {
                if (cartProduct.amount == 1){
                    viewModelScope.launch {
                        _deleteProductDialog.emit(cartProduct)
                    }
                    return
                }
                viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                decreaseAmount(documentId)
            }else{
                viewModelScope.launch {
                    _amountCheck.emit(Resource.Success(Unit))
                }
            }
        }

    }


    private fun decreaseAmount(documentId: String) {
        firebaseCommon.decreaseAmount(documentId){result, error ->
            if(error != null){
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(error.message.toString()))
                }
            }
        }
    }

    private fun increaseAmount(documentId: String) {
        firebaseCommon.increaseAmount(documentId){result, error ->
            if(error != null){
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(error.message.toString()))
                }
            }
        }
    }


}