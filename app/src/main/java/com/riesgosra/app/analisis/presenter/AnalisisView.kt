package com.riesgosra.app.analisis.presenter

import android.graphics.Bitmap

interface AnalisisView {
    fun mostrarEstado(foto: Bitmap?, ocupado: Boolean, resultado: String, error: String?)
}
