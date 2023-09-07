package com.example.onlineshop.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus : String,
    val totalPrice : String,
    val products : List<CartProduct>,
    val address: Address,
    val data: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
    val orderId : Long = nextLong(0,100_000_000_000) + totalPrice.toDouble().toLong(),
    val userUid: String ?= null
) : Parcelable{
    constructor() : this("","", emptyList(),Address(),"",0L,"")
}
