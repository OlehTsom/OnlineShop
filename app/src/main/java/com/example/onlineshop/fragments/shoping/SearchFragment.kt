package com.example.onlineshop.fragments.shoping

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.onlineshop.R
import com.example.onlineshop.adapters.ProductAdapterAutoCompleteText
import com.example.onlineshop.adapters.SearchCategoriesAdapter
import com.example.onlineshop.data.Product
import com.example.onlineshop.databinding.BestDealsRvItemBinding
import com.example.onlineshop.databinding.FragmentSearchBinding
import com.example.onlineshop.helper.customSnackbarForError
import com.example.onlineshop.helper.showBottomNavigation
import com.example.onlineshop.util.Constants
import com.example.onlineshop.util.Constants.BUNDLE_KEY_CATEGORY
import com.example.onlineshop.util.Resource
import com.example.onlineshop.viewmodel.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class SearchFragment : Fragment(), KodeinAware, ProductClickListener {
    private var hasSearchResults = false
    private lateinit var binding: FragmentSearchBinding
    private val categoryAdapter by lazy { SearchCategoriesAdapter() }

    override val kodein by kodein()
    private val viewModel: SearchViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCategoryAdapter()

        val autoCompleteTextView = binding.edSearchBar
        val adapter = ProductAdapterAutoCompleteText(requireContext(), this)

        autoCompleteTextView.threshold = 1
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val product = adapter.getItem(position)
            product?.let {
                onProductClick(it)
            }
        }

        binding.edSearchBar.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null // Зберігаємо посилання на запущену задачу

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                val searchText = s.toString()

                if (isKeyboardActive(requireContext())) {
                    viewModel.searchProduct(searchText.toLowerCase().capitalize())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })



        lifecycleScope.launchWhenCreated {
            viewModel.searchProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAutoCompleteText.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        Log.d("SearchFragment", "Success")
                        it.data?.let { it1 -> adapter.updateData(it1) }
                        binding.progressbarAutoCompleteText.visibility = View.GONE
                        hasSearchResults =
                            it.data?.isNotEmpty() == true // Оновити значення залежно від наявності результатів


                        toggleTextViewVisibility(hasSearchResults)
                    }

                    is Resource.Error -> {
                        customSnackbarForError(
                            it.message.toString(),
                            R.dimen.snackbar_margin_bottom_details
                        )
                        binding.progressbarAutoCompleteText.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
        }


        categoryAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable(BUNDLE_KEY_CATEGORY, it) }
            findNavController().navigate(R.id.action_searchFragment_to_searchCategoryBase, bundle)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.categories.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.categoryProgressbar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        categoryAdapter.differ.submitList(it.data)
                        binding.categoryProgressbar.visibility = View.INVISIBLE
                    }

                    is Resource.Error -> {
                        Log.d("SearchFragment", it.message.toString())
                        binding.categoryProgressbar.visibility = View.INVISIBLE
                    }

                    else -> Unit
                }
            }
        }

    }

    private fun setCategoryAdapter() {
        binding.apply {
            rvCategorySearchFrag.layoutManager = GridLayoutManager(
                requireContext(),
                2, GridLayoutManager.VERTICAL, false
            )
            rvCategorySearchFrag.adapter = categoryAdapter
        }
    }

    private fun isKeyboardActive(context: Context): Boolean {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.isActive
    }


    private fun toggleTextViewVisibility(visible: Boolean) {
        if (visible) {
            binding.tvEmptyList.animate()
                .alpha(0f)
                .setDuration(500)
                .withEndAction {
                    binding.tvEmptyList.visibility = View.GONE
                }
        } else {
            binding.tvEmptyList.alpha = 0f
            binding.tvEmptyList.visibility = View.VISIBLE
            binding.tvEmptyList.animate()
                .alpha(1f)
                .setDuration(500)
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }

    override fun onProductClick(product: Product) {
        val bundle = Bundle().apply { putParcelable(Constants.BUNDLE_KEY_PRODUCT, product) }
        findNavController().navigate(R.id.action_searchFragment_to_productsDetailsFragment, bundle)
    }

}

interface ProductClickListener {
    fun onProductClick(product: Product)
}



