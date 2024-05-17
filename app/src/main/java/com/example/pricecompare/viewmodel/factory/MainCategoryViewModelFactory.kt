package com.example.pricecompare.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pricecompare.firebase.FirebaseCommon
import com.example.pricecompare.viewmodel.MainCategotyViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MainCategoryViewModelFactory(private val firestore: FirebaseFirestore, private val firebaseCommon: FirebaseCommon) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainCategotyViewModel::class.java)) {
            return MainCategotyViewModel(firestore, firebaseCommon) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
