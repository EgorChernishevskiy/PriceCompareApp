package com.example.pricecompare.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.pricecompare.R
import com.example.pricecompare.adapters.BestProductsAdapter
import com.example.pricecompare.databinding.FragmentMainCategoryBinding
import com.example.pricecompare.utils.Resource
import com.example.pricecompare.viewmodel.MainCategotyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.pricecompare.viewmodel.factory.MainCategoryViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pricecompare.adapters.ProductDetailsAdapter
import com.example.pricecompare.firebase.FirebaseCommon
import com.example.pricecompare.viewmodel.ProductDetailsViewModel


private val TAG = "MainCategoryFragment"

class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: BestProductsAdapter
    private lateinit var viewModel: MainCategotyViewModel



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater)

        val firestore = FirebaseFirestore.getInstance()
        val firebaseCommon = FirebaseCommon(firestore)

        val factory = MainCategoryViewModelFactory(firestore, firebaseCommon)
        viewModel = ViewModelProvider(this, factory).get(MainCategotyViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()

        specialProductsAdapter.onClick = {
            val b = Bundle().apply { putString("productName", it.name) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, b)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.specialProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.mainCategoryProgressBarBot.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            specialProductsAdapter.differ.submitList(it.data)
                            binding.mainCategoryProgressBarBot.visibility = View.GONE
                        }
                        is Resource.Error -> {
                            Log.e(TAG, it.message.toString())
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            binding.mainCategoryProgressBarBot.visibility = View.GONE
                        }
                        else -> Unit
                    }
                }
            }
        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{v, _, scrollY,_,_ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.fetchSpecialProducts()
            }
        })
    }

    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility = View.VISIBLE
    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter = BestProductsAdapter(viewModel)
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(context, 2) // Используем GridLayoutManager для отображения двух элементов в строке
            adapter = specialProductsAdapter
        }
    }
}
