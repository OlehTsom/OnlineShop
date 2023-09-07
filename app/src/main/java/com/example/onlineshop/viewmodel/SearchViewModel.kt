package com.example.onlineshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.Product
import com.example.onlineshop.data.SearchCategory
import com.example.onlineshop.util.Constants.COLLECTION_PATH_PRODUCTS
import com.example.onlineshop.util.Constants.FIELD_PATH_CATEGORY
import com.example.onlineshop.util.Constants.FIELD_PATH_NAME
import com.example.onlineshop.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _categories =
        MutableStateFlow<Resource<List<SearchCategory>>>(Resource.Unspecified())
    val categories = _categories.asStateFlow()

    private val _searchProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val searchProducts = _searchProducts.asStateFlow()

    private var categoriesLoaded = false


    init {
            Log.d("SearchViewModel","init block")
            getAllCategories()
    }


    fun searchProduct(searchProductName: String) {
        viewModelScope.launch { _searchProducts.emit(Resource.Loading()) }
        firestore.collection(COLLECTION_PATH_PRODUCTS)
            .whereGreaterThanOrEqualTo(FIELD_PATH_NAME, searchProductName)
            .whereLessThan(FIELD_PATH_NAME, searchProductName + '\uf8ff')
            .get()
            .addOnSuccessListener { querySnapshot ->
                val product = querySnapshot.toObjects(Product::class.java)
                viewModelScope.launch {
                    _searchProducts.emit(Resource.Success(product))
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _searchProducts.emit(Resource.Error(exception.message.toString()))
                }
            }
    }


    private fun getAllCategories() {
        if (!categoriesLoaded) {
            viewModelScope.launch {
                _categories.emit(Resource.Loading())
            }
            firestore.collection(FIELD_PATH_CATEGORY)
                .get()
                .addOnSuccessListener { result ->
                    val category = result.toObjects(SearchCategory::class.java)
                    viewModelScope.launch {
                        _categories.emit(Resource.Success(category))
                        Log.d("SearchViewModelSuccess", "In Success")
                    }
                    if (category.isNotEmpty()) {
                        categoriesLoaded = true
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _categories.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}