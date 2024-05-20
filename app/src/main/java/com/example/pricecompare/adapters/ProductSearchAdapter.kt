package com.example.pricecompare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pricecompare.data.CartProduct
import com.example.pricecompare.data.Product
import com.example.pricecompare.databinding.ProductDetailsCardBinding
import com.example.pricecompare.viewmodel.SearchViewModel

class ProductSearchAdapter(private val viewModel: SearchViewModel) : RecyclerView.Adapter<ProductSearchAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ProductDetailsCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, viewModel: SearchViewModel) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imageItem)
                tvProductName.text = product.name
                tvProductShop.text = product.shop
                tvProductPrice.text = product.price.toString()

//                btnAddToCart.setOnClickListener {
//                    val cartProduct = CartProduct(product, 1, product.shop)
//                    viewModel.addUpdateProductInCart(cartProduct)
//                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ProductDetailsCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product, viewModel)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
