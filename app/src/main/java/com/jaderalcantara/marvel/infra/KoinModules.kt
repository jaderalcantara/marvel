package com.jaderalcantara.marvel.infra

import androidx.room.Room
import com.jaderalcantara.marvel.feature.data.CharacterApiService
import com.jaderalcantara.marvel.feature.data.CharacterRepository
import com.jaderalcantara.marvel.feature.data.LocalDataSource
import com.jaderalcantara.marvel.infra.database.AppDatabase
import com.jaderalcantara.marvel.infra.request.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

object KoinModules {
    val appModule = module {
        single { CharacterRepository() }
        single { RetrofitBuilder.getRetrofit().create(CharacterApiService::class.java) }
        factory { Dispatchers.IO }
        single { Room.databaseBuilder(
                    get(),
                    AppDatabase::class.java, "marvel-db"
                ).build() as AppDatabase }
        single { LocalDataSource() }
        factory { ImageHelper(get()) }
    }
}