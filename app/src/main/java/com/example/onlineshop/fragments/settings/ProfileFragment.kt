package com.example.onlineshop.fragments.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.onlineshop.BuildConfig
import com.example.onlineshop.R
import com.example.onlineshop.activities.LoginRegisterActivity
import com.example.onlineshop.databinding.FragmentProfileBinding
import com.example.onlineshop.helper.customSnackbarForError
import com.example.onlineshop.helper.showBottomNavigation
import com.example.onlineshop.util.Resource
import com.example.onlineshop.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class ProfileFragment : Fragment(), KodeinAware {
    private lateinit var binding: FragmentProfileBinding
    override val kodein by kodein()
    private val viewModel: ProfileViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }

        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                0f,
                emptyArray(),
                false
            )
            findNavController().navigate(action)
        }


        binding.linearLogOut.setOnClickListener {
            viewModel.logOut()
            val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
            startActivity(intent)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            requireActivity().finish()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireContext()).load(it.data!!.imagePath)
                            .error(ColorDrawable(Color.BLACK)).into(binding.imageUser)

                        binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                    }

                    is Resource.Error -> {
                        binding.progressbarSettings.visibility = View.GONE
                        customSnackbarForError(
                            it.message.toString(),
                            R.dimen.snackbar_margin_bottom_details
                        )
                    }

                    else -> Unit
                }
            }
        }

        binding.linearSupprot.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_supportFragment)
        }

        binding.tvVersion.text = "Version: " + BuildConfig.VERSION_NAME

    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }

}