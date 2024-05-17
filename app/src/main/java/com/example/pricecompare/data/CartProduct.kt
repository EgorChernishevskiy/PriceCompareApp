package com.example.pricecompare.data

data class CartProduct(
    val product: Product,
    val quantity: Int,
    val shop: String
){
    constructor(): this(Product(), 1, "")
}
