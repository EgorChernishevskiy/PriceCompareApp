package com.example.pricecompare.fragments.categories

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pricecompare.R
import com.example.pricecompare.adapters.BestProductsAdapter
import com.example.pricecompare.databinding.FragmentHomeBinding
import com.example.pricecompare.databinding.FragmentMainCategoryBinding
import com.example.pricecompare.utils.Resource
import com.example.pricecompare.viewmodel.MainCategotyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import com.example.pricecompare.viewmodel.MainCategoryViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.GridLayoutManager


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

        // Создание Firestore экземпляра (пример, может быть изменен в зависимости от вашего кода)
        val firestore = FirebaseFirestore.getInstance()

        // Использование фабрики для создания ViewModel
        val factory = MainCategoryViewModelFactory(firestore)
        viewModel = ViewModelProvider(this, factory).get(MainCategotyViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()

        // Используйте repeatOnLifecycle для управления корутинами в зависимости от жизненного цикла
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.specialProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            showLoading()
                        }
                        is Resource.Success -> {
                            specialProductsAdapter.differ.submitList(it.data)
                            hideLoading()
                        }
                        is Resource.Error -> {
                            hideLoading()
                            Log.e(TAG, it.message.toString())
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility = View.VISIBLE
    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(context, 2) // Используем GridLayoutManager для отображения двух элементов в строке
            adapter = specialProductsAdapter
        }
    }
}
