package com.jaderalcantara.marvel.infra

import androidx.room.Room
import com.jaderalcantara.marvel.feature.data.CharacterRemoteDataSource
import com.jaderalcantara.marvel.feature.data.CharacterRepository
import com.jaderalcantara.marvel.feature.data.LocalDataSource
import com.jaderalcantara.marvel.feature.domain.CharactersHandler
import com.jaderalcantara.marvel.feature.domain.FavoritesHandler
import com.jaderalcantara.marvel.feature.presentation.all.AllViewModel
import com.jaderalcantara.marvel.feature.presentation.characterDetail.CharacterDetailViewModel
import com.jaderalcantara.marvel.feature.presentation.favorites.FavoritesViewModel
import com.jaderalcantara.marvel.infra.database.AppDatabase
import com.jaderalcantara.marvel.infra.request.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object KoinModules {
    val appModule = module {
        single { CharacterRepository(get(), get()) }
        single { CharactersHandler(get()) }
        single { FavoritesHandler(get(), get()) }
        single { RetrofitBuilder.getRetrofit(get()).create(CharacterRemoteDataSource::class.java) }
        factory { Dispatchers.IO }
        single { Room.databaseBuilder(
                    get(),
                    AppDatabase::class.java, "marvel-db"
                ).build() as AppDatabase }
        single { LocalDataSource(get()) }
        factory { ImageHelper(get()) }
        viewModel { AllViewModel(get(), get() ,get()) }
        viewModel { CharacterDetailViewModel(get() ,get()) }
        viewModel { FavoritesViewModel(get(), get()) }
    }
}