package com.jaderalcantara.pedidofacil.feature.presentation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import com.jaderalcantara.pedidofacil.feature.data.Product
import com.jaderalcantara.pedidofacil.feature.data.ProductRepository

class MainViewModel : ViewModel {

    private var repository: ProductRepository
    private var cart: HashMap<Long, Int> = HashMap()

    @VisibleForTesting
    constructor(repository: ProductRepository, cart: HashMap<Long, Int>){
        this.repository = repository
        this.cart = cart
    }

    constructor(){
        repository = ProductRepository
    }

    fun loadProducts(): List<Product> {
        return repository.loadProducts()
    }

    fun addToCart(product: Product, quantity: Int){
        product.id?.let {
            cart[it] = quantity
        }
    }

    fun productIsAdded(item: Product): Boolean {
        return cart.containsKey(item.id)
    }

    fun getQuantityFromCart(item: Product): Int? {
        return cart[item.id]
    }
}