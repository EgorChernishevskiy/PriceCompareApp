package com.example.pricecompare.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pricecompare.firebase.FirebaseCommon
import com.example.pricecompare.viewmodel.SearchViewModel
import com.google.firebase.firestore.FirebaseFirestore

class SearchViewModelFactory(private val firestore: FirebaseFirestore,  private val firebaseCommon: FirebaseCommon) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            SearchViewModel(firestore, firebaseCommon) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
