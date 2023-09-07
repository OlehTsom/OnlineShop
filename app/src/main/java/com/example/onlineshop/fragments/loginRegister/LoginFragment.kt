package com.example.onlineshop.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.onlineshop.R
import com.example.onlineshop.activities.ShoppingActivity
import com.example.onlineshop.databinding.FragmentLoginBinding
import com.example.onlineshop.dialog.setUpBottomSheetDialog
import com.example.onlineshop.util.Resource
import com.example.onlineshop.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class LoginFragment: Fragment(), KodeinAware{
    lateinit var binding: FragmentLoginBinding
    override val kodein by kodein()
    private val viewModel : LoginViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.butLoginLogin.setOnClickListener{
            binding.apply {
                val email = etEmailLogin.text.toString().trim()
                val password = etPasswordLogin.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.login(email, password)
                }
            }
        }

        binding.tvDontHaveAccount.setOnClickListener {
            navBackToRegisterFragment()
        }

        lifecycleScope.launchWhenCreated {
            viewModel.login.collect{
                when(it){
                    is Resource.Loading -> {binding.butLoginLogin.startAnimation()}
                    is Resource.Success -> {
                            Intent(requireActivity(),ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        binding.butLoginLogin.revertAnimation()
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                        binding.butLoginLogin.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }

        binding.tvForgotPasswordLogin.setOnClickListener {
            setUpBottomSheetDialog {email ->
                viewModel.resetPassword(email)

            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.resetPassword.collect{
                when(it){
                    is Resource.Loading -> {
                    }
                    is Resource.Success ->{
                        Snackbar.make(requireView(),
                            getString(R.string.snacbar_reset_text_login_fragment), Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        Snackbar.make(requireView(),"Error: ${it.message.toString()}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun navBackToRegisterFragment(){
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

}