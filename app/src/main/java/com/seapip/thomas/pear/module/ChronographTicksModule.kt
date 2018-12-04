package com.seapip.thomas.pear.module

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

class ChronographTicksModule(private var mScale: Int) : Module {
    private var mBounds: Rect? = null
    private var mAmbient: Boolean = false
    private var mBurnInProtection: Boolean = false

    /* Paint */
    private val mTickPaint: Paint
    private val mValueTickPaint: Paint
    private val mTextPaint: Paint

    init {
        mTickPaint = Paint()
        mTickPaint.setAntiAlias(true)
        mTickPaint.setColor(Color.parseColor("#707070"))
        mValueTickPaint = Paint()
        mValueTickPaint.setAntiAlias(true)
        mValueTickPaint.setColor(Color.WHITE)
        mTextPaint = Paint()
        mTextPaint.setAntiAlias(true)
        mTextPaint.setColor(Color.WHITE)
        mTextPaint.setTextAlign(Paint.Align.CENTER)
    }

    @Override
    fun setBounds(bounds: Rect) {
        mBounds = bounds
        mTickPaint.setStrokeWidth(bounds.width() * 0.005f)
        mValueTickPaint.setStrokeWidth(bounds.width() * 0.007f)
        mTextPaint.setTextSize(bounds.width() * 0.08f)
    }

    @Override
    fun draw(canvas: Canvas) {
        val outerRadius = mBounds!!.width() / 2
        val textRadius = outerRadius - mBounds!!.width() * 0.10f
        for (i in 0..239) {
            val rot = (i * Math.PI * 2 / 240) as Float
            val x = Math.sin(rot) as Float
            val y = -Math.cos(rot) as Float
            val innerRadius = outerRadius - mBounds!!.width() * if (i % 4 == 0) 0.042f else 0.022f
            canvas.drawLine(mBounds!!.centerX() + x * innerRadius,
                    mBounds!!.centerY() + y * innerRadius,
                    mBounds!!.centerX() + x * outerRadius,
                    mBounds!!.centerY() + y * outerRadius,
                    if (i % (48 / mScale.toFloat()) == 0f && mScale < 12
                            || i % 16 == 0 && mScale == 30
                            || i % 20 == 0 && (mScale > 30 || mScale == 12))
                        mValueTickPaint
                    else
                        mTickPaint)
            if (i % (240 / mScale) == 0 && mScale < 30
                    || i % 16 == 0 && mScale == 30
                    || i % 20 == 0 && mScale > 30) {
                canvas.drawText(String.valueOf(if (i == 0) mScale else mScale * i / 240),
                        mBounds!!.centerX() + x * textRadius,
                        mBounds!!.centerY() + y * textRadius - (mTextPaint.descent() + mTextPaint.ascent()) / 2,
                        mTextPaint)
            }
        }
    }

    fun setScale(scale: Int) {
        mScale = scale
    }

    @Override
    fun setColor(color: Int) {
    }

    @Override
    fun setAmbient(ambient: Boolean) {
        mAmbient = ambient
    }

    @Override
    fun setBurnInProtection(burnInProtection: Boolean) {
        mBurnInProtection = burnInProtection
    }

    @Override
    fun setLowBitAmbient(lowBitAmbient: Boolean) {

    }
}
