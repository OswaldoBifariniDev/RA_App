package com.example.ra_app.domain.model

data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    val token: String? = null
)
