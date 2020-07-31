package com.jaderalcantara.pedidofacil.feature.data

import com.jaderalcantara.pedidofacil.feature.data.dummy.DummyContent

object ProductRepository {

    /*
      This method we can replace mock items for Retrofit and use suspend
      so we can use coroutines to handle threads. Unfortunately I don't have much time
      cause to create a better example of my code, cause I'm working.
     */
    fun loadProducts(): List<Product>{
        return DummyContent.ITEMS
    }
}