package com.example.onlineshop.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshop.R
import com.example.onlineshop.adapters.BestDealsProductsAdapter
import com.example.onlineshop.adapters.BestProductsAdapter
import com.example.onlineshop.adapters.SpecialProductsAdapter
import com.example.onlineshop.databinding.FragmentMainCategoryBinding
import com.example.onlineshop.helper.showBottomNavigation
import com.example.onlineshop.util.Resource
import com.example.onlineshop.viewmodel.MainCategoryViewModel
import com.example.onlineshop.viewmodel.PagingInfoBestDeals
import com.example.onlineshop.viewmodel.PagingInfoBestProducts
import com.example.onlineshop.viewmodel.PagingInfoSpecialProducts
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

private val TAG = "MainCategoryFragment"
class MainCategoryFragment : Fragment(), KodeinAware {
    private lateinit var binding : FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsProductsAdapter: BestDealsProductsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    override val kodein by kodein()

    private val viewModel : MainCategoryViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("SearchFragment","In InCreateView")
        binding = FragmentMainCategoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSpecialProductRv()
        setUpBestDealsProductRv()
        setUpBestProductRv()

        specialProductsAdapter.onClick = {product ->
            val bundle = Bundle().apply { putParcelable("product",product)}
            findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment,bundle)
        }

        bestDealsProductsAdapter.onClick = {product ->
            val bundle = Bundle().apply { putParcelable("product",product)}
            findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment,bundle)
        }

        bestProductsAdapter.onClick = {product ->
            val bundle = Bundle().apply { putParcelable("product",product)}
            findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment,bundle)
        }


        lifecycleScope.launchWhenCreated {
            viewModel.specialProduct.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            showLoading()
                            if (viewModel.getPagingInfoSpecialProducts().specialProductPage > 1)
                                binding.bestSpecialProgressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.bestSpecialProgressBar.visibility = View.GONE
                            specialProductsAdapter.differ.submitList(it.data)
                            hideLoading()
                        }

                        is Resource.Error -> {
                            hideLoading()
                            binding.bestSpecialProgressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> Unit
                    }
                }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.bestDealsProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            if (viewModel.getPagingInfoBestDeals().dealsProductPage > 1)
                                binding.bestDealsProgressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.bestDealsProgressBar.visibility = View.GONE
                            bestDealsProductsAdapter.differ.submitList(it.data)

                        }

                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.bestDealsProgressBar.visibility = View.GONE

                        }

                        else -> Unit
                    }
                }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.bestProduct.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.bestProductsProgressBar.visibility = View.VISIBLE
                            Log.d(TAG, "Loading")
                        }

                        is Resource.Success -> {
                            bestProductsAdapter.differ.submitList(it.data)
                            binding.bestProductsProgressBar.visibility = View.GONE
                            binding.swipeRefreshLayout.isRefreshing = false
                            Log.d(TAG, "Success")

                        }

                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.bestProductsProgressBar.visibility = View.GONE
                            Log.d(TAG, "Error")

                        }

                        else -> Unit
                    }
                }
        }

        onScrollBestProductsToBottom()
        onScrollBestDeals()
        onScrollSpecialProducts()




        binding.swipeRefreshLayout.setOnRefreshListener {
            swipeToRefresh()
        }

    }

    private fun swipeToRefresh(){
        clearPagingDataWhenUpdateScreen()
        viewModel.fetchBestProducts()
        viewModel.fetchBestDealsProducts()
        viewModel.fetchSpecialProducts()
        binding.rvBestDealsProducts.smoothScrollToPosition(0)
        binding.rvSpecialProducts.smoothScrollToPosition(0)
    }

    private fun onScrollBestProductsToBottom(){
        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.
        OnScrollChangeListener{view,_,scrollY,_,_ ->
            if(view.getChildAt(0).bottom <= view.height + scrollY){
                viewModel.fetchBestProducts()
            }
        })
    }

    private fun onScrollBestDeals(){
        binding.rvBestDealsProducts.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val contentWidth = recyclerView.computeHorizontalScrollRange()
                val currentScroll = recyclerView.computeHorizontalScrollOffset() + recyclerView.width

                if (currentScroll >= contentWidth) {
                    viewModel.fetchBestDealsProducts()
                }

            }
        })
    }

    private fun onScrollSpecialProducts(){
        binding.rvSpecialProducts.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val contentWidth = recyclerView.computeHorizontalScrollRange()
                val currentScroll = recyclerView.computeHorizontalScrollOffset() + recyclerView.width
                if (currentScroll >= contentWidth){
                    viewModel.fetchSpecialProducts()
                }
            }
        })
    }

    private fun clearPagingDataWhenUpdateScreen(){
        viewModel.updatePagingInfoBestDeals(PagingInfoBestDeals())
        viewModel.updatePagingInfoBestProducts(PagingInfoBestProducts())
        viewModel.updatePagingInfoSpecialProducts(PagingInfoSpecialProducts())
    }

    private fun hideLoading() {
        binding.progressbarMainCategory.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressbarMainCategory.visibility = View.VISIBLE
    }

    private fun setUpSpecialProductRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = specialProductsAdapter
        }
    }

    private fun setUpBestDealsProductRv(){
        bestDealsProductsAdapter = BestDealsProductsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = bestDealsProductsAdapter
        }
    }

    private fun setUpBestProductRv(){
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter = bestProductsAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }
}