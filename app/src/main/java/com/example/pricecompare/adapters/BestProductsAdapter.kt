package com.example.pricecompare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pricecompare.data.CartProduct
import com.example.pricecompare.data.Product
import com.example.pricecompare.databinding.SpecialRvItemBinding
import com.example.pricecompare.viewmodel.MainCategotyViewModel
import com.example.pricecompare.viewmodel.ProductDetailsViewModel

class BestProductsAdapter(private val viewModel: MainCategotyViewModel): RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

    inner class BestProductsViewHolder(private val binding: SpecialRvItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(product: Product, viewModel: MainCategotyViewModel){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imageSpecialRvItem)
                tvSpecialProductName.text = product.name
                tvSpecialProductShop.text = product.shop
                tvSpecialProductPrice.text = product.price.toString()

                btnAddToCart.setOnClickListener {
                    val cartProduct = CartProduct(product, 1, product.shop)
                    viewModel.addUpdateProductInCart(cartProduct)
                }
            }

        }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        return BestProductsViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product, viewModel)

        holder.itemView.setOnClickListener{
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick:((Product) -> Unit)? = null
}