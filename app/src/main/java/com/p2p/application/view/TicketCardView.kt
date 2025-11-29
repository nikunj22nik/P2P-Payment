package com.p2p.application.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlin.apply

class TicketCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        style = Paint.Style.FILL
    }

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF0000")
        style = Paint.Style.FILL
        setShadowLayer(25f, 0f, 8f, Color.parseColor("#FF0000"))
    }

    private val path = Path()

    private val radius = 40f
    private val cutRadius = 130f

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setPadding(32, 32, 32, 32)
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2

        path.reset()
        path.moveTo(radius, 0f)

        // top-left → start cut
        path.lineTo(cx - cutRadius-30, 0f)

        // deeper circular inward cut
        path.quadTo(
            cx, cutRadius * 1.9f,  // ⬅️ more depth
            cx + cutRadius, 0f
        )

        // top-right line
        path.lineTo(w - radius+30, 0f)

        // top-right corner
        path.quadTo(w, 0f, w, radius)

        // right
        path.lineTo(w, h - radius)

        // bottom-right corner
        path.quadTo(w, h, w - radius, h)

        // bottom
        path.lineTo(radius, h)

        // bottom-left corner
        path.quadTo(0f, h, 0f, h - radius)

        // left
        path.lineTo(0f, radius)

        // top-left corner
        path.quadTo(0f, 0f, radius, 0f)

        path.close()

        canvas.drawPath(path, shadowPaint)
        canvas.drawPath(path, paint)
    }
}
