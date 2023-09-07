package com.example.onlineshop.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewpagerAdapter(
    private val listFragments : List<Fragment>,
    fm : FragmentManager,
    lifecycle : Lifecycle
) : FragmentStateAdapter(fm,lifecycle){

    override fun getItemCount(): Int {
        return listFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return listFragments[position]
    }
}