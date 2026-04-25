package com.ruoyi.app.feature.document.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * 签名 View - 基于 Canvas 绑定触摸事件绘制签名
 */
class SignatureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 8f
        color = Color.BLACK
    }

    private val path = Path()
    private var lastX = 0f
    private var lastY = 0f
    private var hasSignature = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                lastX = x
                lastY = y
                hasSignature = true
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2)
                lastX = x
                lastY = y
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                path.lineTo(x, y)
                invalidate()
                return true
            }
        }
        return false
    }

    /**
     * 清除签名
     */
    fun clear() {
        path.reset()
        hasSignature = false
        invalidate()
    }

    /**
     * 获取签名的 Bitmap
     */
    fun getSignatureBitmap(): Bitmap? {
        if (!hasSignature) return null

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawPath(path, paint)
        return bitmap
    }

    /**
     * 是否有签名
     */
    fun hasSignature(): Boolean = hasSignature
}
