package com.example.pricecompare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class CategoryViewModelFactory(private val firestore: FirebaseFirestore, private val categoryName: String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(firestore, categoryName) as T  // Обратите внимание, что мы передаем firestore и categoryName
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
