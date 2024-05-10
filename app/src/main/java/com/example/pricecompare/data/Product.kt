package com.example.pricecompare.data

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val shop: String,
    val price: Float,
    val description: String? = null,
    val volume: String? = null,
    val images: List<String>
){
    constructor(): this("0","","","",0f, images = emptyList())
}