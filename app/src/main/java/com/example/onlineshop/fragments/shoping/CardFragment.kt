package com.example.onlineshop.fragments.shoping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshop.R
import com.example.onlineshop.adapters.CartProductsAdapter
import com.example.onlineshop.data.CartProduct
import com.example.onlineshop.databinding.FragmentCartBinding
import com.example.onlineshop.firebase.FirebaseCommon
import com.example.onlineshop.helper.customSnackbarForError
import com.example.onlineshop.helper.showBottomNavigation
import com.example.onlineshop.util.Constants.BUNDLE_KEY_PRODUCT
import com.example.onlineshop.util.Resource
import com.example.onlineshop.util.VerticalItemDecoration
import com.example.onlineshop.viewmodel.CardViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class CardFragment : Fragment(), KodeinAware {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductsAdapter() }

    override val kodein by kodein()
    private val viewModel: CardViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAdapterFoRv()

        var totalPrice = 0f
        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    totalPrice = it.replace(",", ".").toFloat()
                    binding.tvTotalPrice.text = getString(R.string.dolar) + price
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteroductDialog.collectLatest { cartProduct ->
                stationDeleteAlertDialog(cartProduct) {}.show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.amountCheck.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        customSnackbarForError(
                            getString(R.string.snackbar_maximum_amount_text_cardFragmrnt),
                            R.dimen.snackbar_margin_bottom_card
                        )
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()) {
                            showEmptyCart()
                            hideOtherViews()
                        } else {
                            hideEmptyCard()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }

                    is Resource.Error -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> Unit
                }
            }
        }


        cartAdapter.onProductClick = {
            val bundle = Bundle().apply { putParcelable(BUNDLE_KEY_PRODUCT, it.product) }
            findNavController().navigate(
                R.id.action_cardFragment_to_productsDetailsFragment,
                bundle
            )
        }

        cartAdapter.onPlusClick = {
            viewModel.changeAmountProduct(it, FirebaseCommon.AmountChanging.INCREASE)
        }

        cartAdapter.onMinesClick = {
            viewModel.changeAmountProduct(it, FirebaseCommon.AmountChanging.DECREASE)
        }



        binding.buttonCheckout.setOnClickListener {
            val action = CardFragmentDirections
                .actionCardFragmentToBillingFragment(
                    totalPrice,
                    cartAdapter.differ.currentList.toTypedArray(),
                    true
                )
            findNavController().navigate(action)
        }



        ItemTouchHelper(createItemTouchHelper()).apply {
            attachToRecyclerView(binding.rvCart)
        }

    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCard() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun setUpAdapterFoRv() {
        binding.rvCart.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

    private fun stationDeleteAlertDialog(
        cartProduct: CartProduct,
        result: ((Boolean) -> Unit)
    ): AlertDialog.Builder {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.delete_alert_dialog_text_title))
            setMessage(getString(R.string.delete_alert_dialog_text_message))
            setNegativeButton(getString(R.string.delete_alert_dialog_text_negativ_button)) { dialog, _ ->
                result.invoke(false)
                dialog.dismiss()
            }
            setPositiveButton(getString(R.string.delete_alert_dialog_text_positiv_button)) { dialog, _ ->
                result.invoke(true)
                viewModel.deleteItemFromCart(cartProduct)
                dialog.dismiss()
            }
        }
        alertDialog.create()

        return alertDialog
    }

    private fun createItemTouchHelper(): ItemTouchHelper.Callback {
        val onItemTouchHelper = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int = makeMovementFlags(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.START or ItemTouchHelper.END
            )

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val cardProduct = cartAdapter.differ.currentList[viewHolder.adapterPosition]
                stationDeleteAlertDialog(cardProduct) { shouldDelete ->
                    if (shouldDelete) {
                        // Виконати видалення
                    } else {
                        // Оповістити про скасування дії
                        cartAdapter.notifyDataSetChanged()
                    }
                }.show()
            }
        }
        return onItemTouchHelper
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }

}