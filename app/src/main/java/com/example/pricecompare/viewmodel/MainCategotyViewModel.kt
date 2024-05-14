package com.example.pricecompare.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pricecompare.data.Product
import com.example.pricecompare.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainCategotyViewModel(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val pagingInfo = pagingInfo()

    init {
        fetchSpecialProducts()
    }

    fun fetchSpecialProducts() {
        if (!pagingInfo.isPagingEnd) {
            _specialProducts.value = Resource.Loading()

            // Получаем все продукты
            firestore.collection("Products")
                .limit(pagingInfo.BestProductspage * 10)
                .get()
                .addOnSuccessListener { documents ->
                    val allProducts = documents.documents.map { it.toObject(Product::class.java)!! }
                    pagingInfo.isPagingEnd = allProducts == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = allProducts
                    val categories = allProducts.map { it.category }.distinct()

                    val categoryProducts = mutableListOf<Product>()

                    categories.forEach { category ->
                        val productsByCategory = allProducts.filter { it.category == category }
                            .sortedBy { it.price }
                            .take(10)
                        categoryProducts.addAll(productsByCategory)
                    }

                    _specialProducts.value = Resource.Success(categoryProducts)
                    pagingInfo.BestProductspage++
                }
                .addOnFailureListener {
                    _specialProducts.value = Resource.Error(it.message ?: "Error fetching products")
                }
        }
    }
}

internal data class pagingInfo(
    var BestProductspage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false

)