package com.example.pricecompare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pricecompare.data.Product
import com.example.pricecompare.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CategoryViewModel(private val firestore: FirebaseFirestore, private val categoryName: String?) : ViewModel() {
    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val products: StateFlow<Resource<List<Product>>> = _products

    private val pagingInfo = PagingInfoCategory()

    init {
        loadProducts()
    }

    fun loadProducts() {
        if (pagingInfo.isPagingEnd || pagingInfo.isLoading) return

        pagingInfo.isLoading = true
        _products.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val query = firestore.collection("Product1")
                    .whereEqualTo("category", categoryName)
                    .limit(pagingInfo.pageSize)

                val result = if (pagingInfo.lastDocument != null) {
                    query.startAfter(pagingInfo.lastDocument!!).get().await()
                } else {
                    query.get().await()
                }

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

                if (productsList.isEmpty()) {
                    pagingInfo.isPagingEnd = true
                } else {
                    pagingInfo.lastDocument = result.documents.last()
                    pagingInfo.productsPage++
                    pagingInfo.oldProducts += productsList
                    _products.value = Resource.Success(pagingInfo.oldProducts)
                }

            } catch (e: Exception) {
                _products.value = Resource.Error(e.message ?: "An error occurred")
            } finally {
                pagingInfo.isLoading = false
            }
        }
    }
}

internal data class PagingInfoCategory(
    var productsPage: Long = 1,
    var oldProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false,
    var lastDocument: DocumentSnapshot? = null,
    var isLoading: Boolean = false,
    val pageSize: Long = 10
)
