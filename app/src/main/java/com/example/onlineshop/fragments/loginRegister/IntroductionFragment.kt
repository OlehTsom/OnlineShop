package com.example.onlineshop.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.onlineshop.R
import com.example.onlineshop.activities.ShoppingActivity
import com.example.onlineshop.databinding.FragmentIntroductionBinding
import com.example.onlineshop.viewmodel.IntroductionViewModel
import com.example.onlineshop.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTION_FRAGMENT
import com.example.onlineshop.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import kotlinx.coroutines.flow.collect
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class IntroductionFragment : Fragment(), KodeinAware {
    private lateinit var binding: FragmentIntroductionBinding
    override val kodein by kodein()
    private val viewModel: IntroductionViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.butStartIntroductio.setOnClickListener {
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
            viewModel.startButtonClick()
        }

        lifecycleScope.launchWhenCreated {
            viewModel.navigate.collect {
                when (it) {
                    SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    ACCOUNT_OPTION_FRAGMENT -> {
                        findNavController().navigate(ACCOUNT_OPTION_FRAGMENT)
                    }

                    else -> Unit
                }
            }

        }
    }
}