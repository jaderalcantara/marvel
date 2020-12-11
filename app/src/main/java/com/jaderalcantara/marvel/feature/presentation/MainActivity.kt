package com.jaderalcantara.marvel.feature.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.jaderalcantara.marvel.R
import com.jaderalcantara.marvel.feature.presentation.all.AllFragment
import com.jaderalcantara.marvel.feature.presentation.favorites.FavoritesFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragment = supportFragmentManager.findFragmentByTag("tag")
        if(fragment == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, AllFragment.newInstance(), "tag").commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.all -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, AllFragment.newInstance(), "tag").commit()
                true
            }
            R.id.favorites -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, FavoritesFragment.newInstance(), "tag").commit()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}