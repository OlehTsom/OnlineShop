package com.example.onlineshop.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.onlineshop.R
import com.example.onlineshop.data.Category
import com.example.onlineshop.data.Product
import com.example.onlineshop.util.Resource
import com.example.onlineshop.viewmodel.CategoryViewModel
import com.example.onlineshop.viewmodel.PagingInfoBestProductForBaseCategory
import com.example.onlineshop.viewmodel.PagingInfoOfferProductsForCategory
import com.example.onlineshop.viewmodel.providerFactory.VMBaseCategoryProviderFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class JacketsFragment : BaseCategoryFragment(), KodeinAware {
    override val kodein by kodein()
    private val firestore: FirebaseFirestore by instance()

    private val viewModel by viewModels<CategoryViewModel> {
        VMBaseCategoryProviderFactory(firestore, Category.Jackets.category)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        if (viewModel.pagingInfoOfferProductsForCategory.offerProductPage > 1)
                            showProgressOffer()
                        Log.d("OfferOk", "Loading")
                    }

                    is Resource.Success -> {
                        hideProgressOffer()
                        offerAdapter.differ.submitList(it.data)
                        setSwipeRefreshLayoutEnabled(false)
                    }

                    is Resource.Error -> {
                        hideProgressOffer()
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                        setSwipeRefreshLayoutEnabled(false)
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showProgressBestProducts()
                    }

                    is Resource.Success -> {
                        if (it.data.isNullOrEmpty()) {
                            ifCategoryIsEmpty()
                        }
                        hideProgressBestProducts()
                        bestProductsAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        hideProgressBestProducts()
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }

                    else -> Unit
                }
            }
        }
    }

    override fun onBestProductsPagingRequest() {
        viewModel.fetchBestProducts()
    }

    override fun onOfferPagingRequest() {
        viewModel.fetchOfferProducts()
    }

    override fun refreshScreen() {
        clearPagingDataWhenUpdateScreen(viewModel)
        viewModel.fetchBestProducts()
        viewModel.fetchOfferProducts()
    }

    override fun onResume() {
        super.onResume()
        if (offerAdapter.differ.currentList.isEmpty()) {
            viewModel.fetchBestProducts()
            viewModel.fetchOfferProducts()
            viewModel.updatePagingInfoOfferProducts(PagingInfoOfferProductsForCategory())
            viewModel.updatePagingInfoBestProducts(PagingInfoBestProductForBaseCategory())
        }
    }

}
