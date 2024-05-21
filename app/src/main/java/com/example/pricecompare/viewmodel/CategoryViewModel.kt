package com.example.pricecompare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.pricecompare.data.Product
import com.example.pricecompare.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategoryViewModel(private val firestore: FirebaseFirestore, private val categoryName: String?) : ViewModel() {
    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val products: StateFlow<Resource<List<Product>>> = _products

    private val pagingInfo = pagingInfoCategory()

    init {
        loadProducts()
    }

    fun loadProducts() {
        firestore.collection("Product1")
            .limit(pagingInfo.productsPage * 10)
            .whereEqualTo("category", categoryName)
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
                pagingInfo.isPagingEnd = productsList == pagingInfo.oldProducts
                pagingInfo.oldProducts = productsList
                _products.value = Resource.Success(productsList)
            }
    }
}

internal data class pagingInfoCategory(
    var productsPage: Long = 1,
    var oldProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false

)
