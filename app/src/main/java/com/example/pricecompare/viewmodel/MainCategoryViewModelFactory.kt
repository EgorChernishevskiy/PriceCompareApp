package com.example.pricecompare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class MainCategoryViewModelFactory(private val firestore: FirebaseFirestore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainCategotyViewModel::class.java)) {
            return MainCategotyViewModel(firestore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
