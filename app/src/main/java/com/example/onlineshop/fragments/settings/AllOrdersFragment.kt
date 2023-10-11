package com.example.onlineshop.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.R
import com.example.onlineshop.adapters.AllOrdersAdapter
import com.example.onlineshop.databinding.FragmentOrdersBinding
import com.example.onlineshop.helper.customSnackbarForError
import com.example.onlineshop.helper.hideBottomNavigation
import com.example.onlineshop.util.Resource
import com.example.onlineshop.util.VerticalItemDecoration
import com.example.onlineshop.viewmodel.AllOrdersViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class AllOrdersFragment : Fragment(), KodeinAware {
    private lateinit var binding: FragmentOrdersBinding
    private val allOrdersAdapter by lazy { AllOrdersAdapter() }

    override val kodein by kodein()
    private val viewModel: AllOrdersViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOrdersAdapter()

        lifecycleScope.launchWhenStarted {
            viewModel.allOrders.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        allOrdersAdapter.differ.submitList(it.data)
                        if (it.data.isNullOrEmpty()) {
                            binding.tvEmptyOrders.visibility = View.VISIBLE
                        }
                    }

                    is Resource.Error -> {
                        customSnackbarForError(
                            it.message.toString(),
                            R.dimen.snackbar_margin_bottom_details
                        )
                        binding.progressbarAllOrders.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
        }

        allOrdersAdapter.onClick = {
            val action = AllOrdersFragmentDirections.actionOrdersFragmentToOrderDetailFragment(it)
            findNavController().navigate(action)
        }

        binding.imageCloseOrders.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun setUpOrdersAdapter() {
        binding.rvAllOrders.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = allOrdersAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
    }

}