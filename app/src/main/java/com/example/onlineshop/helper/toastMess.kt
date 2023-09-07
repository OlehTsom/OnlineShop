package com.example.onlineshop.helper


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.onlineshop.R
import com.google.android.material.snackbar.Snackbar

fun Fragment.customSnackbarForError(text: String, marginBottom: Int) {
    val snackbar = this.view?.let { Snackbar.make(it, "", Snackbar.LENGTH_SHORT) }
    val snackbarLayout = snackbar?.view as Snackbar.SnackbarLayout
    val inflater = LayoutInflater.from(view?.context)

    // Інфлейт кастомного макету
    val customSnackbarView = inflater.inflate(R.layout.custom_snackbar, null)

    customSnackbarView.visibility = View.VISIBLE

    // Налаштування тексту та іконки
    customSnackbarView.findViewById<TextView>(android.R.id.text1).text = text
    customSnackbarView.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_cart)




    // Заміна вбудованого вигляду на свій

    snackbarLayout.setBackgroundColor(Color.TRANSPARENT)
    snackbarLayout.removeAllViews()
    val params = snackbarLayout.layoutParams as ViewGroup.MarginLayoutParams
    view?.resources?.let {
        it.getDimensionPixelSize(marginBottom).let { it1 ->
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin,
                it1
            )
        }
    }
    snackbarLayout.layoutParams = params
    snackbarLayout.addView(customSnackbarView)

    snackbar.show()
}


fun Fragment.customSnackbarForCompleteAddProductToCart(text: String, marginBottom: Int) {
    val snackbar = this.view?.let { Snackbar.make(it, "", Snackbar.LENGTH_SHORT) }
    val snackbarLayout = snackbar?.view as Snackbar.SnackbarLayout
    val inflater = LayoutInflater.from(view?.context)

    // Інфлейт кастомного макету
    val customSnackbarView = inflater.inflate(R.layout.custom_snackbar, null)

    customSnackbarView.visibility = View.VISIBLE

    // Налаштування тексту та іконки
    customSnackbarView.findViewById<TextView>(android.R.id.text1).text = text
    customSnackbarView.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_cart_more_darckly)
    customSnackbarView.findViewById<LinearLayout>(R.id.layoutLiner).setBackgroundResource(R.drawable.custom_snackbar_background_success)




    // Заміна вбудованого вигляду на свій

    snackbarLayout.setBackgroundColor(Color.TRANSPARENT)
    snackbarLayout.removeAllViews()
    val params = snackbarLayout.layoutParams as ViewGroup.MarginLayoutParams
    view?.resources?.let {
        it.getDimensionPixelSize(marginBottom).let { it1 ->
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin,
                it1
            )
        }
    }
    snackbarLayout.layoutParams = params
    snackbarLayout.addView(customSnackbarView)

    snackbar.show()
}

