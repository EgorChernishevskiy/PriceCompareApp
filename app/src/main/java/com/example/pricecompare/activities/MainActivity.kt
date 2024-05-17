package com.example.pricecompare.activities

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pricecompare.R
import com.example.pricecompare.databinding.ActivityMainBinding
import com.example.pricecompare.firebase.FirebaseCommon
import com.example.pricecompare.utils.Resource
import com.example.pricecompare.viewmodel.CartViewModel
import com.example.pricecompare.viewmodel.CategoryViewModel
import com.example.pricecompare.viewmodel.factory.CartViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val firestore = FirebaseFirestore.getInstance()
        val firebaseCommon = FirebaseCommon(firestore)
        val factory = CartViewModelFactory(firestore, firebaseCommon)
        val viewModel = ViewModelProvider(this, factory).get(CartViewModel::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        val count = it.data?.size ?: 0
                        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
                        bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.g_orange_yellow)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}
