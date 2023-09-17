package com.example.onlineshop.dialog

import android.app.Activity
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.onlineshop.R
import com.example.onlineshop.activities.ShoppingActivity.Companion.isConnected
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun Activity.setUpBottomSheetDialogLoseConnection(
){
    val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
    val view = layoutInflater.inflate(R.layout.no_nettwork_dialog,null)
    dialog.setContentView(view)
    dialog.setCancelable(false)
    dialog.window?.decorView?.alpha = 0.93f
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val butTryAgain= view.findViewById<Button>(R.id.butTryAgain)
    val butGoOut= view.findViewById<Button>(R.id.butGoOut)

    butTryAgain.setOnClickListener {
        if (isConnected) dialog.dismiss()
        else{
            dialog.dismiss()
            CoroutineScope(Dispatchers.IO).launch {
                delay(700)
                withContext(Dispatchers.Main){
                    setUpBottomSheetDialogLoseConnection()
                }
            }

        }
    }
    butGoOut.setOnClickListener {
        finish()
    }

}