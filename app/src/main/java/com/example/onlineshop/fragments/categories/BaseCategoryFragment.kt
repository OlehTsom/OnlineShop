package com.example.onlineshop.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.onlineshop.R
import com.example.onlineshop.adapters.BestProductsAdapter
import com.example.onlineshop.databinding.FragmentBaseCategoryBinding
import com.example.onlineshop.helper.showBottomNavigation
import com.example.onlineshop.util.Constants.BUNDLE_KEY_PRODUCT
import com.example.onlineshop.viewmodel.CategoryViewModel
import com.example.onlineshop.viewmodel.PagingInfoBestDeals
import com.example.onlineshop.viewmodel.PagingInfoBestProductForBaseCategory
import com.example.onlineshop.viewmodel.PagingInfoBestProducts
import com.example.onlineshop.viewmodel.PagingInfoOfferProductsForCategory
import com.example.onlineshop.viewmodel.PagingInfoSpecialProducts

open class BaseCategoryFragment : Fragment() {
    private lateinit var binding : FragmentBaseCategoryBinding
    protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapters()

        offerAdapter.onClick = {product ->
            val bundle = Bundle().apply { putParcelable(BUNDLE_KEY_PRODUCT,product)}
            findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment,bundle)
        }

        bestProductsAdapter.onClick = {product ->
            val bundle = Bundle().apply { putParcelable(BUNDLE_KEY_PRODUCT,product)}
            findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment,bundle)
        }

        binding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollHorizontally(1) && dx != 0){
                    onOfferPagingRequest()
                }
            }
        })

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{view,_,scrollY,_,_ ->
            if (view.getChildAt(0).bottom <= view.height + scrollY){
                onBestProductsPagingRequest()
            }

        })

        binding.swipeRefreshBaseCategory.setOnRefreshListener{
            refreshScreen()
        }


    }

    open fun onOfferPagingRequest(){

    }

    open fun onBestProductsPagingRequest(){

    }

    fun showProgressOffer(){
        binding.offerProductsProgressBar.visibility = View.VISIBLE
    }

    fun showProgressBestProducts(){
        binding.bestProductsProgressBar.visibility = View.VISIBLE
    }

    fun hideProgressOffer(){
        binding.offerProductsProgressBar.visibility = View.GONE
    }

    fun hideProgressBestProducts(){
        binding.bestProductsProgressBar.visibility = View.GONE
    }

    open fun refreshScreen(){

    }

    fun clearPagingDataWhenUpdateScreen(viewModel : CategoryViewModel){
        viewModel.updatePagingInfoOfferProducts(PagingInfoOfferProductsForCategory())
        viewModel.updatePagingInfoBestProducts(PagingInfoBestProductForBaseCategory())
        binding.rvOffer.smoothScrollToPosition(0)
        binding.rvBestProducts.smoothScrollToPosition(0)
    }

    private fun setUpAdapters(){
        binding.rvOffer.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = offerAdapter
        }

        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter = bestProductsAdapter
        }
    }

    fun setSwipeRefreshLayoutEnabled(enabled: Boolean) {
        binding.swipeRefreshBaseCategory.isRefreshing = enabled
    }

    protected fun resumeFragment(viewModel: CategoryViewModel) {
        // Логіка ініціалізації та завантаження даних
        viewModel.fetchBestProducts()
        viewModel.fetchOfferProducts()
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }

}
