package com.jaderalcantara.pedidofacil.feature.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaderalcantara.pedidofacil.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
            I left buttons with 48dp which is recommendable to avoid miss touch on elements
         */
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val loadProducts = viewModel.loadProducts()

        if(list is RecyclerView) {
            with(list) {
                layoutManager = LinearLayoutManager(context)
                adapter =
                    ProductRecyclerViewAdapter(
                        context,
                        viewModel,
                        loadProducts
                    )
            }
        }
    }
}