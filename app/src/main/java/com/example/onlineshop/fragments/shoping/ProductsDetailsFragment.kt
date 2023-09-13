package com.example.onlineshop.fragments.shoping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.R
import com.example.onlineshop.adapters.ColorsAdapter
import com.example.onlineshop.adapters.SizesAdapter
import com.example.onlineshop.adapters.ViewPager2ImagesAdapter
import com.example.onlineshop.data.CartProduct
import com.example.onlineshop.databinding.FragmentProductDetailsBinding
import com.example.onlineshop.helper.customSnackbarForComplete
import com.example.onlineshop.helper.customSnackbarForError
import com.example.onlineshop.helper.getProductPrice
import com.example.onlineshop.helper.hideBottomNavigation
import com.example.onlineshop.util.Resource
import com.example.onlineshop.viewmodel.DetailsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class ProductsDetailsFragment : Fragment(),KodeinAware {
    private val args by navArgs<ProductsDetailsFragmentArgs>()
    private lateinit var binding : FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2ImagesAdapter() }
    private val sizeAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor : Int ?= null
    private var selectedSize : String ?= null

    override val kodein by kodein()
    private val viewModel : DetailsViewModel by instance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigation()
        binding = FragmentProductDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setUpSizesRv()
        setUpColorsRv()
        setUpViewPager()

        sizeAdapter.onItemClick ={
            selectedSize = it
        }

        colorsAdapter.onItemClick ={
            selectedColor = it
        }

        binding.butAddToCart.setOnClickListener {
            if(selectedColor != null && selectedSize != null) {
                viewModel.addUpdateProductInCart(
                    CartProduct(
                        product,
                        1,
                        selectedColor,
                        selectedSize
                    )
                )
            }else{
                customSnackbarForError(getString(R.string.snackbar_select_color_and_size_text),R.dimen.snackbar_margin_bottom_details)
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.amountCheck.collectLatest {
                when(it){
                    is Resource.Success -> {
                        delay(1200)
                        binding.butAddToCart.revertAnimation()
                        customSnackbarForError(
                            getString(R.string.snackbar_max_amount_text_details_fragment),
                            R.dimen.snackbar_margin_bottom_details)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.butAddToCart.startAnimation()
                        binding.butAddToCart.background = resources.getDrawable(R.drawable.blue_background)
                    }
                    is Resource.Success -> {
                        binding.butAddToCart.revertAnimation()
                        binding.butAddToCart.background = resources.getDrawable(R.color.black)
                        customSnackbarForComplete(
                            getString(R.string.snackbar_success_add_product_to_car_details_fragment),
                            R.dimen.snackbar_margin_bottom_details)
                    }
                    is Resource.Error ->{
                        binding.butAddToCart.revertAnimation()
                        Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }


        binding.imageCloseBut.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            tvProductNameDetails.text = product.name
            tvProductPrice.text = "$ " + product.offerPercentage.getProductPrice(product.price)
            tvProductDescription.text = product.description

            if (product.sizes.isNullOrEmpty())
                tvProductSizes.visibility = View.INVISIBLE

            if (product.colors.isNullOrEmpty())
                tvProductColors.visibility = View.INVISIBLE
        }

        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizeAdapter.differ.submitList(it) }
    }

    private fun setUpViewPager() {
        binding.apply {
            imageViewPager.adapter = viewPagerAdapter
            indicator.setViewPager(imageViewPager)
            viewPagerAdapter.registerAdapterDataObserver(indicator.adapterDataObserver)
        }
    }

    private fun setUpColorsRv() {
        binding.rvColors.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = colorsAdapter
        }
    }

    private fun setUpSizesRv() {
        binding.rvSizes.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = sizeAdapter
        }
    }




}