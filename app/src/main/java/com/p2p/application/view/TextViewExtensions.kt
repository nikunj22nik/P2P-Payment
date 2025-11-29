package com.p2p.application.view

import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Color
import android.widget.TextView
import androidx.core.graphics.toColorInt

fun TextView.applyExactGradient() {
    post {
        val height = textSize // height of text

        val shader = LinearGradient(
            0f, 0f, 0f, height,  // <-- VERTICAL GRADIENT (important)
            intArrayOf(
                "#B13A7E".toColorInt(), // Top Pink/Purple
                "#E2692B".toColorInt()  // Bottom Orange
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )

        paint.shader = shader
        invalidate()
    }
}
