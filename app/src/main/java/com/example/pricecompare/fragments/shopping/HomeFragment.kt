package com.example.pricecompare.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pricecompare.R
import com.example.pricecompare.adapters.HomeViewpagerAdapter
import com.example.pricecompare.databinding.FragmentHomeBinding
import com.example.pricecompare.fragments.categories.BaseCategoryFragment
import com.example.pricecompare.fragments.categories.MainCategoryFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCategoriesFromFirestore()
    }

    private fun loadCategoriesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Products")
            .get()
            .addOnSuccessListener { result ->
                val categories = mutableListOf<String>("main")  // "main" добавляется первым в список
                for (document in result) {
                    val category = document.getString("category") ?: continue
                    if (category != "main" && !categories.contains(category)) {
                        categories.add(category)
                    }
                }
                setupViewPagerAndTabs(categories)
            }
            .addOnFailureListener { e ->
                // Обработка ошибок
            }
    }

    private fun setupViewPagerAndTabs(categories: List<String>) {
        val fragments = categories.map { categoryName ->
            if (categoryName == "main") {
                MainCategoryFragment()  // Используется специализированный фрагмент для "main"
            } else {
                BaseCategoryFragment.newInstance(categoryName)
            }
        }
        val adapter = HomeViewpagerAdapter(fragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            tab.text = if (categories[position] == "main") "Main" else categories[position]
        }.attach()
    }
}

