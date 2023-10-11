package com.example.onlineshop.fragments.shoping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.onlineshop.R
import com.example.onlineshop.data.Address
import com.example.onlineshop.databinding.FragmentAddressBinding
import com.example.onlineshop.helper.customSnackbarForComplete
import com.example.onlineshop.helper.customSnackbarForError
import com.example.onlineshop.util.validatinon.AddressValidation
import com.example.onlineshop.util.validatinon.AddressValidationFailedState
import com.example.onlineshop.util.Resource
import com.example.onlineshop.viewmodel.AddressViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class AddressFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private lateinit var binding: FragmentAddressBinding
    private val viewModel: AddressViewModel by instance()
    private val args by navArgs<AddressFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }

                    is Resource.Error -> {
                        customSnackbarForError(
                            it.message.toString(),
                            R.dimen.snackbar_margin_bottom_card
                        )
                        binding.progressbarAddress.visibility = View.INVISIBLE
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validateError.collect { validation ->
                validation(validation)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = args.address

        if (address == null) {
            binding.buttonDelelte.visibility = View.GONE
        } else {
            binding.apply {
                edAddressTitle.setText(address.addressTitle)
                edFullName.setText(address.fullName)
                edStreet.setText(address.street)
                edCity.setText(address.city)
                edPhone.setText(address.phone)
                edState.setText(address.state)
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.updateAddressDate.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                        customSnackbarForComplete(
                            getString(R.string.address_updated_successfully),
                            R.dimen.snackbar_margin_bottom_details
                        )
                    }

                    is Resource.Error -> {
                        customSnackbarForError(
                            it.message.toString(),
                            R.dimen.snackbar_margin_bottom_card
                        )
                        binding.progressbarAddress.visibility = View.INVISIBLE
                    }

                    else -> Unit
                }
            }
        }

        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle = edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val street = edStreet.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val state = edState.text.toString()

                val address = Address(addressTitle, fullName, street, phone, city, state)

                if (args.address == null) {
                    viewModel.addAddressToAccount(address)
                } else {
                    viewModel.updateAddressDate(args.address!!, address)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteAddressResult.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                        customSnackbarForComplete(
                            getString(R.string.address_deleted_successfully),
                            R.dimen.snackbar_margin_bottom_details
                        )
                    }

                    is Resource.Error -> {
                        customSnackbarForError(
                            it.message.toString(),
                            R.dimen.snackbar_margin_bottom_card
                        )
                        binding.progressbarAddress.visibility = View.INVISIBLE
                    }

                    else -> Unit
                }
            }
        }

        binding.buttonDelelte.setOnClickListener {
            if (address != null)
                viewModel.deleteAddress(address)
        }

        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private suspend fun validation(validation: AddressValidationFailedState) {
        val fieldsToValidate = mapOf(
            binding.edAddressTitle to validation.addressTitle,
            binding.edFullName to validation.fullName,
            binding.edPhone to validation.phone,
            binding.edStreet to validation.street,
            binding.edCity to validation.city,
            binding.edState to validation.state
        )

        withContext(Dispatchers.Main) {
            fieldsToValidate.forEach { (view, validationResult) ->
                if (validationResult is AddressValidation.Failed) {
                    view.apply {
                        requestFocus()
                        error = validationResult.message
                    }
                }
            }
        }
    }


}
