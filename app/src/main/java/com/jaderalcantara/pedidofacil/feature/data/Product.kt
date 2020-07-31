package com.jaderalcantara.pedidofacil.feature.data

data class Product(
    val id: Long?,
    val name: String?,
    val description: String?,
    val quantity: Int?,
    val paidPrice: Double?,
    val normalPrice: Double?) {

    fun isPromo(): Boolean{
        if(normalPrice == null || paidPrice == null){
            return false
        }

        return paidPrice != normalPrice
    }
}