package com.example.onlineshop.fragments.loginRegister

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.onlineshop.R
import com.example.onlineshop.activities.ShoppingActivity
import com.example.onlineshop.data.User
import com.example.onlineshop.databinding.FragmentLoginBinding
import com.example.onlineshop.dialog.setUpBottomSheetDialog
import com.example.onlineshop.firebase.GoogleSignInManager
import com.example.onlineshop.helper.customSnackbarForComplete
import com.example.onlineshop.helper.customSnackbarForError
import com.example.onlineshop.util.Resource
import com.example.onlineshop.viewmodel.LoginViewModel
import com.example.onlineshop.viewmodel.RegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class LoginFragment : Fragment(), KodeinAware {
    lateinit var binding: FragmentLoginBinding
    override val kodein by kodein()
    private val viewModel: LoginViewModel by instance()
    private val viewModelRegister: RegisterViewModel by instance()

    private val auth: FirebaseAuth by instance()

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val googleSignInManager by lazy {
        GoogleSignInManager(
            requireContext(),
            googleSignInLauncher
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.butLoginLogin.setOnClickListener {
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
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.butLoginLogin.startAnimation()
                    }

                    is Resource.Success -> {
                        startIntentForSuccessLogin()
                        binding.butLoginLogin.revertAnimation()
                    }

                    is Resource.Error -> {
                        customSnackbarForError(
                            it.message.toString(),
                            R.dimen.snackbar_margin_bottom_details
                        )
                        binding.butLoginLogin.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }

        binding.tvForgotPasswordLogin.setOnClickListener {
            setUpBottomSheetDialog { email ->
                viewModel.resetPassword(email)

            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        customSnackbarForComplete(
                            getString(R.string.snacbar_reset_text_login_fragment),
                            R.dimen.snackbar_margin_bottom_card
                        )
                    }

                    is Resource.Error -> {
                        customSnackbarForError(
                            it.message.toString(),
                            R.dimen.snackbar_margin_bottom_card
                        )
                    }

                    else -> Unit
                }
            }
        }


        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                    account?.let {
                        googleAuthForFirebase(it)
                    }
                }
            }

        binding.googleLogin.setOnClickListener {
            googleSignInManager.startGoogleSignIn()
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {
                    viewModelRegister.saveUserInfo(
                        auth.uid!!,
                        User(
                            account.givenName ?: "Username",
                            account.familyName ?: "Family Name",
                            account.email ?: "", account.photoUrl?.toString() ?: ""
                        )
                    )
                    startIntentForSuccessLogin()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    customSnackbarForError(
                        e.message.toString(),
                        R.dimen.snackbar_margin_bottom_card
                    )
                }
            }
        }
    }

    private fun startIntentForSuccessLogin() {
        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun navBackToRegisterFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

}