package com.example.onlineshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.Category
import com.example.onlineshop.data.Product
import com.example.onlineshop.util.Constants.COLLECTION_PATH_PRODUCTS
import com.example.onlineshop.util.Constants.FIELD_PATH_CATEGORY
import com.example.onlineshop.util.Constants.FIELD_PATH_OFFER_PERCENTAGE
import com.example.onlineshop.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val firestore: FirebaseFirestore,
    private val category: String
) : ViewModel() {

    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    var pagingInfoOfferProductsForCategory = PagingInfoOfferProductsForCategory()
    private var pagingInfoBestProductsForCategory = PagingInfoBestProductForBaseCategory()


    fun fetchOfferProducts(){
        if (!pagingInfoOfferProductsForCategory.isPagingEnd) {
            CoroutineScope(Dispatchers.IO).launch {
                _offerProducts.emit(Resource.Loading())
            }
            firestore.collection(COLLECTION_PATH_PRODUCTS)
                .whereEqualTo(FIELD_PATH_CATEGORY, category)
                .whereNotEqualTo(FIELD_PATH_OFFER_PERCENTAGE, null)
                .limit(pagingInfoOfferProductsForCategory.offerProductPage * 10)
                .get()
                .addOnSuccessListener {
                    Log.d("SearchCategoryBase","Success")
                    val product = it.toObjects(Product::class.java)
                    pagingInfoOfferProductsForCategory.isPagingEnd =
                        product == pagingInfoOfferProductsForCategory.oldOfferProducts
                    pagingInfoOfferProductsForCategory.oldOfferProducts = product
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("BaseCategoryViewModelSuccess","Success Offer")
                        _offerProducts.emit(Resource.Success(product))
                    }
                    pagingInfoOfferProductsForCategory.offerProductPage++
                }
                .addOnFailureListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        _offerProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestProducts(){
        if (!pagingInfoBestProductsForCategory.isPagingEnd) {
            CoroutineScope(Dispatchers.IO).launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection(COLLECTION_PATH_PRODUCTS)
                .whereEqualTo(FIELD_PATH_CATEGORY, category)
                .whereEqualTo(FIELD_PATH_OFFER_PERCENTAGE, null)
                .limit(pagingInfoBestProductsForCategory.bestProductPage * 10)
                .get()
                .addOnSuccessListener {
                    val product = it.toObjects(Product::class.java)
                    pagingInfoBestProductsForCategory.isPagingEnd =
                        product == pagingInfoBestProductsForCategory.oldBestProducts
                    pagingInfoBestProductsForCategory.oldBestProducts = product
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("BaseCategoryViewModelSuccess","Success Best Products")
                        _bestProducts.emit(Resource.Success(product))
                    }
                    pagingInfoBestProductsForCategory.bestProductPage++
                }
                .addOnFailureListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun updatePagingInfoBestProducts(newPagingInfo: PagingInfoBestProductForBaseCategory) {
        pagingInfoBestProductsForCategory = newPagingInfo
    }

    fun updatePagingInfoOfferProducts(newPagingInfo: PagingInfoOfferProductsForCategory) {
        pagingInfoOfferProductsForCategory = newPagingInfo
    }

}

data class PagingInfoOfferProductsForCategory(
    var offerProductPage : Long = 1,
    var oldOfferProducts : List<Product> = emptyList(),
    var isPagingEnd : Boolean = false,
)

data class PagingInfoBestProductForBaseCategory(
    var bestProductPage : Long = 1,
    var oldBestProducts : List<Product> = emptyList(),
    var isPagingEnd : Boolean = false,
)
