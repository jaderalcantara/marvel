package com.jaderalcantara.marvel.infra

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule


@GlideModule
open class AppGlideModule : AppGlideModule(){
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        val memoryCacheSizeBytes: Long = 1024 * 1024 * 20
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, memoryCacheSizeBytes))
    }
}