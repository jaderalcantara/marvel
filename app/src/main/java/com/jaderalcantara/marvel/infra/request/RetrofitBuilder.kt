package com.jaderalcantara.marvel.infra.request

import com.jaderalcantara.marvel.infra.md5
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


object RetrofitBuilder {
    private const val BASE_URL = "https://gateway.marvel.com/"
    private val build: Retrofit

    init {
        build = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient())
            .build()
    }

     fun getRetrofit(): Retrofit {
          return build
    }

    fun getOkHttpClient(): OkHttpClient{
        val builder = Builder()
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(logging)
        builder.addInterceptor { chain ->
            val request = chain.request().newBuilder()
            val originalHttpUrl = chain.request().url

            val ts = System.currentTimeMillis().toString()
            val apikey = "49ab8e7280c9c4fb780342c496f9bef1"
            val hashInput = "$ts${"a55d5017545659490a5e759e2c6dbac6ee5c04bf"}${apikey}"

            val url =
                originalHttpUrl.newBuilder()
                    .addQueryParameter(
                    "apikey",
                    "49ab8e7280c9c4fb780342c496f9bef1")
                    .addQueryParameter("hash",
                        hashInput.md5())
                    .addQueryParameter("ts",
                        ts)
                    .build()
            request.url(url)
            val response = chain.proceed(request.build())
            return@addInterceptor response
        }

        builder.addInterceptor(ConnectivityInterceptor())
        return builder.build()
    }
}