package com.jaderalcantara.marvel.infra

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageHelper(val context: Context) {

    fun loadImage(url: String, view: ImageView){
        GlideApp.with(context)
            .load(url)
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
}