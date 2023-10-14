package com.example.onlineshop.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.onlineshop.R
import com.example.onlineshop.databinding.ActivityShopingBinding
import com.example.onlineshop.dialog.setUpBottomSheetDialogLoseConnection
import com.example.onlineshop.util.Resource
import com.example.onlineshop.util.connection.ConnectivityObserver
import com.example.onlineshop.util.connection.NetworkConnectivityObserver
import com.example.onlineshop.viewmodel.CardViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.kodein.di.android.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class ShoppingActivity : AppCompatActivity(), BottomNavigationListener, KodeinAware {
    override val kodein by kodein()
    val binding by lazy {
        ActivityShopingBinding.inflate(layoutInflater)
    }
    private lateinit var connectivityObserver: NetworkConnectivityObserver
    private val viewModel: CardViewModel by instance()
    val navController by lazy { findNavController(R.id.shoppingNavHost) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_OnlineShop)
        setContentView(binding.root)
        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        binding.bottomNavigation.setupWithNavController(navController)


        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        val amount = it.data?.size ?: 0
                        val bottomNavigation =
                            findViewById<BottomNavigationView>(R.id.bottom_navigation)
                        bottomNavigation.getOrCreateBadge(R.id.cardFragment).apply {
                            number = amount
                            backgroundColor = resources.getColor(R.color.g_braun)
                        }
                    }

                    else -> Unit
                }
            }
        }


        connectivityObserver.observe().onEach {
            when (it) {
                ConnectivityObserver.Status.Lost -> {
                    if (isConnected) isConnected = false
                    setUpBottomSheetDialogLoseConnection()
                }

                ConnectivityObserver.Status.Available -> {
                    if (!isConnected) isConnected = true
                }

                ConnectivityObserver.Status.NegativeConnection -> {
                    if (isConnected) isConnected = false
                }

                ConnectivityObserver.Status.PositiveConnection -> {
                    if (!isConnected) isConnected = true
                }

                ConnectivityObserver.Status.Unavailable -> {
                    if (isConnected) isConnected = false
                }

                else -> Unit
            }
        }.launchIn(lifecycleScope)

    }

    override fun onBottomNavigationSelected(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }

    companion object {
        @JvmStatic
        var isConnected = false
            private set
    }


}

interface BottomNavigationListener {
    fun onBottomNavigationSelected(itemId: Int)
}
