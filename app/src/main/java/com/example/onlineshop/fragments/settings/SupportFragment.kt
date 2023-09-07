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
import com.example.onlineshop.databinding.FragmentSupportBinding

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


        binding.button.setOnClickListener {
            val viberPackageName = "com.viber.voip"
                try {
                    activity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$viberPackageName")))
                } catch (ex: ActivityNotFoundException) {
                    activity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$viberPackageName")))
                }
            }

    }
}