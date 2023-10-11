package com.example.onlineshop.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.example.onlineshop.R
import com.example.onlineshop.util.ConfigManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.json.JSONObject

class GoogleSignInManager(private val context: Context, private val googleSignInLauncher : ActivityResultLauncher<Intent>) {


    fun startGoogleSignIn() {
        val configManager = ConfigManager()
        val config = configManager.loadConfig(context)
        val requestIdToken = config.google_request_id_token

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(requestIdToken)
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
