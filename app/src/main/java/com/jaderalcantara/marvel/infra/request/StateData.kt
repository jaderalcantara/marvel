package com.jaderalcantara.marvel.infra.request

data class StateData<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): StateData<T> = StateData(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): StateData<T> =
            StateData(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?): StateData<T> = StateData(status = Status.LOADING, data = data, message = null)
    }
}