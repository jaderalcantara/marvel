package com.jaderalcantara.pedidofacil.feature.data.dummy

import com.jaderalcantara.pedidofacil.feature.data.Product
import java.util.ArrayList
import java.util.HashMap

object DummyContent {
    val ITEMS: MutableList<Product> = ArrayList()

    init {
        // Add some sample items.
        ITEMS.add(Product(1, "Pepsi Lata ", "120ML CAN", 1, 10.20, 10.60))
        ITEMS.add(Product(2, "Pepsi Light 24/12ONZ LATA ", "120ML LATA", 2, 603.90, 603.90))
        ITEMS.add(Product(3, "Pepsi Light 24/12ONZ LATA ", "120ML LATA", 3, 603.90, null))
        ITEMS.add(Product(4, "Pepsi Light 24/12ONZ LATA ", "120ML LATA", 4, 603.90, null))
        ITEMS.add(Product(5, "Pepsi Lata ", "120ML CAN", 1, 10.20, 10.60))
        ITEMS.add(Product(6, "Pepsi Light 24/12ONZ LATA ", "120ML LATA", 1, 603.90, null))
        ITEMS.add(Product(7, "Pepsi Light 24/12ONZ LATA ", "120ML LATA", 1, 603.90, null))
        ITEMS.add(Product(8, "Pepsi Light 24/12ONZ LATA ", "120ML LATA", 1, 603.90, null))

    }
}