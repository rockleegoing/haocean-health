package com.ruoyi.code.witget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class WatermarkView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    // 水印文本（可配置）
    private var watermarkText = "杭州浩洋科技有限公司 © 2026"

    // 水印文本大小
    private val textSize = 40f

    // 水印文本颜色（半透明）
    private val textColor = Color.parseColor("#33000000")

    // 水印旋转角度（通常-30°左右，视觉效果更好）
    private val rotateAngle = -30f

    // 绘制画笔
    private val paint by lazy {
        Paint().apply {
            color = textColor
            textSize = this@WatermarkView.textSize
            isAntiAlias = true // 抗锯齿
            textAlign = Paint.Align.CENTER
        }
    }

    // 水印位图（缓存，避免重复绘制提升性能）
    private var watermarkBitmap: Bitmap? = null

    /**
     * 对外提供设置水印文本的方法
     */
    fun setWatermarkText(text: String) {
        this.watermarkText = text
        invalidate() // 刷新绘制
    }

    /**
     * 第一步：先绘制单个水印到Bitmap（缓存）
     */
    private fun createWatermarkBitmap() {
        if (watermarkBitmap != null) return
        // 计算单个水印的宽高（预留足够间距）
        val textWidth = paint.measureText(watermarkText) + 100
        val textHeight = textSize + 100
        // 创建位图
        watermarkBitmap = Bitmap.createBitmap(
            textWidth.toInt(),
            textHeight.toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(watermarkBitmap!!)
        // 绘制文本到位图（居中显示）
        val textY = textHeight / 2 + (paint.descent() + paint.ascent()) / 2
        canvas.drawText(watermarkText, textWidth / 2, textY, paint)
    }

    /**
     * 第二步：在View上平铺绘制水印（核心：重复绘制缓存的水印位图）
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 创建水印位图
        createWatermarkBitmap()
        val bitmap = watermarkBitmap ?: return

        // 保存画布状态（旋转后恢复）
        canvas.save()
        // 旋转画布（实现水印倾斜效果）
        canvas.rotate(rotateAngle, width / 2f, height / 2f)

        // 计算需要绘制的行列数（铺满整个View）
        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height
        val horizontalCount = (width / bitmapWidth + 2) // 多绘制一列，避免留白
        val verticalCount = (height / bitmapHeight + 2) // 多绘制一行，避免留白

        // 平铺绘制水印（双重循环实现重复效果）
        for (i in 0 until horizontalCount) {
            for (j in 0 until verticalCount) {
                val x = i * bitmapWidth - bitmapWidth / 2f
                val y = j * bitmapHeight - bitmapHeight / 2f
                canvas.drawBitmap(bitmap, x, y, null)
            }
        }

        // 恢复画布状态
        canvas.restore()
    }
}