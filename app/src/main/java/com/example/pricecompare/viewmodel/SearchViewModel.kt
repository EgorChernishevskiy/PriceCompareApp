package com.example.pricecompare.viewmodel

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
import java.util.Locale

class SearchViewModel(private val firestore: FirebaseFirestore,
                      private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _searchResults = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val searchResults: StateFlow<Resource<List<Product>>> get() = _searchResults

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



    fun searchProducts(query: String) {
        val normalizedQuery = normalize(query)
        viewModelScope.launch {
            try {
                firestore.collection("Product1")
                    .get()
                    .addOnSuccessListener { result ->
                        val products = result.documents.mapNotNull { document ->
                            document.toObject(Product::class.java)
                        }
                        val filteredProducts = products.filter { product ->
                            normalize(product.name).contains(normalizedQuery)
                        }
                        _searchResults.value = Resource.Success(filteredProducts)
                    }
                    .addOnFailureListener { exception ->
                        _searchResults.value = Resource.Error(exception.message ?: "Error occurred")
                    }
            } catch (e: Exception) {
                _searchResults.value = Resource.Error(e.message ?: "Error occurred")
            }
        }
    }

    private fun normalize(input: String): String {
        return input.split(" ")
            .map { word -> word.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase(Locale.getDefault()) } }
            .sorted()
            .joinToString(" ")
    }
}





