package com.example.pricecompare.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pricecompare.R
import com.example.pricecompare.adapters.ProductSearchAdapter
import com.example.pricecompare.adapters.ProductsAdapter
import com.example.pricecompare.databinding.FragmentSearchBinding
import com.example.pricecompare.firebase.FirebaseCommon
import com.example.pricecompare.utils.Resource
import com.example.pricecompare.viewmodel.SearchViewModel
import com.example.pricecompare.viewmodel.factory.SearchViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel
    private lateinit var productAdapter: ProductSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firestore = FirebaseFirestore.getInstance()
        val firebaseCommon = FirebaseCommon(firestore)
        val factory = SearchViewModelFactory(firestore, firebaseCommon)
        viewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchRecyclerView()

        productAdapter.onClick = {
            val b = Bundle().apply { putString("productName", it.name) }
            findNavController().navigate(R.id.action_searchFragment_to_productDetailsFragment, b)
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                viewModel.searchProducts(text.toString())
            }
        }

        binding.searchIcon.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchProducts(query)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResults.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Success -> {
                            productAdapter.differ.submitList(resource.data)
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
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun setupSearchRecyclerView() {
        productAdapter = ProductSearchAdapter(viewModel)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
