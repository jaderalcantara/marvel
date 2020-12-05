package com.jaderalcantara.marvel.feature.presentation.characterDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.jaderalcantara.marvel.feature.data.CharacterRepository
import com.jaderalcantara.marvel.feature.data.CharacterResponse
import com.jaderalcantara.marvel.feature.data.DataCharacterResponse
import com.jaderalcantara.marvel.infra.ImageHelper
import com.jaderalcantara.marvel.infra.MarvelApplication
import com.jaderalcantara.marvel.infra.request.StateData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.function.Consumer

class CharacterDetailViewModel : ViewModel(), KoinComponent {

    lateinit var character: CharacterResponse
    private val repository: CharacterRepository by inject()
    private val defaultDispatcher: CoroutineDispatcher by inject()
    private val imageHelper: ImageHelper by inject()

    fun favorite(character: CharacterResponse){
        character.isFavorite = true
        viewModelScope.launch(defaultDispatcher) {
            val imageBytes: ByteArray = imageHelper.downloadImage(character.thumbnail.path + "." + character.thumbnail.extension)
            repository.addFavorite(character, imageBytes);
        }
    }

    fun removeFavorite(character: CharacterResponse){
        character.isFavorite = false
        viewModelScope.launch(defaultDispatcher) {
            repository.removeFavorite(character)
        }
    }
}