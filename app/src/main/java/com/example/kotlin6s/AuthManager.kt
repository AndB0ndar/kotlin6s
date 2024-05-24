package com.example.kotlin6s

object AuthManager {
    private var authToken: String? = null

    fun saveToken(token: String) {
        authToken = token
    }

    fun getToken(): String? {
        return authToken
    }
}
