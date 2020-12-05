package com.jaderalcantara.marvel.feature.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.jaderalcantara.marvel.feature.data.CharacterRepository
import com.jaderalcantara.marvel.feature.data.CharacterResponse
import com.jaderalcantara.marvel.feature.data.DataCharacterResponse
import com.jaderalcantara.marvel.infra.MarvelApplication
import com.jaderalcantara.marvel.infra.request.StateData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.function.Consumer

class FavoritesViewModel : ViewModel(), KoinComponent {

    private val repository: CharacterRepository by inject()
    private val defaultDispatcher: CoroutineDispatcher by inject()

    fun loadCharacters(query : String? = null): LiveData<StateData<DataCharacterResponse>> = liveData(defaultDispatcher) {
        emit(StateData.loading(null))
        try {
            val loadCharacters = query?.let { repository.loadFavorites(query) } ?: repository.loadFavorites()
            emit(StateData.success(loadCharacters.data))
        } catch (ioException: Exception) {
            ioException.message?.let {
                emit(StateData.error(null, it))
            } ?: run{
                emit(StateData.error(null, ioException.toString()))
            }
        }
    }

    fun removeFavorite(character: CharacterResponse){
        viewModelScope.launch(defaultDispatcher) {
            repository.removeFavorite(character)
        }
    }
}