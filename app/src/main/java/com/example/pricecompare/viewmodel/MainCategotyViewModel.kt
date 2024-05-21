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

class MainCategotyViewModel(
    private val firestore: FirebaseFirestore,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val pagingInfo = pagingInfo()

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
        fetchSpecialProducts()
    }

    fun fetchSpecialProducts() {
        if (!pagingInfo.isPagingEnd) {
            _specialProducts.value = Resource.Loading()

            // Получаем все продукты
            firestore.collection("Product1")
                .limit(pagingInfo.BestProductspage * 10)
                .get()
                .addOnSuccessListener { documents ->
                    val allProducts = documents.documents.map { it.toObject(Product::class.java)!! }
                    pagingInfo.isPagingEnd = allProducts == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = allProducts
                    val categories = allProducts.map { it.category }.distinct()

                    val categoryProducts = mutableListOf<Product>()

                    categories.forEach { category ->
                        val productsByCategory = allProducts.filter { it.category == category }
                            .sortedBy { it.price }
                            .take(10)
                        categoryProducts.addAll(productsByCategory)
                    }

                    _specialProducts.value = Resource.Success(categoryProducts)
                    pagingInfo.BestProductspage++
                }
                .addOnFailureListener {
                    _specialProducts.value = Resource.Error(it.message ?: "Error fetching products")
                }
        }
    }
}

internal data class pagingInfo(
    var BestProductspage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false

)