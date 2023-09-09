package com.example.onlineshop.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SupportMessage(
    val email : String,
    val phoneNumber : String,
    val name : String,
    val message : String,
    val data: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
){
    constructor() : this("","","","","")
}
