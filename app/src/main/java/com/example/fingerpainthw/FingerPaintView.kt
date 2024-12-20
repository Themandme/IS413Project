package com.nex3z.fingerpaintview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class FingerPaintView(
        context: Context,
        attrs: AttributeSet? = null
) : View(context, attrs) {

    private val path = Path()
    private lateinit var drawingBitmap: Bitmap
    private lateinit var drawingCanvas: Canvas
    private val drawingPaint = Paint(Paint.DITHER_FLAG)
    private var penX: Float = 0f
    private var penY:Float = 0f

    var pen = buildDefaultPen()
    var empty = true
        private set

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawingCanvas = Canvas(drawingBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.drawBitmap(drawingBitmap, 0f, 0f, drawingPaint)
        canvas?.drawPath(path, pen)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        empty = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchStart(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                onTouchMove(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                onTouchUp()
                performClick()
                invalidate()
            }
        }
        super.onTouchEvent(event)
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun clear() {
        path.reset()
        drawingBitmap = Bitmap.createBitmap(drawingBitmap.width, drawingBitmap.height,
                Bitmap.Config.ARGB_8888)
        drawingCanvas = Canvas(drawingBitmap)
        empty = true
        invalidate()
    }

    fun exportToBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        draw(canvas)
        return bitmap
    }

    fun exportToBitmap(width: Int, height: Int): Bitmap {
        val rawBitmap = exportToBitmap()
        val scaledBitmap = Bitmap.createScaledBitmap(rawBitmap, width, height, false)
        rawBitmap.recycle()
        return scaledBitmap
    }

    private fun onTouchStart(x: Float, y: Float) {
        path.reset()
        path.moveTo(x, y)
        penX = x
        penY = y
    }

    private fun onTouchMove(x: Float, y: Float) {
        val dx = abs(x - penX)
        val dy = abs(y - penY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(penX, penY, (x + penX) / 2, (y + penY) / 2)
            penX = x
            penY = y
        }
    }

    private fun onTouchUp() {
        path.lineTo(penX, penY)
        drawingCanvas.drawPath(path, pen)
        path.reset()
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4f
        private const val PEN_SIZE = 48f

        @JvmStatic
        private fun buildDefaultPen(): Paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = PEN_SIZE
        }
    }
}