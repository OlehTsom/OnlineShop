package com.example.onlineshop.helper

fun Float?.getProductPrice(price: Float): String{
    //this --> Percentage
    if (this == null)
        return price.toString()
    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * price

    return String.format("%.1f",priceAfterOffer)
}

fun Float?.getProductPriceFloat(price: Float): Float{
    //this --> Percentage
    if (this == null)
        return price
    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * price

    return priceAfterOffer
}