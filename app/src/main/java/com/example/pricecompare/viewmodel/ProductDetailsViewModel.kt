package com.example.pricecompare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pricecompare.data.CartProduct
import com.example.pricecompare.data.Product
import com.example.pricecompare.firebase.FirebaseCommon
import com.example.pricecompare.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailsViewModel(private val firestore: FirebaseFirestore, private val firebaseCommon: FirebaseCommon, val productName: String?) : ViewModel() {
    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val products: StateFlow<Resource<List<Product>>> = _products
    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateProductInCart(cartProduct: CartProduct){
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        firestore.collection("cart").whereEqualTo("product.id", cartProduct.product.id).get().addOnSuccessListener {
            it.documents.let{
                if (it.isEmpty()){ //add new product
                    addNewProduct(cartProduct)
                }
                else{
                    val product = it.first().toObject(cartProduct::class.java)
                    if (product == cartProduct){ //increase quantity
                        val documentId = it.first().id
                        increaseQuantity(documentId, cartProduct)
                    }
                    else{ //add new product
                        addNewProduct(cartProduct)
                    }
                }
            }
        }.addOnFailureListener{
            viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
        }
    }

    private fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductToCart(cartProduct){ addedProduct, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(addedProduct!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun increaseQuantity(documentId: String, cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId){ _, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(cartProduct))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    init {
        loadProducts()
    }

    fun loadProducts() {
        firestore.collection("Product1")
            .whereEqualTo("name", productName)
            .get()
            .addOnSuccessListener { result ->
                val productsList = result.map { document ->
                    Product(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        category = document.getString("category") ?: "",
                        shop = document.getString("shop") ?: "",
                        price = document.getDouble("price")?.toFloat() ?: 0f,
                        description = document.getString("description"),
                        volume = document.getString("volume"),
                        images = document.get("images") as List<String>? ?: listOf()
                    )
                }
                _products.value = Resource.Success(productsList)
    }
}
}
