package com.jaderalcantara.marvel.infra.request

import com.jaderalcantara.marvel.infra.WifiService
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if(response.networkResponse == null && response.cacheResponse == null){
            if (!WifiService.instance.isOnline()) {
                throw IOException("No internet connection")
            }
        }
        return response
    }
}