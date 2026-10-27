package com.example.ra_app.utils

import com.riesgosra.app.BuildConfig

object ApiKeyProvider {

    fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    fun hasValidKey(): Boolean {
        val key = getApiKey()
        return key.isNotBlank() && key != "dummy_key_or_user_key"
    }
}
