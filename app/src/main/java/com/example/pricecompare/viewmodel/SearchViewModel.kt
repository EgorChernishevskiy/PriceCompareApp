package com.example.pricecompare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pricecompare.data.Product
import com.example.pricecompare.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class SearchViewModel(private val firestore: FirebaseFirestore) : ViewModel() {

    private val _searchResults = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val searchResults: StateFlow<Resource<List<Product>>> get() = _searchResults

    fun searchProducts(query: String) {
        val normalizedQuery = normalize(query)
        viewModelScope.launch {
            try {
                firestore.collection("Product1")
                    .get()
                    .addOnSuccessListener { result ->
                        val products = result.documents.mapNotNull { document ->
                            document.toObject(Product::class.java)
                        }
                        val filteredProducts = products.filter { product ->
                            normalize(product.name).contains(normalizedQuery)
                        }
                        _searchResults.value = Resource.Success(filteredProducts)
                    }
                    .addOnFailureListener { exception ->
                        _searchResults.value = Resource.Error(exception.message ?: "Error occurred")
                    }
            } catch (e: Exception) {
                _searchResults.value = Resource.Error(e.message ?: "Error occurred")
            }
        }
    }

    private fun normalize(input: String): String {
        return input.split(" ")
            .map { word -> word.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase(Locale.getDefault()) } }
            .sorted()
            .joinToString(" ")
    }
}






//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.pricecompare.data.Product
//import com.example.pricecompare.utils.LevenshteinDistance
//import com.example.pricecompare.utils.Resource
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//
//class SearchViewModel(private val firestore: FirebaseFirestore) : ViewModel() {
//
//    private val _searchResults = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
//    val searchResults: StateFlow<Resource<List<Product>>> get() = _searchResults
//
//    fun searchProducts(query: String) {
//        viewModelScope.launch {
//            _searchResults.value = Resource.Loading()
//            try {
//                val result = firestore.collection("Products")
//                    .get()
//                    .await()
//                val products = result.toObjects(Product::class.java)
//                val filteredProducts = products.filter { isFuzzyMatch(it.name, query) }
//                _searchResults.value = Resource.Success(filteredProducts)
//            } catch (e: Exception) {
//                _searchResults.value = Resource.Error(e.message ?: "Ошибка при поиске продуктов")
//            }
//        }
//    }
//
//    private fun isFuzzyMatch(text: String, query: String): Boolean {
//        val threshold = 5  // Пороговое значение для расстояния Левенштейна
//        return LevenshteinDistance.compute(text, query) <= threshold
//    }
//}
//
//
