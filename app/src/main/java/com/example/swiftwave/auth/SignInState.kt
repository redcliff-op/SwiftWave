package com.example.swiftwave.auth

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)