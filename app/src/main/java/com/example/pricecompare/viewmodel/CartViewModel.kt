package com.example.pricecompare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pricecompare.data.CartProduct
import com.example.pricecompare.firebase.FirebaseCommon
import com.example.pricecompare.utils.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CartViewModel(
    private val firestore: FirebaseFirestore,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {
    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    val productsPrice = cartProducts.map { resource ->
        when (resource) {
            is Resource.Success -> calculatePrice(resource.data!!)
            else -> null
        }
    }

    private fun calculatePrice(data: List<CartProduct>): Map<String, Float> {
        return data.groupBy { it.shop }
            .mapValues { (_, products) ->
                products.sumByDouble { cartProduct ->
                    (cartProduct.product.price * cartProduct.quantity).toDouble()
                }.toFloat()
            }
    }

    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            firestore.collection("cart")
                .document(documentId).delete()
        }
    }

    init {
        getCartProducts()
    }

    private fun getCartProducts() {
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }
        firestore.collection("cart")
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Success(cartProducts))
                    }
                }
            }
    }

    fun changeQuantity(cartProduct: CartProduct, quantityChanging: FirebaseCommon.QuantityChanging) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            val newQuantity = when (quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE -> cartProduct.quantity + 1
                FirebaseCommon.QuantityChanging.DECREASE -> cartProduct.quantity - 1
            }

            if (newQuantity > 0) {
                firestore.collection("cart")
                    .document(documentId)
                    .update("quantity", newQuantity)
            } else {
                viewModelScope.launch {
                    _deleteDialog.emit(cartProduct)
                }
            }
        }
    }
}
