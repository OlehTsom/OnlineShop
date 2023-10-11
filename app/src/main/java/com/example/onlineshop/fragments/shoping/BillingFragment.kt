package com.example.onlineshop.fragments.shoping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.R
import com.example.onlineshop.adapters.AddressAdapter
import com.example.onlineshop.adapters.BillingProductsAdapter
import com.example.onlineshop.data.Address
import com.example.onlineshop.data.CartProduct
import com.example.onlineshop.data.Order
import com.example.onlineshop.data.OrderStatus
import com.example.onlineshop.databinding.FragmentBillingBinding
import com.example.onlineshop.helper.customSnackbarForComplete
import com.example.onlineshop.helper.customSnackbarForError
import com.example.onlineshop.helper.hideBottomNavigation
import com.example.onlineshop.util.Constants.BUNDLE_KEY_ADDRESS
import com.example.onlineshop.util.HorizontalItemDecoration
import com.example.onlineshop.util.Resource
import com.example.onlineshop.viewmodel.BillingViewModel
import com.example.onlineshop.viewmodel.OrderViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class BillingFragment : Fragment(), KodeinAware {
    //ui
    private lateinit var binding: FragmentBillingBinding

    //adapters
    private val addressAdapters by lazy { AddressAdapter() }
    private val billingProductAdapter by lazy { BillingProductsAdapter() }

    //arguments
    private val args by navArgs<BillingFragmentArgs>()

    //data
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f
    private var selectedAddress: Address? = null

    //kodein
    override val kodein by kodein()

    //viewModels
    private val viewModel: BillingViewModel by instance()
    private val orderViewModel: OrderViewModel by instance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        products = args.products.toList()
        totalPrice = args.totalPrice
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stUpBillingProductsAdapter()
        setUpAddressProductsAdapter()


        if (!args.payment) {
            binding.rvProducts.visibility = View.INVISIBLE
            binding.bottomLine.visibility = View.INVISIBLE
            binding.totalBoxContainer.visibility = View.INVISIBLE
            binding.buttonPlaceOrder.visibility = View.INVISIBLE

        }


        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.address.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        addressAdapters.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        customSnackbarForError(
                            it.message.toString(),
                            R.dimen.snackbar_margin_bottom_details
                        )
                        binding.progressbarAddress.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        customSnackbarForComplete(
                            getString(R.string.your_order_was_placed),
                            R.dimen.snackbar_margin_bottom_details
                        )
                    }

                    is Resource.Error -> {
                        customSnackbarForError(
                            it.message.toString(),
                            R.dimen.snackbar_margin_bottom_details
                        )
                        binding.buttonPlaceOrder.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }

        addressAdapters.onClick = {
            selectedAddress = it
            if (!args.payment) {
                val bundle = Bundle().apply { putParcelable(BUNDLE_KEY_ADDRESS, it) }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment, bundle)
            }
        }


        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                customSnackbarForError(
                    getString(R.string.select_address),
                    R.dimen.snackbar_margin_bottom_details
                )
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }

        billingProductAdapter.differ.submitList(products)
        binding.tvTotalPrice.text =
            requireContext().getString(R.string.dolar) + totalPrice.toString()

        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.orders_items))
            setMessage(getString(R.string.do_you_want_to_order_you_cart_items))
            setNegativeButton(getString(R.string.delete_alert_dialog_text_negativ_button)) { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton(getString(R.string.delete_alert_dialog_text_positiv_button)) { dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice.toString(),
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setUpAddressProductsAdapter() {
        binding.rvAddress.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = addressAdapters
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun stUpBillingProductsAdapter() {
        binding.rvProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = billingProductAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
    }

}