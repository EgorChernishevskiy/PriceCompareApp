package com.example.pricecompare.fragments.shopping

import android.os.Bundle
import android.util.Log
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pricecompare.R
import com.example.pricecompare.adapters.ProductDetailsAdapter
import com.example.pricecompare.adapters.ProductsAdapter
import com.example.pricecompare.data.Product
import com.example.pricecompare.databinding.FragmentBaseCategoryBinding
import com.example.pricecompare.databinding.FragmentProductDetailsBinding
import com.example.pricecompare.utils.Resource
import com.example.pricecompare.viewmodel.CategoryViewModel
import com.example.pricecompare.viewmodel.CategoryViewModelFactory
import com.example.pricecompare.viewmodel.ProductDetailsViewModel
import com.example.pricecompare.viewmodel.ProductDetailsViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductDetailsFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var viewModel: ProductDetailsViewModel
    private lateinit var productsAdapter: ProductDetailsAdapter
    private var productName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productName = arguments?.getString("productName")
        val firestore = FirebaseFirestore.getInstance()
        val factory = ProductDetailsViewModelFactory(firestore, productName)
        viewModel = ViewModelProvider(this, factory).get(ProductDetailsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProductDetailsRecyclerView()

        binding.apply {
            tvNameOfTheProduct.text = productName
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.products.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Success -> {
                            productsAdapter.differ.submitList(resource.data)
                            hideLoading()
                        }
                        is Resource.Error -> {
                            hideLoading()
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        binding.nestedScrollProductDetail.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.loadProducts()
            }
        })

    }


    private fun showLoading() {
        binding.productDetailProgressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.productDetailProgressBar.visibility = View.GONE
    }
    private fun setupProductDetailsRecyclerView() {
        productsAdapter = ProductDetailsAdapter()
        binding.rvProductDetails.apply {
            layoutManager = GridLayoutManager(context, 1) // Grid layout to show products in a grid
            adapter = productsAdapter
        }
    }

}

//class ProductDetailsFragment : Fragment() {
//
//    private lateinit var viewModel: ProductDetailsViewModel
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: ProductDetailsAdapter
//    private lateinit var productName: String
//    private lateinit var firestore: FirebaseFirestore
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_product_details, container, false)
//        recyclerView = view.findViewById(R.id.rvProductDetails)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//
//        // Получаем имя продукта из аргументов
//        productName = arguments?.getString("productName") ?: ""
//
//        // Инициализация Firestore
//        firestore = FirebaseFirestore.getInstance()
//
//        loadProducts()
//
//        return view
//    }
//
//    private fun loadProducts() {
//        firestore.collection("products")
//            .whereEqualTo("name", productName)
//            .get()
//            .addOnSuccessListener { result ->
//                val products = result.toObjects(Product::class.java)
//                adapter = ProductDetailsAdapter(products)
//                recyclerView.adapter = adapter
//            }
//            .addOnFailureListener { exception ->
//                Log.w("ProductDetailsFragment", "Error getting documents: ", exception)
//            }
//    }
//}

//class ProductDetailsFragment : Fragment() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: ProductDetailsAdapter
//    private lateinit var productName: String
//    private lateinit var firestore: FirebaseFirestore
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_product_details, container, false)
//        recyclerView = view.findViewById(R.id.rvProductDetails)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//
//        // Получаем имя продукта из аргументов
//        productName = arguments?.getString("productName") ?: ""
//
//        // Инициализация Firestore
//        firestore = FirebaseFirestore.getInstance()
//
//        loadProducts()
//
//        return view
//    }
//
//    private fun loadProducts() {
//        firestore.collection("products")
//            .whereEqualTo("name", productName)
//            .get()
//            .addOnSuccessListener { result ->
//                val products = result.toObjects(Product::class.java)
//                adapter = ProductDetailsAdapter()
//                recyclerView.adapter = adapter
//            }
//            .addOnFailureListener { exception ->
//                Log.w("ProductDetailsFragment", "Error getting documents: ", exception)
//            }
//    }
//}
