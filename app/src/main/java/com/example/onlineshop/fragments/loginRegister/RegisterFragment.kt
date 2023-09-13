package com.example.onlineshop.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.onlineshop.R

import com.example.onlineshop.data.User
import com.example.onlineshop.databinding.FragmentRegisterBinding
import com.example.onlineshop.util.validatinon.RegisterFailedState
import com.example.onlineshop.util.validatinon.RegisterValidation
import com.example.onlineshop.viewmodel.RegisterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

private val TAG = "RegisterFragment"
class RegisterFragment : Fragment(),KodeinAware {
    lateinit var binding : FragmentRegisterBinding

    override val kodein by kodein()
    private val viewModel : RegisterViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvHaveAccount.setOnClickListener {
            navBackToLoginFragment()
        }

        binding.apply {
             butRegisterRegister.setOnClickListener {
                val user = User(
                    etFirstNameRegister.text.toString().trim(),
                    etLastNameRegister.text.toString().trim(),
                    etEmailRegister.text.toString().trim(),
                )
                val password = etPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when(it){
                    is com.example.onlineshop.util.Resource.Loading ->{
                        binding.butRegisterRegister.startAnimation()
                    }
                    is com.example.onlineshop.util.Resource.Success ->{
                        Log.d("test",it.data.toString())
                        binding.butRegisterRegister.revertAnimation()
                    }
                    is com.example.onlineshop.util.Resource.Error ->{
                        binding.butRegisterRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.validation.collect{validation ->
               registerValidation(validation)
            }
        }
    }

    private suspend fun registerValidation(validation : RegisterFailedState){
        val fieldsToValidate = mapOf(
            binding.etPasswordRegister to validation.password,
            binding.etEmailRegister to validation.email
        )

        withContext(Dispatchers.Main){
            fieldsToValidate.forEach{(view,validationResult) ->
                if (validationResult is RegisterValidation.Failed){
                    view.apply {
                        requestFocus()
                        error = validationResult.message
                    }
                }
            }
        }
        
    }

    private fun navBackToLoginFragment(){
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

}