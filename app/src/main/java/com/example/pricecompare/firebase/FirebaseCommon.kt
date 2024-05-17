package com.example.pricecompare.firebase

import com.example.pricecompare.data.CartProduct
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.lang.Exception

class FirebaseCommon(
    private val firestore: FirebaseFirestore
)
{
    private val cartCollection = firestore.collection("cart")

    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit){
        cartCollection.document().set(cartProduct).addOnSuccessListener {
            onResult(cartProduct, null)
        }.addOnFailureListener{
            onResult(null, it)
        }
    }

    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction{ transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef,newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener{
            onResult(null, it)
        }
    }
}