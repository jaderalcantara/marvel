package com.jaderalcantara.marvel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.bumptech.glide.RequestManager
import com.jaderalcantara.marvel.feature.data.*
import com.jaderalcantara.marvel.feature.presentation.all.AllViewModel
import com.jaderalcantara.marvel.infra.ImageHelper
import com.jaderalcantara.marvel.infra.request.StateData
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.rules.TestRule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.mockito.internal.matchers.Any
import java.io.IOException
import java.lang.Exception

@ExperimentalCoroutinesApi
class AllViewModelUnitTest : KoinTest {

    lateinit var vm: AllViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = CoroutineTestRule()

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadCharacters_shouldCallLoadCharactersRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        given(mock.loadCharacters(0,20)).willReturn(mock(CharactersResponse::class.java))
        val appModule = module {
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
        }
        startKoin { modules(appModule) }

        vm = AllViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.loadCharacters().observeForever(observer)

        verify(mock, times(1)).loadCharacters(0, 20)
    }

    @Test
    fun loadCharacters_shouldEmitLoadingStatus() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        given(mock.loadCharacters(0,20)).willReturn(mock(CharactersResponse::class.java))
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
        }
        startKoin { modules(appModule) }
        vm = AllViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.loadCharacters().observeForever(observer)

        verify(observer).onChanged(
            StateData.loading(null)
        )
    }

    @Test
    fun loadCharacters_SuccessReturn_shouldEmitSuccessStatusWithData() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val success = mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(mock.loadCharacters(0,20)).willReturn(success)
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
        }
        startKoin { modules(appModule) }
        vm = AllViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.loadCharacters().observeForever(observer)

        verify(observer).onChanged(
            StateData.success(success.data)
        )
    }

    @Test
    fun loadCharacters_ReturnNoInternetConnection_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val e = IOException("No internet connection")
        given(mock.loadCharacters(0,20)).willAnswer{throw e}
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
        }
        startKoin { modules(appModule) }
        vm = AllViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.loadCharacters().observeForever(observer)

        verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun loadCharacters_ReturnGenericError_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val e = Exception("Generic erro")
        given(mock.loadCharacters(0,20)).willAnswer{throw e}
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
        }
        startKoin { modules(appModule) }
        vm = AllViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.loadCharacters().observeForever(observer)

        verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun reloadCharacters_shouldCallWithZeroOffset() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val success = mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(mock.loadCharacters(0,20)).willReturn(success)

        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
        }
        startKoin { modules(appModule) }
        vm = AllViewModel()
        vm.offset = 100

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.reloadCharacters().observeForever(observer)

        verify(mock, times(1)).loadCharacters(0, 20)
    }

    @Test
    fun favorite_shouldCallAddFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val imageHelper = mock(ImageHelper::class.java)
        val byteArray = "Hello".encodeToByteArray()
        given(imageHelper.downloadImage(".")).willReturn(byteArray)
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
            factory { imageHelper }
        }
        startKoin { modules(appModule) }
        vm = AllViewModel()

        val thumb = CharacterThumbnailResponse("","", null)
        val character = CharacterResponse(0,"", thumb, false)
        vm.favorite(character, false)

        verify(mock, times(1)).addFavorite(character, byteArray)
    }

    @Test
    fun favorite_shouldCallRemoveFavorite() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
            factory {  mock(ImageHelper::class.java) }
        }
        startKoin { modules(appModule) }
        vm = AllViewModel()

        val character = mock(CharacterResponse::class.java)
        vm.favorite(character, true)

        verify(mock, times(1)).removeFavorite(character)
    }

    @Test
    fun searchCharacter_shouldCallLoadCharactersWithQueryRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        given(mock.loadCharacters(0,20)).willReturn(mock(CharactersResponse::class.java))
        val appModule = module {
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
        }
        startKoin { modules(appModule) }

        vm = AllViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.searchCharacter("query").observeForever(observer)

        verify(mock, times(1)).loadCharacters("query",0, 20)
    }

    @Test
    fun searchCharacter_ReturnSuccess_shouldCallLoadCharactersWithQueryRepository() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        given(mock.loadCharacters(0,20)).willReturn(mock(CharactersResponse::class.java))
        val appModule = module {
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
        }
        startKoin { modules(appModule) }

        vm = AllViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.searchCharacter("query").observeForever(observer)

        verify(mock, times(1)).loadCharacters("query",0, 20)
    }

    @Test
    fun searchCharacter_SuccessReturn_shouldEmitSuccessStatusWithData() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val success = mock(CharactersResponse::class.java)
        given(success.data).willReturn(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(mock.loadCharacters("query",0,20)).willReturn(success)
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
        }
        startKoin { modules(appModule) }
        vm = AllViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.searchCharacter("query").observeForever(observer)

        verify(observer).onChanged(
            StateData.loading(null)
        )

        verify(observer).onChanged(
            StateData.success(success.data)
        )
    }

    @Test
    fun searchCharacter_ReturnNoInternetConnection_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val e = IOException("No internet connection")
        given(mock.loadCharacters("query",0,20)).willAnswer{throw e}
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
        }
        startKoin { modules(appModule) }
        vm = AllViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.searchCharacter("query").observeForever(observer)

        verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun searchCharacter_ReturnGenericError_shouldEmitErrorWithMessage() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val e = Exception("Generic erro")
        given(mock.loadCharacters("query",0,20)).willAnswer{throw e}
        val appModule = module {
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
        }
        startKoin { modules(appModule) }
        vm = AllViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.searchCharacter("query").observeForever(observer)

        verify(observer).onChanged(
            StateData.error(null, e.message!!)
        )
    }

    @Test
    fun loadCharacters_AfterSearch_shouldKeepQuery() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val success = CharactersResponse(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(mock.loadCharacters("query",20,20)).willReturn(success)
        given(mock.loadCharacters("query", 0, 20)).willReturn(success)
        val appModule = module {
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
        }
        startKoin { modules(appModule) }

        vm = AllViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.searchCharacter("query").observeForever(observer)
        vm.loadCharacters().observeForever(observer)

        verify(mock, times(1)).loadCharacters("query", 0, 20)
        verify(mock, times(1)).loadCharacters("query", 20, 20)
        verify(mock, times(0)).loadCharacters(anyInt(), anyInt())
    }

    @Test
    fun clearSearch_AfterSearch_shouldClearQuery() = coroutineRule.testDispatcher.runBlockingTest {
        val mock = mock(CharacterRepository::class.java)
        val success = CharactersResponse(DataCharacterResponse(0,0,0,0, ArrayList()))
        given(mock.loadCharacters("query", 0, 20)).willReturn(success)
        val appModule = module {
            factory { coroutineRule.testDispatcher as CoroutineDispatcher }
            factory { mock }
            factory { mock(CharacterApiService::class.java) }
        }
        startKoin { modules(appModule) }

        vm = AllViewModel()

        val observer = mock<Observer<StateData<DataCharacterResponse>>>()
        vm.searchCharacter("query").observeForever(observer)
        vm.clearSearch()
        vm.loadCharacters().observeForever(observer)

        verify(mock, times(1)).loadCharacters("query", 0, 20)
        verify(mock, times(1)).loadCharacters(anyInt(), anyInt())
    }
}