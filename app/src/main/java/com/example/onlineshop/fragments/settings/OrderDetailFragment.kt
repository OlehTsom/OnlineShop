package com.example.onlineshop.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.R
import com.example.onlineshop.adapters.BillingProductsAdapter
import com.example.onlineshop.data.Order
import com.example.onlineshop.data.OrderStatus
import com.example.onlineshop.data.getOrderStatus
import com.example.onlineshop.databinding.FragmentOrderDetailBinding
import com.example.onlineshop.util.VerticalItemDecoration

class OrderDetailFragment : Fragment() {
    private lateinit var binding : FragmentOrderDetailBinding
    private val productsAdapter by lazy { BillingProductsAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order

        setupProductsAdapter()

        setupTextViewAndRvList(order)

        setUpStepsView(order)
    }

    private fun setUpStepsView(order: Order) {
        binding.stepView.setSteps(
            mutableListOf(
                OrderStatus.Ordered.status,
                OrderStatus.Confirmed.status,
                OrderStatus.Shipped.status,
                OrderStatus.Delivered.status
            )
        )

        val currentOrderStatus = when(getOrderStatus(order.orderStatus)){
            is OrderStatus.Ordered -> 0
            is OrderStatus.Confirmed -> 1
            is OrderStatus.Shipped -> 2
            is OrderStatus.Delivered -> 3
            else -> 3
        }

        binding.stepView.go(currentOrderStatus,false)
        if (currentOrderStatus == 3){
            binding.stepView.done(true)
        }

        binding.imageCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun setupTextViewAndRvList(order: Order) {
        binding.apply {
            tvOrderId.text = getString(R.string.order_word) + {order.orderId}
            tvAddress.text = "${order.address.street}, ${order.address.state}"
            tvPhoneNumber.text = order.address.phone
            tvFullName.text = order.address.fullName
            tvTotalPrice.text = getString(R.string.dolar) +{order.totalPrice}

            productsAdapter.differ.submitList(order.products)

        }
    }

    private fun setupProductsAdapter() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = productsAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

}