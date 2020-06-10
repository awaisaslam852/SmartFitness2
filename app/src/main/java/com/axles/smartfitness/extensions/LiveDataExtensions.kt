package com.axles.smartfitness.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.axles.smartfitness.livedata.ResponseData

fun <T> LiveData<ResponseData<T>>.observeWrapper(
    lifecycleOwner: LifecycleOwner,
    onObserve: (T) -> Unit,
    onError: (Throwable?) -> Unit = {}
) {
    this.observe(lifecycleOwner, Observer { response ->
        when (response) {
            is ResponseData.SuccessResponse -> response.data?.let { it -> onObserve(it) }
                ?: onError(Throwable("Unexpected"))
            is ResponseData.ErrorResponse -> onError(response.error)
        }
    })
}

fun <T> LiveData<T>.observe(
    lifecycleOwner: LifecycleOwner,
    onObserve: (T) -> Unit
) {
    observe(lifecycleOwner, Observer {
        this.value ?: return@Observer
        onObserve(this.value!!)
    })
}