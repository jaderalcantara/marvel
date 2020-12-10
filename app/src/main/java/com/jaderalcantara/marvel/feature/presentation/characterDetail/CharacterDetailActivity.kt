package com.jaderalcantara.marvel.feature.presentation.characterDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaderalcantara.marvel.R
import com.jaderalcantara.marvel.feature.data.CharacterResponse
import com.jaderalcantara.marvel.feature.data.ComicResponse
import com.jaderalcantara.marvel.infra.ImageHelper
import kotlinx.android.synthetic.main.activity_character_detail.*
import kotlinx.android.synthetic.main.all_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


class CharacterDetailActivity : AppCompatActivity(), KoinComponent {
    private val viewModel: CharacterDetailViewModel by viewModel()
    private val imageHelper: ImageHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_detail)

        if(intent.hasExtra(extraCharacter)) {
            viewModel.character = intent.getParcelableExtra(extraCharacter)
        }

        supportActionBar?.title = viewModel.character.name.toUpperCase()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.character.description?.let {
            description.text = it
        }

        imageHelper.loadImage(viewModel.character.thumbnail.path + "." + viewModel.character.thumbnail.extension, image)

        viewModel.character.comics?.items?.let {
            if (recyclerview is RecyclerView) {
                with(recyclerview) {
                    layoutManager = LinearLayoutManager(
                        this@CharacterDetailActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = CharacterDetailRecyclerViewAdapter(
                            it as ArrayList<ComicResponse>
                        )
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu?.findItem(R.id.app_bar_switch)
        val rootView = item?.actionView as FrameLayout
        val star = rootView.findViewById<View>(R.id.star) as ImageView

        if(viewModel.character.isFavorite){
            star.setImageResource(R.drawable.ic_baseline_star_24)
        }else{
            star.setImageResource(R.drawable.ic_baseline_star_border_24)
        }
        star.invalidate()

        star.setOnClickListener {
            if(viewModel.character.isFavorite){
                star.setImageResource(R.drawable.ic_baseline_star_border_24)
                viewModel.removeFavorite(viewModel.character)
            }else{
                star.setImageResource(R.drawable.ic_baseline_star_24)
                viewModel.favorite(viewModel.character)
            }
            star.invalidate()
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                setResult(RESULT_OK, Intent().putExtra(CHARACTER_DATA_RESULT, viewModel.character))
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object{
        private const val extraCharacter = "EXTRA_CHARACTER"
        const val CHARACTER_DATA_RESULT = "character"
        fun intent(context: Context, character: CharacterResponse): Intent {
            return Intent(context, CharacterDetailActivity::class.java).apply {
                putExtra(extraCharacter, character)
            }
        }
    }
}