package com.example.onlineshop.fragments.settings.supportSections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.onlineshop.R
import com.example.onlineshop.data.SupportMessage
import com.example.onlineshop.databinding.FragmentSupportMessageBinding
import com.example.onlineshop.helper.customSnackbarForComplete
import com.example.onlineshop.util.Resource
import com.example.onlineshop.util.validatinon.MessageValidation
import com.example.onlineshop.util.validatinon.MessageValidationFailedState
import com.example.onlineshop.viewmodel.SupportViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class MessageSupportFragment : Fragment(), KodeinAware {
    private lateinit var binding : FragmentSupportMessageBinding

    override val kodein by kodein()
    private val viewModel : SupportViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSupportMessageBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageCloseSupportFrag.setOnClickListener {
            findNavController().navigateUp()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.sendMessageState.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.buttonSend.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.buttonSend.revertAnimation()
                        customSnackbarForComplete(
                            getString(R.string.wait_for_a_reply),
                            R.dimen.snackbar_margin_bottom_details)
                        findNavController().navigateUp()
                    }
                    is Resource.Error ->{
                        binding.buttonSend.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collectLatest {
                messageValidation(it)
            }
        }

        binding.apply {
            buttonSend.setOnClickListener {
                viewModel.sendMessageForSupport(
                    SupportMessage(
                        edMail.text.toString(),
                        edPhone.text.toString(),
                        edFirstName.text.toString(),
                        edMessage.text.toString()
                    )
                )
            }
        }

    }

    private suspend fun messageValidation(messageValFailedState: MessageValidationFailedState){
        var isValidateOk = true
        val fieldsToValidate = mapOf(
            binding.edFirstName to messageValFailedState.name,
            binding.edMail to messageValFailedState.email,
            binding.edMessage to messageValFailedState.message
        )

        withContext(Dispatchers.Main){
            fieldsToValidate.forEach{(view,validationResult) ->
                if (validationResult is MessageValidation.Failed){
                    isValidateOk = false
                    view.apply {
                        requestFocus()
                        error = validationResult.messError
                    }
                }
            }
            if (!isValidateOk) {
                withContext(Dispatchers.IO) {
                    delay(1000)
                    withContext(Dispatchers.Main) {
                        binding.buttonSend.revertAnimation()
                    }
                }
            }
        }

    }
}