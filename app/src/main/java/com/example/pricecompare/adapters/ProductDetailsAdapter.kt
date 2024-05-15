package com.example.pricecompare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pricecompare.data.Product
import com.example.pricecompare.databinding.ProductDetailsCardBinding
import com.example.pricecompare.databinding.ProductRvItemBinding
import com.example.pricecompare.databinding.SpecialRvItemBinding
import android.view.View
import android.widget.TextView
import com.example.pricecompare.R


class ProductDetailsAdapter() : RecyclerView.Adapter<ProductDetailsAdapter.ProductDetailsViewHolder>() {

    inner class ProductDetailsViewHolder(private val binding: ProductDetailsCardBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imageItem)
                tvProductName.text = product.name
                tvProductShop.text = product.shop
                tvProductPrice.text = product.price.toString()
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailsAdapter.ProductDetailsViewHolder {
        return ProductDetailsViewHolder(
            ProductDetailsCardBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
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


    override fun onBindViewHolder(holder: ProductDetailsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}

