package com.jaderalcantara.marvel.feature.presentation.all

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
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

class AllViewModel : ViewModel(), KoinComponent {

    private val repository: CharacterRepository by inject()
    private val defaultDispatcher: CoroutineDispatcher by inject()
    private val imageHelper: ImageHelper by inject()

    private var query : String? = null
    val limit = 20
    var offset = 0;

    fun loadCharacters(): LiveData<StateData<DataCharacterResponse>> = liveData(defaultDispatcher) {
        emit(StateData.loading(null))
        try {
            val loadCharacters = query?.let { repository.loadCharacters(it, offset, limit) } ?: repository.loadCharacters(offset, limit)
            loadCharacters.data.results.forEach(Consumer {
                it.isFavorite = repository.isFavorite(it.id)
            })
            emit(StateData.success(loadCharacters.data))
            offset += limit
        } catch (ioException: Exception) {
            ioException.message?.let {
                emit(StateData.error(null, it))
            } ?: run{
                emit(StateData.error(null, ioException.toString()))
            }

        }
    }

    fun reloadCharacters(): LiveData<StateData<DataCharacterResponse>> {
        offset = 0
        return loadCharacters()
    }

    fun favorite(character: CharacterResponse, actualState: Boolean){
        viewModelScope.launch(defaultDispatcher) {
            if(actualState){
                repository.removeFavorite(character)
            }else{
                val imageBytes: ByteArray = imageHelper.downloadImage(character.thumbnail.path + "." + character.thumbnail.extension)

                repository.addFavorite(character, imageBytes);
            }
        }
    }

    fun searchCharacter(query: String): LiveData<StateData<DataCharacterResponse>> = liveData(defaultDispatcher) {
        offset = 0
        this@AllViewModel.query = query
        emit(StateData.loading(null))
        try {
            val loadCharacters = repository.loadCharacters(query, offset, limit)
            loadCharacters.data.results.forEach(Consumer {
                it.isFavorite = repository.isFavorite(it.id)
            })
            emit(StateData.success(loadCharacters.data))
            offset += limit
        } catch (ioException: Exception) {
            ioException.message?.let {
                emit(StateData.error(null, it))
            } ?: run{
                emit(StateData.error(null, ioException.toString()))
            }

        }
    }

    fun clearSearch() {
        query = null
    }
}