package com.jaderalcantara.marvel.feature.presentation.favorites

import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.jaderalcantara.marvel.feature.data.CharacterRepository
import com.jaderalcantara.marvel.feature.data.CharacterResponse
import com.jaderalcantara.marvel.feature.data.DataCharacterResponse
import com.jaderalcantara.marvel.infra.MarvelApplication
import com.jaderalcantara.marvel.infra.request.StateData
import com.jaderalcantara.marvel.infra.request.Status
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.function.Consumer

class FavoritesViewModel(
        private val repository: CharacterRepository,
        private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val characters = ArrayList<CharacterResponse>()
    val charactersLiveData = MutableLiveData<StateData<List<CharacterResponse>>>()

    fun loadCharacters(query : String? = null){
        viewModelScope.launch(defaultDispatcher) {
            charactersLiveData.postValue(StateData.loading(null))
            try {
                val loadCharacters = query?.let { repository.loadFavorites(query) } ?: repository.loadFavorites()
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
            repository.removeFavorite(character)
            characters.remove(character)
            if(characters.isEmpty()){
                charactersLiveData.postValue(StateData.success(characters))
            }
        }
    }
}