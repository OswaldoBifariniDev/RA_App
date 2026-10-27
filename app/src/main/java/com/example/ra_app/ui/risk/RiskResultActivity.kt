package com.example.ra_app.ui.risk

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ra_app.data.local.AppDatabase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.ra_app.data.model.RiskAnalysis
import com.example.ra_app.data.repository.RiskAnalysisRepository
import com.example.ra_app.ui.risk.adapter.SuggestionAdapter
import com.google.android.material.chip.Chip
import com.riesgosra.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RiskResultActivity : AppCompatActivity() {

    private lateinit var imgPhoto: ImageView
    private lateinit var chipCategory: Chip
    private lateinit var progressConfidence: ProgressBar
    private lateinit var txtConfidence: TextView
    private lateinit var cardLowConfidence: View
    private lateinit var txtDescription: TextView
    private lateinit var recyclerSuggestions: RecyclerView
    private lateinit var adapter: SuggestionAdapter

    @Inject lateinit var database: AppDatabase

    private val repository by lazy {
        RiskAnalysisRepository(dao = database.riskAnalysisDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_result)

        imgPhoto = findViewById(R.id.img_risk_photo)
        chipCategory = findViewById(R.id.chip_category)
        progressConfidence = findViewById(R.id.progress_confidence)
        txtConfidence = findViewById(R.id.txt_confidence)
        cardLowConfidence = findViewById(R.id.card_low_confidence)
        txtDescription = findViewById(R.id.txt_description)
        recyclerSuggestions = findViewById(R.id.recycler_suggestions)

        adapter = SuggestionAdapter()
        recyclerSuggestions.layoutManager = LinearLayoutManager(this)
        recyclerSuggestions.adapter = adapter

        findViewById<View>(R.id.btn_analyze_another).setOnClickListener {
            finish()
        }

        val analysisId = intent.getLongExtra(EXTRA_ANALYSIS_ID, -1L)
        if (analysisId != -1L) {
            loadAnalysisData(analysisId)
        } else {
            finish()
        }
    }

    private fun loadAnalysisData(id: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val analysis = repository.getAnalysisById(id)
            if (analysis != null) {
                withContext(Dispatchers.Main) {
                    displayAnalysis(analysis)
                }
            }
        }
    }

    private fun displayAnalysis(analysis: RiskAnalysis) {
        val categoryEnum = analysis.getCategoryEnum()
        chipCategory.text = categoryEnum.displayName
        val categoryColor = ContextCompat.getColor(this, categoryEnum.colorResId)
        chipCategory.chipBackgroundColor = ColorStateList.valueOf(categoryColor)

        val confidencePercentage = (analysis.confidence * 100).toInt()
        progressConfidence.progress = confidencePercentage
        txtConfidence.text = getString(R.string.txt_confianza, confidencePercentage)

        if (analysis.confidence < 0.5f) {
            cardLowConfidence.visibility = View.VISIBLE
        } else {
            cardLowConfidence.visibility = View.GONE
        }

        txtDescription.text = analysis.description

        if (!analysis.photoPath.isNullOrBlank()) {
            val bitmap = BitmapFactory.decodeFile(analysis.photoPath)
            if (bitmap != null) {
                imgPhoto.setImageBitmap(bitmap)
            }
        }

        val suggestions = analysis.getSuggestionsList()
        adapter.updateSuggestions(suggestions)
    }

    companion object {
        const val EXTRA_ANALYSIS_ID = "extra_analysis_id"
    }
}
