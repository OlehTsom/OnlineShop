package com.example.onlineshop.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchCategory(
    val id: String,
    val nameCategory: String,
    val category : String,
    val image : String
) : Parcelable{
    constructor() : this("","","","")
}