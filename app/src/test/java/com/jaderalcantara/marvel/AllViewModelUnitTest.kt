package com.jaderalcantara.marvel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.jaderalcantara.marvel.feature.data.*
import com.jaderalcantara.marvel.feature.presentation.all.AllViewModel
import com.jaderalcantara.marvel.infra.ImageHelper
import com.jaderalcantara.marvel.infra.request.StateData
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.rules.TestRule
import org.koin.test.KoinTest
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.io.IOException
import java.lang.Exception

@ExperimentalCoroutinesApi
class AllViewModelUnitTest : KoinTest {

    lateinit var vm: AllViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = CoroutineTestRule()

    @Test
    fun loadCharacters_shouldCallLoadCharactersRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        given(repository.loadCharacters(0,20)).willReturn(mock(CharactersResponse::class.java))

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        vm.loadCharacters()

        verify(repository, times(1)).loadCharacters(0, 20)
    }

    @Test
    fun loadCharacters_OffsetZero_shouldEmitLoadingStatus() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        given(repository.loadCharacters(0,20)).willReturn(mock(CharactersResponse::class.java))

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()

        verify(observer).onChanged(
            StateData.loading(null)
        )
    }

    @Test
    fun loadCharacters_OffsetNotZero_shouldNotEmitLoadingStatus() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val success = mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(repository.loadCharacters(0,20)).willReturn(success)
        given(repository.loadCharacters(20,20)).willReturn(success)

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()
        vm.loadCharacters()

        verify(observer, times(1)).onChanged(
            StateData.loading(null)
        )
    }

    @Test
    fun loadCharacters_SuccessReturn_shouldEmitSuccessStatusWithData() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val success = mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(repository.loadCharacters(0,20)).willReturn(success)

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()

        verify(observer).onChanged(
            StateData.success(success.data.results)
        )
    }

    @Test
    fun loadCharacters_SuccessReturnNoMoreItems_shouldDisableEndlessScroll() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val success = mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(20,0,10,0, ArrayList()))
        given(repository.loadCharacters(0,20)).willReturn(success)

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<Boolean>>()
        vm.disableEndlessLiveData.observeForever(observer)

        vm.loadCharacters()

        verify(observer).onChanged(true)
    }

    @Test
    fun loadCharacters_ReturnNoInternetConnection_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val e = IOException("No internet connection")
        given(repository.loadCharacters(0,20)).willAnswer{throw e}

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()

        verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun loadCharacters_ReturnGenericError_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val e = Exception("Generic erro")
        given(repository.loadCharacters(0,20)).willAnswer{throw e}

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()

        verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun reloadCharacters_shouldCallWithZeroOffset() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val success = mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(repository.loadCharacters(0,20)).willReturn(success)

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.loadCharacters()
        vm.reloadCharacters()

        verify(repository, times(2)).loadCharacters(0, 20)
    }

    @Test
    fun favorite_shouldCallAddFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val imageHelper = mock(ImageHelper::class.java)
        val byteArray = "Hello".encodeToByteArray()
        given(imageHelper.downloadImage(".")).willReturn(byteArray)

        vm = AllViewModel(repository, coroutineRule.testDispatcher, imageHelper)

        val thumb = CharacterThumbnailResponse("","", null)
        val character = CharacterResponse(0,"", thumb, false)
        vm.favorite(character, false)

        verify(repository, times(1)).addFavorite(character, byteArray)
    }

    @Test
    fun favorite_shouldCallRemoveFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val character = mock(CharacterResponse::class.java)
        vm.favorite(character, true)

        verify(repository, times(1)).removeFavorite(character)
    }

    @Test
    fun searchCharacter_shouldCallLoadCharactersWithQueryRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        given(repository.loadCharacters(0,20)).willReturn(mock(CharactersResponse::class.java))

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")

        verify(repository, times(1)).loadCharacters("query",0, 20)
    }

    @Test
    fun searchCharacter_ReturnSuccess_shouldCallLoadCharactersWithQueryRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        given(repository.loadCharacters(0,20)).willReturn(mock(CharactersResponse::class.java))

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")

        verify(repository, times(1)).loadCharacters("query",0, 20)
    }

    @Test
    fun searchCharacter_SuccessReturn_shouldEmitSuccessStatusWithData() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val success = mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(repository.loadCharacters("query",0,20)).willReturn(success)

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")

        verify(observer).onChanged(
            StateData.loading(null)
        )

        verify(observer).onChanged(
            StateData.success(success.data.results)
        )
    }

    @Test
    fun searchCharacter_ReturnNoInternetConnection_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val e = IOException("No internet connection")
        given(repository.loadCharacters("query",0,20)).willAnswer{throw e}

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")

        verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun searchCharacter_ReturnGenericError_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val e = Exception("Generic erro")
        given(repository.loadCharacters("query",0,20)).willAnswer{throw e}

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")

        verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun loadCharacters_AfterSearch_shouldKeepQuery() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val success = CharactersResponse(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(repository.loadCharacters("query",20,20)).willReturn(success)
        given(repository.loadCharacters("query", 0, 20)).willReturn(success)

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")
        vm.loadCharacters()

        verify(repository, times(1)).loadCharacters("query", 0, 20)
        verify(repository, times(1)).loadCharacters("query", 20, 20)
        verify(repository, times(0)).loadCharacters(anyInt(), anyInt())
    }

    @Test
    fun clearSearch_AfterSearch_shouldClearQuery() = coroutineRule.testDispatcher.runBlockingTest {
        val repository = mock(CharacterRepository::class.java)
        val success = CharactersResponse(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(repository.loadCharacters("query", 0, 20)).willReturn(success)

        vm = AllViewModel(repository, coroutineRule.testDispatcher, mock(ImageHelper::class.java))

        val observer = mock<Observer<StateData<List<CharacterResponse>>>>()
        vm.charactersLiveData.observeForever(observer)

        vm.searchCharacter("query")
        vm.clearSearch()
        vm.loadCharacters()

        verify(repository, times(1)).loadCharacters("query", 0, 20)
        verify(repository, times(1)).loadCharacters(anyInt(), anyInt())
    }
}