package com.example.pricecompare.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pricecompare.viewmodel.CartViewModel
import com.example.pricecompare.firebase.FirebaseCommon
import com.google.firebase.firestore.FirebaseFirestore

class CartViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val firebaseCommon: FirebaseCommon
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(firestore, firebaseCommon) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
