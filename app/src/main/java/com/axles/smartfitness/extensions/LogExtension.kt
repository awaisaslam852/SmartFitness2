package com.axles.smartfitness.extensions

import android.util.Log
import com.axles.smartfitness.BuildConfig

fun Any.logD(message:String){
    if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, message)
}

fun Any.logE(errorMessage: String){
    if (BuildConfig.DEBUG) Log.e(this::class.java.simpleName, errorMessage)
}

fun List<Any>.logAll(){
    if (BuildConfig.DEBUG){
        this.forEach {
            logD("item : $it")
        }
    }
}

fun Any.logIt(){
    if (BuildConfig.DEBUG){
        logD("Object of type :[${this::class.java.simpleName}] value: $this")
    }
}