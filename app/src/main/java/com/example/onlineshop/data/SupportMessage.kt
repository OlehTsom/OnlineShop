package com.example.onlineshop.data

data class SupportMessage(
    val email : String,
    val phoneNumber : String,
    val name : String,
    val message : String
){
    constructor() : this("","","","")
}
