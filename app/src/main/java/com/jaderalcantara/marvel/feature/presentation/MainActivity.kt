package com.jaderalcantara.marvel.feature.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaderalcantara.marvel.R
import com.jaderalcantara.marvel.feature.data.CharacterResponse
import com.jaderalcantara.marvel.feature.data.DataCharacterResponse
import com.jaderalcantara.marvel.feature.presentation.all.AllFragment
import com.jaderalcantara.marvel.feature.presentation.favorites.FavoritesFragment
import com.jaderalcantara.marvel.infra.request.StateData
import com.jaderalcantara.marvel.infra.request.Status.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.container, AllFragment.newInstance()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.all -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, AllFragment.newInstance()).commit()
                true
            }
            R.id.favorites -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, FavoritesFragment.newInstance()).commit()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}