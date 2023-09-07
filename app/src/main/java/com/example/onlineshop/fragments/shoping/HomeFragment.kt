package com.example.onlineshop.fragments.shoping

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.onlineshop.R
import com.example.onlineshop.activities.BottomNavigationListener
import com.example.onlineshop.activities.ShoppingActivity
import com.example.onlineshop.adapters.HomeViewpagerAdapter
import com.example.onlineshop.databinding.FragmentHomeBinding
import com.example.onlineshop.fragments.categories.SuitsFragment
import com.example.onlineshop.fragments.categories.TshirtsFragment
import com.example.onlineshop.fragments.categories.TrousersFragment
import com.example.onlineshop.fragments.categories.JeansFragment
import com.example.onlineshop.fragments.categories.MainCategoryFragment
import com.example.onlineshop.fragments.categories.JacketsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private var bottomNavigationListener: BottomNavigationListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomNavigationListener) {
            bottomNavigationListener = context
        } else {
            throw RuntimeException("$context must implement BottomNavigationListener")
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragment = arrayListOf<Fragment>(
            MainCategoryFragment(),
            TshirtsFragment(),
            JacketsFragment(),
            SuitsFragment(),
            SuitsFragment(),
            TrousersFragment()
        )


        binding.homeHeader.setOnClickListener {
            bottomNavigationListener?.onBottomNavigationSelected(R.id.searchFragment)
        }

        val viewPager2Adapter = HomeViewpagerAdapter(categoriesFragment,childFragmentManager,lifecycle)
        binding.viewPagerHome.adapter = viewPager2Adapter
        binding.viewPagerHome.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout,binding.viewPagerHome){tab, position ->
            when(position){
                0 -> tab.text = getString(R.string.main_viewpager_name_fragment)
                1 -> tab.text = getString(R.string.zweite_viewpager_name_fragment)
                2 -> tab.text = getString(R.string.dritte_viewpager_name_fragment)
                3 -> tab.text = getString(R.string.vierte_viewpager_name_fragment)
                4 -> tab.text = getString(R.string.funfte_viewpager_name_fragment)
                5 -> tab.text = getString(R.string.sexte_viewpager_name_fragment)
            }
        }.attach()


    }

    override fun onDetach() {
        super.onDetach()
        bottomNavigationListener = null
    }
}