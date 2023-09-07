package com.example.onlineshop.viewmodel.providerFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.onlineshop.data.Category
import com.example.onlineshop.viewmodel.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class VMBaseCategoryProviderFactory(
    private val firestore: FirebaseFirestore,
    private val category: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(firestore,category) as T
    }
}