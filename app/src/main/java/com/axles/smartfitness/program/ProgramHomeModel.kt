package com.axles.smartfitness.program

data class ProgramHomeModel<T>(val title: String, val date: String){
    var image : T? = null // T - string if url from web or int if resource
}