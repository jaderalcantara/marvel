package com.jaderalcantara.marvel.feature.presentation.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaderalcantara.marvel.feature.data.api.CharacterResponse
import com.jaderalcantara.marvel.feature.domain.FavoritesHandler
import com.jaderalcantara.marvel.infra.request.StateData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class FavoritesViewModel(
        private val favoritesHandler: FavoritesHandler,
        private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val characters = ArrayList<CharacterResponse>()
    val charactersLiveData = MutableLiveData<StateData<List<CharacterResponse>>>()

    fun loadCharacters(query : String? = null){
        viewModelScope.launch(defaultDispatcher) {
            charactersLiveData.postValue(StateData.loading(null))
            try {
                val loadCharacters = query?.let { favoritesHandler.loadFavorites(query) } ?: favoritesHandler.loadFavorites()
                characters.clear()
                characters.addAll(loadCharacters.data.results)
                charactersLiveData.postValue(StateData.success(characters))
            } catch (ioException: Exception) {
                ioException.message?.let {
                    charactersLiveData.postValue(StateData.error(null, it))
                } ?: run{
                    charactersLiveData.postValue(StateData.error(null, ioException.toString()))
                }
            }
        }
    }

    fun removeFavorite(character: CharacterResponse){
        viewModelScope.launch(defaultDispatcher) {
            favoritesHandler.removeFavorite(character)
            characters.remove(character)
            if(characters.isEmpty()){
                charactersLiveData.postValue(StateData.success(characters))
            }
        }
    }
}