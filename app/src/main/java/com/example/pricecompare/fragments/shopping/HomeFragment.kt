package com.example.pricecompare.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pricecompare.R
import com.example.pricecompare.adapters.HomeViewpagerAdapter
import com.example.pricecompare.databinding.FragmentHomeBinding
import com.example.pricecompare.fragments.categories.BreadFragment
import com.example.pricecompare.fragments.categories.GroceryFragment
import com.example.pricecompare.fragments.categories.MainCategoryFragment
import com.example.pricecompare.fragments.categories.MeatFragment
import com.example.pricecompare.fragments.categories.MilkFragment
import com.example.pricecompare.fragments.categories.PastaFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            BreadFragment(),
            GroceryFragment(),
            MeatFragment(),
            MilkFragment(),
            PastaFragment()
        )

        val viewpager2Adapter = HomeViewpagerAdapter(categoriesFragments,
                                                     childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewpager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome){tab, position ->
            when (position){
                0 -> tab.text = "Main"
                1 -> tab.text = "Grocery"
                2 -> tab.text = "Milk"
                3 -> tab.text = "Bread"
                4 -> tab.text = "Pasta"
                5 -> tab.text = "Meat"
            }
        }.attach()
    }

}