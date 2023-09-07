package com.example.onlineshop.data

sealed class Category(val category : String) {
    object Tshirts : Category("Tshirts")
    object Jackets : Category("Jackets")
    object Trousers : Category("Trousers")
    object Suits : Category("Suits")
    object Jeans : Category("Jeans")
}