package com.example.grayscale3.Classes

sealed class ScreenState<T> (val data: T? = null, val message: String? =  null, val message2: String? = null) {

    class Success<T>(data : T) : ScreenState<T>(data)

    class Loading<T>(data: T? = null): ScreenState<T>(data)

    class Error<T>(data: T? = null, message: String?, message2: String? = null) : ScreenState<T>(data, message)

}