package com.example.onlineshop.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onlineshop.R
import com.example.onlineshop.databinding.FragmentSupportBinding
import com.example.onlineshop.helper.hideBottomNavigation

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

        binding.imageCloseSupportFrag.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.butWriteUs.setOnClickListener {
            findNavController().navigate(R.id.action_supportFragment_to_messageSupportFragment)
        }

    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
    }
}