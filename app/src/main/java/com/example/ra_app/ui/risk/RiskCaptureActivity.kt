package com.example.ra_app.ui.risk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ra_app.data.local.AppDatabase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.ra_app.data.repository.RiskAnalysisRepository
import com.example.ra_app.utils.ImageUtils
import com.google.android.material.snackbar.Snackbar
import com.riesgosra.app.R
import kotlinx.coroutines.flow.first
import com.example.ra_app.utils.ApiKeyProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.InputStream

@AndroidEntryPoint
class RiskCaptureActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var layoutLoading: View
    private var imageCapture: ImageCapture? = null
    private var lastCapturedBitmap: Bitmap? = null

    @Inject lateinit var database: AppDatabase

    private val repository by lazy {
        RiskAnalysisRepository(dao = database.riskAnalysisDao())
    }

    private val viewModel: RiskViewModel by viewModels {
        RiskViewModel.Factory(repository)
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, R.string.permiso_camara_requerido, Toast.LENGTH_LONG).show()
        }
    }

    private val pickGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val bitmap = ImageUtils.fixRotation(this, uri)
            if (bitmap != null) {
                lastCapturedBitmap = bitmap
                if (puedeAnalizar()) viewModel.analyzeBitmap(this, bitmap)
            } else {
                Snackbar.make(previewView, "No se pudo cargar la imagen seleccionada", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_capture)

        previewView = findViewById(R.id.preview_view)
        layoutLoading = findViewById(R.id.layout_loading)

        findViewById<View>(R.id.btn_capture).setOnClickListener {
            capturePhoto()
        }

        findViewById<View>(R.id.btn_gallery).setOnClickListener {
            pickGalleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        findViewById<View>(R.id.btn_history).setOnClickListener {
            showHistoryDialog()
        }

        observeViewModel()
        when (intent.getStringExtra(EXTRA_INITIAL_ACTION)) {
            "history" -> if (savedInstanceState == null) showHistoryDialog()
            "gallery" -> if (savedInstanceState == null && puedeAnalizar()) pickGalleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            else -> checkCameraPermission()
        }
    }

    private fun puedeAnalizar(): Boolean {
        if (ApiKeyProvider.hasValidKey()) return true
        AlertDialog.Builder(this)
            .setTitle(R.string.analisis_activar_titulo)
            .setMessage(R.string.analisis_clave_pendiente)
            .setPositiveButton(android.R.string.ok, null)
            .show()
        return false
    }

    companion object {
        const val EXTRA_INITIAL_ACTION = "analysis_initial_action"
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun capturePhoto() {
        if (!puedeAnalizar()) return
        val imageCapture = imageCapture ?: return
        layoutLoading.visibility = View.VISIBLE

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    val buffer = imageProxy.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imageProxy.close()

                    val rotated = ImageUtils.fixRotation(bitmap, rotationDegrees)
                    lastCapturedBitmap = rotated
                    viewModel.analyzeBitmap(this@RiskCaptureActivity, rotated)
                }

                override fun onError(exception: ImageCaptureException) {
                    layoutLoading.visibility = View.GONE
                    Snackbar.make(previewView, "Error al capturar foto: ${exception.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is RiskUiState.Idle -> {
                            layoutLoading.visibility = View.GONE
                        }
                        is RiskUiState.Loading -> {
                            layoutLoading.visibility = View.VISIBLE
                        }
                        is RiskUiState.Success -> {
                            layoutLoading.visibility = View.GONE
                            val intent = Intent(this@RiskCaptureActivity, RiskResultActivity::class.java).apply {
                                putExtra(RiskResultActivity.EXTRA_ANALYSIS_ID, state.analysis.id)
                            }
                            startActivity(intent)
                            viewModel.resetState()
                        }
                        is RiskUiState.Error -> {
                            layoutLoading.visibility = View.GONE
                            val snackbar = Snackbar.make(previewView, state.message, Snackbar.LENGTH_INDEFINITE)
                            if (state.canRetry && lastCapturedBitmap != null) {
                                snackbar.setAction(R.string.btn_reintentar) {
                                    lastCapturedBitmap?.let { bitmap ->
                                        viewModel.analyzeBitmap(this@RiskCaptureActivity, bitmap)
                                    }
                                }
                            } else {
                                snackbar.setAction(android.R.string.ok) { snackbar.dismiss() }
                            }
                            snackbar.show()
                        }
                    }
                }
            }
        }
    }

    private fun showHistoryDialog() {
        lifecycleScope.launch {
            val list = viewModel.recentAnalyses.first()
            run {
                if (list.isEmpty()) {
                    AlertDialog.Builder(this@RiskCaptureActivity)
                        .setTitle(R.string.titulo_historial)
                        .setMessage(R.string.txt_sin_historial)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                } else {
                    val items = list.map { "${it.category.uppercase()} - Confianza: ${(it.confidence * 100).toInt()}%" }.toTypedArray()
                    AlertDialog.Builder(this@RiskCaptureActivity)
                        .setTitle(R.string.titulo_historial)
                        .setItems(items) { _, which ->
                            val selected = list[which]
                            val intent = Intent(this@RiskCaptureActivity, RiskResultActivity::class.java).apply {
                                putExtra(RiskResultActivity.EXTRA_ANALYSIS_ID, selected.id)
                            }
                            startActivity(intent)
                        }
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                }
            }
        }
    }
}
