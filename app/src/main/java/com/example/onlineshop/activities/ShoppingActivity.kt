package com.example.onlineshop.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.onlineshop.R
import com.example.onlineshop.databinding.ActivityShopingBinding
import com.example.onlineshop.util.Resource
import com.example.onlineshop.viewmodel.CardViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.android.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class ShoppingActivity : AppCompatActivity(),BottomNavigationListener,KodeinAware {
    val binding by lazy {
        ActivityShopingBinding.inflate(layoutInflater)
    }

    override val kodein by kodein()
    private val viewModel : CardViewModel by instance()

    val navController by lazy { findNavController(R.id.shoppingNavHost) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_OnlineShop)
        setContentView(binding.root)

        binding.bottomNavigation.setupWithNavController(navController)



        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Success ->{
                        val amount = it.data?.size ?: 0
                        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
                        bottomNavigation.getOrCreateBadge(R.id.cardFragment).apply {
                            number = amount
                            backgroundColor = resources.getColor(R.color.g_blue)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onBottomNavigationSelected(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }


}

interface BottomNavigationListener {
    fun onBottomNavigationSelected(itemId: Int)
}
