package com.example.pricecompare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailsViewModelFactory(private val firestore: FirebaseFirestore, private val productName: String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailsViewModel::class.java)) {
            return ProductDetailsViewModel(firestore, productName) as T  // Обратите внимание, что мы передаем firestore и categoryName
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}