package com.example.ra_app.ui.risk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.riesgosra.app.R

class SuggestionAdapter(
    private var suggestions: List<String> = emptyList()
) : RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {

    fun updateSuggestions(newSuggestions: List<String>) {
        suggestions = newSuggestions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }

    override fun getItemCount(): Int = suggestions.size

    class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtAction: TextView = itemView.findViewById(R.id.txt_suggestion_action)

        fun bind(action: String) {
            txtAction.text = action
        }
    }
}
