package com.example.onlineshop.fragments.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.onlineshop.R
import com.example.onlineshop.adapters.HomeViewpagerAdapter
import com.example.onlineshop.databinding.FragmentSupportBinding
import com.example.onlineshop.fragments.settings.supportSections.CallSupportFragment
import com.example.onlineshop.fragments.settings.supportSections.LocationSupportFragment
import com.example.onlineshop.fragments.settings.supportSections.MessageSupportFragment
import com.google.android.material.tabs.TabLayoutMediator

class SupportFragment : Fragment() {
    lateinit var binding: FragmentSupportBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSupportBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportListFragment = arrayListOf<Fragment>(
            MessageSupportFragment(),
            CallSupportFragment(),
            LocationSupportFragment()
        )

        val viewPager2Adapter = HomeViewpagerAdapter(supportListFragment,childFragmentManager,lifecycle)
        binding.viewPagerSupport.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayoutSupportFragment,binding.viewPagerSupport){tab, position ->
            when(position){
                0 -> tab.text = getString(R.string.message_support_tab_layout)
                1 -> tab.text = getString(R.string.call_support_tab_layout)
                2 -> tab.text = getString(R.string.location_support_tab_layout)
            }

        }.attach()

    }
}