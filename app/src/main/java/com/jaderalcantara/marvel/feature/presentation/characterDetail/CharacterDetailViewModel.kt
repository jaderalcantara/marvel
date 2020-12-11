package com.jaderalcantara.marvel.feature.presentation.characterDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaderalcantara.marvel.feature.data.api.CharacterResponse
import com.jaderalcantara.marvel.feature.domain.FavoritesHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
        private val favoritesHandler: FavoritesHandler,
        private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    lateinit var character: CharacterResponse

    fun favorite(character: CharacterResponse){
        character.isFavorite = true
        viewModelScope.launch(defaultDispatcher) {
            favoritesHandler.addFavorite(character);
        }
    }

    fun removeFavorite(character: CharacterResponse){
        character.isFavorite = false
        viewModelScope.launch(defaultDispatcher) {
            favoritesHandler.removeFavorite(character)
        }
    }
}