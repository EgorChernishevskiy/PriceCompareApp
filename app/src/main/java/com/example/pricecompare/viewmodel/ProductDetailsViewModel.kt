package com.example.pricecompare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pricecompare.data.Product
import com.example.pricecompare.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductDetailsViewModel(private val firestore: FirebaseFirestore, private val productName: String?) : ViewModel() {
    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val products: StateFlow<Resource<List<Product>>> = _products

    init {
        loadProducts()
    }

    fun loadProducts() {
        firestore.collection("Products")
            .whereEqualTo("name", productName)
            .get()
            .addOnSuccessListener { result ->
                val productsList = result.map { document ->
                    Product(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        category = document.getString("category") ?: "",
                        shop = document.getString("shop") ?: "",
                        price = document.getDouble("price")?.toFloat() ?: 0f,
                        description = document.getString("description"),
                        volume = document.getString("volume"),
                        images = document.get("images") as List<String>? ?: listOf()
                    )
                }
                _products.value = Resource.Success(productsList)
    }
}
}
