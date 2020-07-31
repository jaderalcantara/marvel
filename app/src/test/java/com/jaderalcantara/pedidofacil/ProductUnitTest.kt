package com.jaderalcantara.pedidofacil

import com.jaderalcantara.pedidofacil.feature.data.Product
import org.junit.Test

import org.junit.Assert.*

class ProductUnitTest {

    @Test
    fun isPromo_samePaidAndNormalPrice_shouldReturnFalse() {
        val product = Product(1, "product", "description", 1, 10.5, 10.5)
        assert(product.isPromo())
    }

    @Test
    fun isPromo_differentPaidAndNormalPrice_shouldReturnFalse() {
        val product = Product(1, "product", "description", 1, 10.5, 13.70)
        assertFalse(product.isPromo())
    }

    @Test
    fun isPromo_normalPriceIsNull_shouldReturnFalse() {
        val product = Product(1, "product", "description", 1, 10.5, null)
        assertFalse(product.isPromo())
    }

    @Test
    fun isPromo_paidPriceIsNull_shouldReturnFalse() {
        val product = Product(1, "product", "description", 1, null, null)
        assertFalse(product.isPromo())
    }
}