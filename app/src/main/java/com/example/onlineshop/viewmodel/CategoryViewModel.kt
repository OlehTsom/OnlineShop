package com.example.onlineshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.Category
import com.example.onlineshop.data.Product
import com.example.onlineshop.fragments.categories.search.Filtered
import com.example.onlineshop.util.Constants.COLLECTION_PATH_PRODUCTS
import com.example.onlineshop.util.Constants.FIELD_PATH_CATEGORY
import com.example.onlineshop.util.Constants.FIELD_PATH_NAME
import com.example.onlineshop.util.Constants.FIELD_PATH_OFFER_PERCENTAGE
import com.example.onlineshop.util.Constants.FIELD_PATH_PRICE
import com.example.onlineshop.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
            viewModelScope.launch {
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
                    viewModelScope.launch {
                        Log.d("BaseCategoryViewModelSuccess","Success Offer")
                        _offerProducts.emit(Resource.Success(product))
                    }
                    pagingInfoOfferProductsForCategory.offerProductPage++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _offerProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestProducts(filtered: Filtered? = null) {
        if (!pagingInfoBestProductsForCategory.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }

            var query = firestore.collection(COLLECTION_PATH_PRODUCTS)
                .whereEqualTo(FIELD_PATH_CATEGORY, category)
                .whereEqualTo(FIELD_PATH_OFFER_PERCENTAGE, null)
                .limit(pagingInfoBestProductsForCategory.bestProductPage * 10)

            if (filtered != null) {
                query = when(filtered) {
                    Filtered.FROM_LOWEST_PRICE_TO_HIGHEST -> query.orderBy(FIELD_PATH_PRICE)
                    Filtered.FROM_HIGHEST_PRICE_TO_LOWEST -> query.orderBy(FIELD_PATH_PRICE,Query.Direction.DESCENDING)
                    Filtered.BY_NAME -> query.orderBy(FIELD_PATH_NAME)
                }
            }

            query.get()
                .addOnSuccessListener {
                    val products = it.toObjects(Product::class.java)
                    pagingInfoBestProductsForCategory.isPagingEnd =
                        products == pagingInfoBestProductsForCategory.oldBestProducts
                    pagingInfoBestProductsForCategory.oldBestProducts = products
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(products))
                    }
                    pagingInfoBestProductsForCategory.bestProductPage++
                }
                .addOnFailureListener { exception ->
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(exception.message.toString()))
                        Log.d("BaseCategoryViewModelSuccess", exception.message.toString())
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
