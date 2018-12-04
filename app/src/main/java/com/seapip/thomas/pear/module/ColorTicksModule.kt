package com.seapip.thomas.pear.module

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log

class ColorTicksModule : Module {
    private var mBounds: Rect? = null
    private var mAmbient: Boolean = false
    private var mBurnInProtection: Boolean = false

    /* Paint */
    private val mTickPaint: Paint
    private val mHollowTickPaint: Paint

    init {
        mTickPaint = Paint()
        mTickPaint.setAntiAlias(true)
        mTickPaint.setStrokeCap(Paint.Cap.ROUND)
        mHollowTickPaint = Paint()
        mHollowTickPaint.setAntiAlias(true)
        mHollowTickPaint.setStrokeCap(Paint.Cap.ROUND)
        mHollowTickPaint.setColor(Color.BLACK)
    }

    @Override
    fun setBounds(bounds: Rect) {
        mBounds = bounds
        var stroke = (bounds.width() * 0.02f) as Int
        if (stroke % 2 != 0) {
            stroke--
        }
        mTickPaint.setStrokeWidth(stroke)
        mHollowTickPaint.setStrokeWidth(stroke - 6)
    }

    @Override
    fun draw(canvas: Canvas) {
        val outerRadius = mBounds!!.width() / 2 - mTickPaint.getStrokeWidth()
        val innerHourRadius = outerRadius - mBounds!!.width() * 0.13f + mTickPaint.getStrokeWidth()
        val innerMinuteRadius = outerRadius - mBounds!!.width() * 0.0001f
        for (i in 0..59) {
            val rot = (i * Math.PI * 2 / 60) as Float
            var innerX = Math.sin(rot) as Float
            var innerY = -Math.cos(rot) as Float
            val outerX = Math.sin(rot) as Float * outerRadius
            val outerY = -Math.cos(rot) as Float * outerRadius
            if (i % 5 == 0) {
                innerX *= innerHourRadius
                innerY *= innerHourRadius
            } else {
                innerX *= innerMinuteRadius
                innerY *= innerMinuteRadius
            }
            canvas.drawLine(mBounds!!.centerX() + innerX, mBounds!!.centerY() + innerY,
                    mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                    mTickPaint)
            if (mAmbient && mBurnInProtection && mHollowTickPaint.getStrokeWidth() > 0) {
                canvas.drawLine(mBounds!!.centerX() + innerX, mBounds!!.centerY() + innerY,
                        mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                        mHollowTickPaint)
            }
        }
    }

    @Override
    fun setColor(color: Int) {
        mTickPaint.setColor(color)
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
