package com.example.pricecompare.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pricecompare.firebase.FirebaseCommon
import com.example.pricecompare.viewmodel.ProductDetailsViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailsViewModelFactory(private val firestore: FirebaseFirestore, private val firebaseCommon: FirebaseCommon,
                                     private val productName: String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailsViewModel::class.java)) {
            return ProductDetailsViewModel(firestore, firebaseCommon, productName) as T  // Обратите внимание, что мы передаем firestore и categoryName
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}