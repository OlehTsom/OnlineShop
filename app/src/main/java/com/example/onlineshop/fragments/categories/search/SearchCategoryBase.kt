package com.example.onlineshop.fragments.categories.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
    private var positionSpinner = 0
    private val args by navArgs<SearchCategoryBaseArgs>()
    private val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    private val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override val kodein by kodein()
    private val firestore: FirebaseFirestore by instance()

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
                            binding.offerProductsProgressBarSearchCat.showView()
                        Log.d("OfferOk", "Loading")
                    }

                    is Resource.Success -> {
                        stopShimmer()
                        Log.d("CategorySearchBase", "Ok")
                        offerAdapter.differ.submitList(it.data)
                        setSwipeRefreshLayoutEnabled(false)
                    }

                    is Resource.Error -> {
                        binding.offerProductsProgressBarSearchCat.hideView()
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
                        binding.bestProductsProgressBarSearch.showView()
                    }

                    is Resource.Success -> {
                        if (it.data.isNullOrEmpty()) {
                            ifCategoryEmpty()
                        }
                        binding.bestProductsProgressBarSearch.hideView()
                        bestProductsAdapter.differ.submitList(it.data)
                        createItemSelectedListener()
                    }

                    is Resource.Error -> {
                        binding.bestProductsProgressBarSearch.hideView()
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
            setFilterByDefault()
        }

    }

    private fun stopShimmer() {
        binding.shimmerVertical.stopShimmer()
        binding.shimmerVertical.visibility = View.GONE
        binding.shimmerHorizontal.stopShimmer()
        binding.shimmerHorizontal.visibility = View.GONE
        binding.offerProductsProgressBarSearchCat.hideView()
    }

    private fun ifCategoryEmpty() {
        binding.lv.playAnimation()
        binding.swipeRefreshSearchCategory.visibility = View.INVISIBLE
        binding.lv.visibility = View.VISIBLE
    }

    private fun onBestProductsPagingRequest() {
        when(positionSpinner){
            SortingOptions.DEFAULT -> viewModel.fetchBestProducts()
            SortingOptions.HIGHEST_TO_LOWEST -> viewModel.fetchBestProducts(Filtered.FROM_HIGHEST_PRICE_TO_LOWEST)
            SortingOptions.LOWEST_TO_HIGHEST -> viewModel.fetchBestProducts(Filtered.FROM_LOWEST_PRICE_TO_HIGHEST)
            SortingOptions.BY_NAME -> viewModel.fetchBestProducts(Filtered.BY_NAME)
        }
    }

    private fun onOfferPagingRequest() {
        viewModel.fetchOfferProducts()
    }

    private fun refreshScreen() {
        clearPagingDataWhenUpdateScreen(viewModel)
        viewModel.fetchBestProducts()
        viewModel.fetchOfferProducts()
    }

    private fun setFilterByDefault(){
        binding.spSorted.setSelection(0)
    }

    private fun createItemSelectedListener(){
        binding.apply {
            spSorted.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, popsition: Int, id: Long) {
                    positionSpinner = when(popsition){
                        SortingOptions.DEFAULT -> {
                            clearPagingDataWhenUpdateScreen(viewModel)
                            viewModel.fetchBestProducts()
                            SortingOptions.DEFAULT
                        }
                        SortingOptions.HIGHEST_TO_LOWEST -> {
                            clearPagingDataWhenUpdateScreen(viewModel)
                            viewModel.fetchBestProducts(Filtered.FROM_HIGHEST_PRICE_TO_LOWEST)
                            SortingOptions.HIGHEST_TO_LOWEST
                        }
                        SortingOptions.LOWEST_TO_HIGHEST -> {
                            clearPagingDataWhenUpdateScreen(viewModel)
                            viewModel.fetchBestProducts(Filtered.FROM_LOWEST_PRICE_TO_HIGHEST)
                            SortingOptions.LOWEST_TO_HIGHEST
                        }
                        SortingOptions.BY_NAME -> {
                            clearPagingDataWhenUpdateScreen(viewModel)
                            viewModel.fetchBestProducts(Filtered.BY_NAME)
                            SortingOptions.BY_NAME
                        }
                        else -> {
                            SortingOptions.DEFAULT
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //TODO("Not yet implemented")
                }

            }
        }
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

    private fun clearPagingDataWhenUpdateScreen(viewModel: CategoryViewModel) {
        viewModel.updatePagingInfoOfferProducts(PagingInfoOfferProductsForCategory())
        viewModel.updatePagingInfoBestProducts(PagingInfoBestProductForBaseCategory())
        binding.rvOfferSearchCategory.smoothScrollToPosition(0)
        binding.rvBestProductsSearchCat.smoothScrollToPosition(0)
    }

    private fun setSwipeRefreshLayoutEnabled(enabled: Boolean) {
        binding.swipeRefreshSearchCategory.isRefreshing = enabled
    }

    private fun View.hideView() {
        visibility = View.GONE
    }

    private fun View.showView() {
        visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if (offerAdapter.differ.currentList.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(600)
                withContext(Dispatchers.Main) {
                    viewModel.fetchBestProducts()
                    viewModel.fetchOfferProducts()
                    viewModel.updatePagingInfoOfferProducts(PagingInfoOfferProductsForCategory())
                    viewModel.updatePagingInfoBestProducts(PagingInfoBestProductForBaseCategory())
                }
            }

        }
    }
}

enum class Filtered{
    FROM_LOWEST_PRICE_TO_HIGHEST,
    FROM_HIGHEST_PRICE_TO_LOWEST,
    BY_NAME
}

object SortingOptions {
    const val DEFAULT = 0
    const val HIGHEST_TO_LOWEST = 1
    const val LOWEST_TO_HIGHEST = 2
    const val BY_NAME = 3
}
