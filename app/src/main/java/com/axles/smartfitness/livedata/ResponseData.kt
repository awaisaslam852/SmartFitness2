package com.axles.smartfitness.livedata

sealed class ResponseData<T>(val data: T?, val error: Throwable?) {

    class SuccessResponse<T>(data: T) : ResponseData<T>(data, null)
    class ErrorResponse<Any>(error: Throwable?) : ResponseData<Any>(null, error)
}
