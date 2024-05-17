package com.example.pricecompare.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pricecompare.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.pricecompare.adapters.ProductsAdapter
import com.example.pricecompare.databinding.FragmentBaseCategoryBinding
import com.example.pricecompare.utils.Resource
import com.example.pricecompare.viewmodel.CategoryViewModel
import com.example.pricecompare.viewmodel.factory.CategoryViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryFragment : Fragment() {
    private lateinit var binding: FragmentBaseCategoryBinding
    private lateinit var viewModel: CategoryViewModel
    private lateinit var productsAdapter: ProductsAdapter
    private var categoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryName = arguments?.getString("categoryName")
        val firestore = FirebaseFirestore.getInstance()
        val factory = CategoryViewModelFactory(firestore, categoryName)
        viewModel = ViewModelProvider(this, factory).get(CategoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProductsRecyclerView()

        productsAdapter.onClick = {
            val b = Bundle().apply { putString("productName", it.name) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, b)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.products.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.baseCategoryProgressBarBot.visibility = View.VISIBLE
                        is Resource.Success -> {
                            productsAdapter.differ.submitList(resource.data)
                            binding.baseCategoryProgressBarBot.visibility = View.GONE
                        }
                        is Resource.Error -> {
                            binding.baseCategoryProgressBarBot.visibility = View.GONE
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.loadProducts()
            }
        })

    }

    companion object {
        private const val ARG_CATEGORY_NAME = "categoryName"

        fun newInstance(categoryName: String): BaseCategoryFragment {
            val fragment = BaseCategoryFragment()
            val args = Bundle()
            args.putString(ARG_CATEGORY_NAME, categoryName)
            fragment.arguments = args
            return fragment
        }
    }
    private fun setupProductsRecyclerView() {
        productsAdapter = ProductsAdapter()
        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(context, 2) // Grid layout to show products in a grid
            adapter = productsAdapter
        }
    }

    private fun showLoading() {
        binding.baseCategoryProgressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.baseCategoryProgressBar.visibility = View.GONE
    }
}
