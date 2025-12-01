package com.p2p.application.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CustomTransactionCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        setShadowLayer(20f, 0f, 10f, Color.parseColor("#40000000"))
    }

    private val cornerRadius = 40f
    private val notchDepth = 60f
    private val notchWidth = 110f

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = 20f
        val centerX = width / 2f
        val cardTop = padding

        // Draw the card with notch curving DOWNWARD
        val path = Path().apply {
            // Start from top-left corner
            moveTo(padding + cornerRadius, cardTop)

            // Top-left corner arc
            arcTo(
                RectF(
                    padding,
                    cardTop,
                    padding + cornerRadius * 2,
                    cardTop + cornerRadius * 2
                ),
                180f, 90f, false
            )

            // Line to notch start
            lineTo(centerX - notchWidth, cardTop)

            // Left curve of notch going DOWN (into the card)
            cubicTo(
                centerX - notchWidth * 0.55f, cardTop,
                centerX - notchWidth * 0.15f, cardTop + notchDepth * 1.3f,
                centerX, cardTop + notchDepth
            )

            // Right curve of notch coming back UP
            cubicTo(
                centerX + notchWidth * 0.15f, cardTop + notchDepth * 1.3f,
                centerX + notchWidth * 0.55f, cardTop,
                centerX + notchWidth, cardTop
            )

            // Line to top-right corner
            lineTo(width - padding - cornerRadius, cardTop)

            // Top-right corner arc
            arcTo(
                RectF(
                    width - padding - cornerRadius * 2,
                    cardTop,
                    width - padding,
                    cardTop + cornerRadius * 2
                ),
                270f, 90f, false
            )

            // Right edge
            lineTo(width - padding, height - padding - cornerRadius)

            // Bottom-right corner arc
            arcTo(
                RectF(
                    width - padding - cornerRadius * 2,
                    height - padding - cornerRadius * 2,
                    width - padding,
                    height - padding
                ),
                0f, 90f, false
            )

            // Bottom edge
            lineTo(padding + cornerRadius, height - padding)

            // Bottom-left corner arc
            arcTo(
                RectF(
                    padding,
                    height - padding - cornerRadius * 2,
                    padding + cornerRadius * 2,
                    height - padding
                ),
                90f, 90f, false
            )

            // Left edge back to start
            lineTo(padding, cardTop + cornerRadius)

            close()
        }

        canvas.drawPath(path, cardPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = 850
        setMeasuredDimension(width, height)
    }
}