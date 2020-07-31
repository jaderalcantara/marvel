package com.jaderalcantara.pedidofacil

import com.jaderalcantara.pedidofacil.feature.data.Product
import com.jaderalcantara.pedidofacil.feature.presentation.MainViewModel
import com.jaderalcantara.pedidofacil.feature.data.ProductRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class MainViewModelUnitTest {

    lateinit var vm: MainViewModel


    @Test
    fun loadProducts_shouldCallLoadProductsRepository() {
        val spy = spy(ProductRepository::class.java)
        vm = MainViewModel(spy, HashMap<Long, Int>())

        vm.loadProducts()

        verify(spy, times(1)).loadProducts()
    }

    @Test
    fun addToCart_producHasId_shouldAddProductToCart() {
        val mock = mock(ProductRepository::class.java)
        val hashMap = HashMap<Long, Int>()
        val product = Product(1, "Pepsi Lata ", "120ML CAN", 1, 10.20, 10.60)
        vm = MainViewModel(mock, hashMap)

        vm.addToCart(product, 2)

        Assert.assertEquals(2, hashMap[product.id])
    }

    @Test
    fun getQuantityFromCart_CartHasProduct_shouldReturnQuantity() {
        val mock = mock(ProductRepository::class.java)
        val hashMap: HashMap<Long, Int> = HashMap()
        val product = Product(1, "Pepsi Lata ", "120ML CAN", 1, 10.20, 10.60)
        hashMap[product.id!!] = 2

        vm = MainViewModel(mock, hashMap)

        val quantityFromCart = vm.getQuantityFromCart(product)

        Assert.assertEquals(2, quantityFromCart)
    }

    @Test
    fun productIsAdded_CartHasProduct_shouldReturnQuantity() {
        val mock = mock(ProductRepository::class.java)
        val hashMap: HashMap<Long, Int> = HashMap()
        val product = Product(1, "Pepsi Lata ", "120ML CAN", 1, 10.20, 10.60)
        hashMap[product.id!!] = 2

        vm = MainViewModel(mock, hashMap)

        assert(vm.productIsAdded(product))
    }

}