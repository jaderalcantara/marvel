package com.jaderalcantara.marvel.infra

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.nio.charset.StandardCharsets

class ImageHelper(val context: Context) {

    fun loadImage(url: String, view: ImageView){
        GlideApp.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)
    }

    fun loadImageBase64(base64: String, view: ImageView){
        GlideApp.with(context)
            .load(Base64.decode(base64, Base64.DEFAULT))
            .into(view)
    }

    fun downloadImage(url: String) : ByteArray{
        return Glide.with(context).`as`<ByteArray>(ByteArray::class.java)
            .load(url)
            .submit()
            .get()
    }

    fun bytesToBase64(imageBytes: ByteArray): String {
        return String(java.util.Base64.getEncoder().encode(imageBytes), StandardCharsets.UTF_8)
    }
}