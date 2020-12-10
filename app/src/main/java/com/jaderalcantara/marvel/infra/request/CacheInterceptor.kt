package com.jaderalcantara.marvel.infra.request

import com.jaderalcantara.marvel.infra.WifiService
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class CacheInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newBuilder = chain.request().newBuilder()
        if (WifiService.instance.isOnline()) {
            val maxAge = 60
            newBuilder.addHeader("Cache-Control", "public, max-age=$maxAge")
        } else {
            val maxStale = 60 * 60 * 24 * 7
            newBuilder.addHeader(
                "Cache-Control",
                "public, only-if-cached, max-stale=$maxStale"
            )
        }
        return chain.proceed(newBuilder.build())
    }
}