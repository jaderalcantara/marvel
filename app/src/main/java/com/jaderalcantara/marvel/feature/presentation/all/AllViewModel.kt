package com.jaderalcantara.marvel.feature.presentation.all

import androidx.lifecycle.*
import com.jaderalcantara.marvel.feature.data.CharacterRepository
import com.jaderalcantara.marvel.feature.data.CharacterResponse
import com.jaderalcantara.marvel.infra.ImageHelper
import com.jaderalcantara.marvel.infra.LiveEvent
import com.jaderalcantara.marvel.infra.request.StateData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.function.Consumer

class AllViewModel(
        private val repository: CharacterRepository,
        private val defaultDispatcher: CoroutineDispatcher,
        private val imageHelper: ImageHelper
) : ViewModel() {

    private var query : String? = null
    private val limit = 20
    private var offset = 0
    private val characters = ArrayList<CharacterResponse>()

    var disableEndlessLiveData = MutableLiveData<Boolean>()
    val itemSelectedLiveData = LiveEvent<CharacterResponse>()
    val charactersLiveData = MutableLiveData<StateData<List<CharacterResponse>>>()

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        viewModelScope.launch(defaultDispatcher) {
            if(offset == 0) {
                charactersLiveData.postValue(StateData.loading(null))
            }

            try {
                val loadCharacters = query?.let { repository.loadCharacters(it, offset, limit) } ?: repository.loadCharacters(offset, limit)
                loadCharacters.data.results.forEach(Consumer {
                    it.isFavorite = repository.isFavorite(it.id)
                })

                if(offset == 0){
                    characters.clear()
                }

                characters.addAll(loadCharacters.data.results)
                charactersLiveData.postValue(StateData.success(characters))
                offset += limit

                disableEndlessLiveData.postValue(loadCharacters.data.offset > loadCharacters.data.total)

            } catch (ioException: Exception) {
                disableEndlessLiveData.postValue(false)
                ioException.message?.let {
                    charactersLiveData.postValue(StateData.error(null, it))
                } ?: run {
                    charactersLiveData.postValue(StateData.error(null, ioException.toString()))
                }
            }
        }
    }

    fun reloadCharacters(){
        offset = 0
        disableEndlessLiveData.value = true
        loadCharacters()
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

    fun searchCharacter(query: String){
        offset = 0
        this@AllViewModel.query = query
        loadCharacters()
    }

    fun clearSearch() {
        query = null
    }

    fun itemSelected(character: CharacterResponse) {
        itemSelectedLiveData.value = character
    }
}