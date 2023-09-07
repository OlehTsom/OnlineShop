package com.example.onlineshop.fragments.categories.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshop.R
import com.example.onlineshop.adapters.BestProductsAdapter
import com.example.onlineshop.databinding.FragmentSearchCategoryBaseBinding
import com.example.onlineshop.helper.hideBottomNavigation
import com.example.onlineshop.util.Constants.BUNDLE_KEY_PRODUCT
import com.example.onlineshop.util.Resource
import com.example.onlineshop.viewmodel.CategoryViewModel
import com.example.onlineshop.viewmodel.PagingInfoBestProductForBaseCategory
import com.example.onlineshop.viewmodel.PagingInfoOfferProductsForCategory
import com.example.onlineshop.viewmodel.providerFactory.VMBaseCategoryProviderFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SearchCategoryBase : Fragment(), KodeinAware {
    lateinit var binding: FragmentSearchCategoryBaseBinding
    private val args by navArgs<SearchCategoryBaseArgs>()
    val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override val kodein by kodein()
    val firestore: FirebaseFirestore by instance()

    val viewModel by viewModels<CategoryViewModel> {
        VMBaseCategoryProviderFactory(firestore, args.category.category)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigation()
        binding = FragmentSearchCategoryBaseBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setUpAdapters()



        offerAdapter.onClick = { product ->
            val bundle = Bundle().apply { putParcelable(BUNDLE_KEY_PRODUCT, product) }
            findNavController().navigate(
                R.id.action_searchCategoryBase_to_productsDetailsFragment,
                bundle
            )
        }

        bestProductsAdapter.onClick = { product ->
            val bundle = Bundle().apply { putParcelable(BUNDLE_KEY_PRODUCT, product) }
            findNavController().navigate(
                R.id.action_searchCategoryBase_to_productsDetailsFragment,
                bundle
            )
        }

        binding.nameCategory.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvNameCategoriesSearch.text = args.category.nameCategory

        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        if (viewModel.pagingInfoOfferProductsForCategory.offerProductPage > 1)
                            showProgressOffer()
                        Log.d("OfferOk", "Loading")
                    }

                    is Resource.Success -> {
                        binding.shimmerVertical.stopShimmer()
                        binding.shimmerVertical.visibility = View.GONE

                        binding.shimmerHorizontal.stopShimmer()
                        binding.shimmerHorizontal.visibility = View.GONE
                        hideProgressOffer()
                        Log.d("CategorySearchBase", "Ok")
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

        binding.rvOfferSearchCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollHorizontally(1) && dx != 0) {
                     onOfferPagingRequest()
                }
            }
        })

        binding.nestedScrollSearchCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { view, _, scrollY, _, _ ->
            if (view.getChildAt(0).bottom <= view.height + scrollY) {
                  onBestProductsPagingRequest()
            }

        })

        binding.swipeRefreshSearchCategory.setOnRefreshListener {
            refreshScreen()
        }

    }

    fun onBestProductsPagingRequest() {
        viewModel.fetchBestProducts()
    }

    fun onOfferPagingRequest() {
        viewModel.fetchOfferProducts()
    }

    fun refreshScreen() {
        clearPagingDataWhenUpdateScreen(viewModel)
        viewModel.fetchBestProducts()
        viewModel.fetchOfferProducts()
    }

    private fun setUpAdapters() {
        binding.rvOfferSearchCategory.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL, false
            )
            adapter = offerAdapter
        }

        binding.rvBestProductsSearchCat.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    fun clearPagingDataWhenUpdateScreen(viewModel: CategoryViewModel) {
        viewModel.updatePagingInfoOfferProducts(PagingInfoOfferProductsForCategory())
        viewModel.updatePagingInfoBestProducts(PagingInfoBestProductForBaseCategory())
        binding.rvOfferSearchCategory.smoothScrollToPosition(0)
        binding.rvBestProductsSearchCat.smoothScrollToPosition(0)
    }

    fun setSwipeRefreshLayoutEnabled(enabled: Boolean) {
        binding.swipeRefreshSearchCategory.isRefreshing = enabled
    }

    fun showProgressOffer() {
        binding.offerProductsProgressBarSearchCat.visibility = View.VISIBLE
    }

    fun showProgressBestProducts() {
        binding.bestProductsProgressBarSearch.visibility = View.VISIBLE
    }

    fun hideProgressOffer() {
        binding.offerProductsProgressBarSearchCat.visibility = View.GONE
    }

    fun hideProgressBestProducts() {
        binding.bestProductsProgressBarSearch.visibility = View.GONE
    }




    override fun onResume() {
        super.onResume()
        if (offerAdapter.differ.currentList.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch{
                delay(600)
                withContext(Dispatchers.Main){
                    viewModel.fetchBestProducts()
                    viewModel.fetchOfferProducts()
                    viewModel.updatePagingInfoOfferProducts(PagingInfoOfferProductsForCategory())
                    viewModel.updatePagingInfoBestProducts(PagingInfoBestProductForBaseCategory())
                }
            }

        }
    }
}