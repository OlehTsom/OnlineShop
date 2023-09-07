package com.example.onlineshop.fragments.shoping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.onlineshop.R
import com.example.onlineshop.data.User
import com.example.onlineshop.databinding.FragmentUserAccountBinding
import com.example.onlineshop.dialog.setUpBottomSheetDialog
import com.example.onlineshop.helper.customSnackbarForCompleteAddProductToCart
import com.example.onlineshop.helper.customSnackbarForError
import com.example.onlineshop.util.Resource
import com.example.onlineshop.viewmodel.UserAccountViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class UserAccountFragment : Fragment(),KodeinAware {
    private lateinit var binding : FragmentUserAccountBinding
    private lateinit var  imageActivityResultLauncher : ActivityResultLauncher<Intent>
    private var imageUri : Uri ?= null

    override val kodein by kodein()
    private val viewModel : UserAccountViewModel by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            imageUri = it.data?.data
            Glide.with(this).load(imageUri).into(binding.imageUser)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAccountBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        showUserLoading()
                    }
                    is Resource.Success ->{
                        hideUserLoading()
                        showUserInformation(it.data!!)
                    }
                    is Resource.Error ->{
                        hideUserLoading()
                        customSnackbarForError(it.message.toString(), R.dimen.snackbar_margin_bottom_details)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.buttonSave.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.buttonSave.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resource.Error ->{
                        binding.buttonSave.revertAnimation()
                        customSnackbarForError(it.message.toString(), R.dimen.snackbar_margin_bottom_details)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collectLatest {
                when(it){
                    is Resource.Loading ->{

                    }
                    is Resource.Success ->{
                        customSnackbarForCompleteAddProductToCart(getString(R.string.snacbar_reset_text_login_fragment),
                            R.dimen.snackbar_margin_bottom_details)
                    }
                    is Resource.Error ->{
                        customSnackbarForError(it.message.toString(), R.dimen.snackbar_margin_bottom_details)
                    }
                    else -> Unit
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = edFirstName.text.toString().trim()
                val lastName = edLastName.text.toString().trim()
                val email = edEmail.text.toString().trim()
                val user = User(firstName, lastName, email)
                viewModel.updateUserInfo(user,imageUri)
            }
        }

        binding.imageEdit.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }

        binding.tvUpdatePassword.setOnClickListener {
            setUpBottomSheetDialog {email ->
                viewModel.resetUserPassword(it.toString().trim())
            }
        }

        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }


    }

    private fun showUserInformation(data: User) {
        binding.apply {
            Glide.with(this@UserAccountFragment).load(data.imagePath).error(ColorDrawable(Color.BLACK)).into(imageUser)
            edFirstName.setText(data.firstName)
            edLastName.setText(data.lastName)
            edEmail.setText(data.email)
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            buttonSave.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            buttonSave.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
        }
    }

}