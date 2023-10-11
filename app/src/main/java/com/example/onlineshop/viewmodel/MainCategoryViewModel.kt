package com.example.onlineshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.Product
import com.example.onlineshop.util.Constants.COLLECTION_PATH_PRODUCTS
import com.example.onlineshop.util.Constants.FIELD_PATH_OFFER_PERCENTAGE
import com.example.onlineshop.util.Constants.FIELD_PATH_SPECIAL_PRODUCT
import com.example.onlineshop.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainCategoryViewModel(
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {
    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProduct = _specialProducts.asStateFlow()

    private val _bestDealsProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts = _bestDealsProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct = _bestProducts.asStateFlow()

    private var pagingInfoBestProducts = PagingInfoBestProducts()
    private var pagingInfoBestDeals = PagingInfoBestDeals()
    private var pagingInfoSpecialProducts = PagingInfoSpecialProducts()

    init {
        fetchSpecialProducts()
        fetchBestDealsProducts()
        fetchBestProducts()
    }

    fun fetchSpecialProducts(){
        if(!pagingInfoSpecialProducts.isPagingEnd) {
            viewModelScope.launch {
                Log.d("MainCategoryViewModelSuccess","Success Loading Special Product")
                _specialProducts.emit(Resource.Loading())
            }
            firebaseFirestore.collection(COLLECTION_PATH_PRODUCTS)
                .whereEqualTo(FIELD_PATH_SPECIAL_PRODUCT, "yes")
                .limit(pagingInfoSpecialProducts.specialProductPage * 10)
                .get()
                .addOnSuccessListener { result ->
                    val specialListProduct = result.toObjects(Product::class.java)
                    pagingInfoSpecialProducts.isPagingEnd =
                        specialListProduct == pagingInfoSpecialProducts.oldSpecialProducts
                    pagingInfoSpecialProducts.oldSpecialProducts = specialListProduct
                    viewModelScope.launch {
                        Log.d("MainCategoryViewModelSuccess","Success SpecialProducts")
                        _specialProducts.emit(Resource.Success(specialListProduct))
                    }
                    pagingInfoSpecialProducts.specialProductPage++
                }.addOnFailureListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        _specialProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestDealsProducts(){
        if(!pagingInfoBestDeals.isPagingEnd) {
           viewModelScope.launch {
               Log.d("MainCategoryViewModelSuccess","Success Loading Best Deals")

               _bestDealsProducts.emit(Resource.Loading())
            }
            firebaseFirestore.collection(COLLECTION_PATH_PRODUCTS)
                .whereNotEqualTo(FIELD_PATH_OFFER_PERCENTAGE, null)
                .limit(pagingInfoBestDeals.dealsProductPage * 10)
                .get()
                .addOnSuccessListener { result ->
                    val product = result.toObjects(Product::class.java)
                    pagingInfoBestDeals.isPagingEnd = pagingInfoBestDeals.oldDealsProducts == product
                    pagingInfoBestDeals.oldDealsProducts = product
                    viewModelScope.launch {
                        Log.d("MainCategoryViewModelSuccess","Success Best Deals")
                        _bestDealsProducts.emit(Resource.Success(product))
                    }
                    pagingInfoBestDeals.dealsProductPage++
                }.addOnFailureListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }


    fun fetchBestProducts(){
        if (!pagingInfoBestProducts.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
                Log.d("MainCategoryViewModelSuccess","Success Loading Best Product")
            }
            firebaseFirestore.collection("Products")
                .limit(pagingInfoBestProducts.bestProductPage * 10)
                .get()
                .addOnSuccessListener { result ->
                    val product = result.toObjects(Product::class.java)

                    pagingInfoBestProducts.isPagingEnd = product == pagingInfoBestProducts.oldBestProducts
                    pagingInfoBestProducts.oldBestProducts = product
                    viewModelScope.launch {
                        Log.d("MainCategoryViewModelSuccess","Success Best Products")
                        _bestProducts.emit(Resource.Success(product))
                    }
                    pagingInfoBestProducts.bestProductPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun updatePagingInfoBestProducts(newPagingInfo: PagingInfoBestProducts) {
        pagingInfoBestProducts = newPagingInfo
    }

    fun updatePagingInfoBestDeals(newPagingInfo: PagingInfoBestDeals) {
        pagingInfoBestDeals = newPagingInfo
    }

    fun updatePagingInfoSpecialProducts(newPagingInfo: PagingInfoSpecialProducts) {
        pagingInfoSpecialProducts = newPagingInfo
    }

    fun getPagingInfoBestDeals(): PagingInfoBestDeals {
        return pagingInfoBestDeals
    }

    fun getPagingInfoSpecialProducts(): PagingInfoSpecialProducts {
        return pagingInfoSpecialProducts
    }

}

data class PagingInfoBestProducts(
    var bestProductPage : Long = 1,
    var oldBestProducts : List<Product> = emptyList(),
    var isPagingEnd : Boolean = false,
)

data class PagingInfoBestDeals(
    var dealsProductPage : Long = 1,
    var oldDealsProducts : List<Product> = emptyList(),
    var isPagingEnd : Boolean = false,
)

data class PagingInfoSpecialProducts(
    var specialProductPage : Long = 1,
    var oldSpecialProducts : List<Product> = emptyList(),
    var isPagingEnd : Boolean = false,
)