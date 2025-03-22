package com.example.smalldbapplication.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val age: Int = 18,
    val gender: String = "",
    val city: String = "",
    val country: String = "",
    val bio: String = "",
    val interests: String = "",
    val avatarUrl: String = "",
    val isPublic: Boolean = true

)