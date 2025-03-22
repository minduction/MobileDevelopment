package com.example.smalldbapplication.model

data class Star(
    val name:String = "",
    val type:String = "",
    val color:String = "",
    val images:List<String> = emptyList(),
    val id:String = ""
)
