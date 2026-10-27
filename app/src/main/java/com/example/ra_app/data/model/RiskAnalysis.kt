package com.example.ra_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "risk_analyses")
data class RiskAnalysis(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val confidence: Float,
    val description: String,
    val suggestionsJson: String,
    val timestamp: Long = System.currentTimeMillis(),
    val photoPath: String? = null,
    val isSynced: Boolean = false
) {
    fun getSuggestionsList(): List<String> {
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(suggestionsJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getCategoryEnum(): RiskCategory {
        return RiskCategory.fromKey(category)
    }

    companion object {
        fun createSuggestionsJson(suggestions: List<String>): String {
            return Gson().toJson(suggestions)
        }
    }
}
