package com.example.onlineshop.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.onlineshop.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setUpBottomSheetDialog(
    onSendClick:(String) -> Unit
){
     val dialog = BottomSheetDialog(requireContext(),R.style.DialogStyle)
     val view = layoutInflater.inflate(R.layout.reset_password_dialog,null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.etResetPassword)
    val butSendResetPassword = view.findViewById<Button>(R.id.butSendResetPassword)
    val butCancelResetPassword= view.findViewById<Button>(R.id.butCancelResetPassword)

    butSendResetPassword.setOnClickListener {
        val email = edEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }

    butCancelResetPassword.setOnClickListener {
       dialog.dismiss()
    }

}