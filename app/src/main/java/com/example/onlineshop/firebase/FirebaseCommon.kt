package com.example.onlineshop.firebase

import com.example.onlineshop.data.CartProduct
import com.example.onlineshop.util.Constants.COLLECTION_PATH_CART
import com.example.onlineshop.util.Constants.COLLECTION_PATH_USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    private val cartCollection = firestore.collection(COLLECTION_PATH_USER)
        .document(firebaseAuth.uid!!).collection(COLLECTION_PATH_CART)

    fun addProductToCart(cartProduct: CartProduct, onResult :(CartProduct?,Exception?) -> Unit){
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct,null)
            }
            .addOnFailureListener {
                 onResult(null,it)
            }
    }

    fun increaseAmount(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.amount + 1
                if (newQuantity <= cartProduct.product.amountProduct) {
                    val newProductObject = cartProduct.copy(amount = newQuantity)
                    transition.set(documentRef, newProductObject)
                } else {
                    // Можна додати обробку помилки, якщо намагаєтесь перевищити наявну кількість
                    // Наприклад, throw Exception("Exceeded available quantity")
                }
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }


    fun decreaseAmount(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.amount - 1
                val newProductObject = cartProduct.copy(amount = newQuantity)
                transition.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }


    enum class AmountChanging{
        INCREASE,DECREASE
    }

}
