package com.example.onlineshop.firebase

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.onlineshop.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleSignInManager(private val context: Context, private val googleSignInLauncher : ActivityResultLauncher<Intent>) {


    fun startGoogleSignIn() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val signInClient = GoogleSignIn.getClient(context, options)
        val signInIntent = signInClient.signInIntent
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        signInClient.signOut()
        googleSignInLauncher.launch(signInIntent)
    }

}
