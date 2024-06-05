package com.example.pricecompare.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pricecompare.R
import com.example.pricecompare.adapters.CartProductAdapter
import com.example.pricecompare.data.CartProduct
import com.example.pricecompare.databinding.FragmentCartBinding
import com.example.pricecompare.firebase.FirebaseCommon
import com.example.pricecompare.utils.Resource
import com.example.pricecompare.utils.VerticalItemDecoration
import com.example.pricecompare.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

class CartFragment : Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.layoutCartEmpty.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        if (resource.data!!.isEmpty()) {
                            showEmptyCart()
                        } else {
                            hideEmptyCart()
                            val shopProductMap = resource.data.groupBy { it.shop }
                            createCartViews(shopProductMap)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Удалить продукт из корзины")
                    setMessage("Вы хотите удалить продукт из корзины?")
                    setNegativeButton("Отмена") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Да") { dialog, _ ->
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }
    }

    private fun createCartViews(shopProductMap: Map<String, List<CartProduct>>) {
        binding.linearLayoutCartContainers.removeAllViews()
        for ((shop, products) in shopProductMap) {
            val shopContainer = LayoutInflater.from(requireContext()).inflate(R.layout.shop_cart_container, null)
            val recyclerView = shopContainer.findViewById<RecyclerView>(R.id.rvCart)
            val totalTextView = shopContainer.findViewById<TextView>(R.id.tvTotalPrice)

            val cartAdapter = CartProductAdapter(shop)
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = cartAdapter
                addItemDecoration(VerticalItemDecoration())
            }
            cartAdapter.differ.submitList(products)

            cartAdapter.onProductClick = { cartProduct ->
                val b = Bundle().apply { putString("productName", cartProduct.product.name) }
                findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, b)
            }

            cartAdapter.onPlusClick = { cartProduct ->
                viewModel.changeQuantity(cartProduct, FirebaseCommon.QuantityChanging.INCREASE)
            }

            cartAdapter.onMinusClick = { cartProduct ->
                viewModel.changeQuantity(cartProduct, FirebaseCommon.QuantityChanging.DECREASE)
            }

            val total = products.sumOf { it.product.price.toDouble() * it.quantity }
            totalTextView.text = "Итого: RUB ${String.format("%.2f", total)}"

            binding.linearLayoutCartContainers.addView(shopContainer)
        }
    }

    private fun showEmptyCart() {
        binding.layoutCartEmpty.visibility = View.VISIBLE
    }

    private fun hideEmptyCart() {
        binding.layoutCartEmpty.visibility = View.GONE
    }
}
