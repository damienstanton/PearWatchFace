package com.seapip.thomas.pear.module

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

class UtilityTicksModule(private var mStyle: Int) : Module {
    private var mBounds: Rect? = null
    private var mAmbient: Boolean = false
    private var mBurnInProtection: Boolean = false

    /* Paint */
    private val mSecondsTickPaint: Paint
    private val mMinuteTickPaint: Paint
    private val mHourTextPaint: Paint
    private val mSecondsTextPaint: Paint

    init {
        mSecondsTickPaint = Paint()
        mSecondsTickPaint.setAntiAlias(true)
        mSecondsTickPaint.setColor(Color.parseColor("#707070"))
        mMinuteTickPaint = Paint()
        mMinuteTickPaint.setAntiAlias(true)
        mMinuteTickPaint.setColor(Color.WHITE)
        mHourTextPaint = Paint()
        mHourTextPaint.setAntiAlias(true)
        mHourTextPaint.setColor(Color.WHITE)
        mHourTextPaint.setTextAlign(Paint.Align.CENTER)
        mSecondsTextPaint = Paint()
        mSecondsTextPaint.setAntiAlias(true)
        mSecondsTextPaint.setColor(Color.parseColor("#eeeeee"))
        mSecondsTextPaint.setTextAlign(Paint.Align.CENTER)
    }

    @Override
    fun setBounds(bounds: Rect) {
        mBounds = bounds
        mSecondsTickPaint.setStrokeWidth(bounds.width() * 0.005f)
        var stroke = (bounds.width() * 0.02f) as Int
        if (stroke % 2 != 0) {
            stroke--
        }
        mMinuteTickPaint.setStrokeWidth(stroke)
        mHourTextPaint.setTextSize(bounds.width() * 0.12f)
        mSecondsTextPaint.setTextSize(bounds.width() * 0.04f)
    }

    @Override
    fun draw(canvas: Canvas) {
        val outerRadius = mBounds!!.width() / 2
        val innerRadius = outerRadius - mBounds!!.width() * 0.038f
        val secondsTextRadius = outerRadius - mBounds!!.width() * 0.019f
        val hourTextRadius = outerRadius - mBounds!!.width() * 0.13f
        for (i in 0..59) {
            val rot = (i * Math.PI * 2 / 60) as Float
            val x = Math.sin(rot) as Float
            val y = -Math.cos(rot) as Float
            if (i % 5 != 0) {
                canvas.drawLine(mBounds!!.centerX() + x * innerRadius,
                        mBounds!!.centerY() + y * innerRadius,
                        mBounds!!.centerX() + x * outerRadius,
                        mBounds!!.centerY() + y * outerRadius,
                        mSecondsTickPaint)
            } else if (mStyle < 3) {
                canvas.drawLine(mBounds!!.centerX() + x * innerRadius,
                        mBounds!!.centerY() + y * innerRadius,
                        mBounds!!.centerX() + x * outerRadius,
                        mBounds!!.centerY() + y * outerRadius,
                        mMinuteTickPaint)
            } else {
                var seconds = String.valueOf(if (i == 0) 12 else i)
                seconds = if (seconds.length() < 2) "0$seconds" else seconds
                canvas.drawText(seconds,
                        mBounds!!.centerX() + x * secondsTextRadius,
                        mBounds!!.centerY() + y * secondsTextRadius - (mSecondsTextPaint.descent() + mSecondsTextPaint.ascent()) / 2,
                        mSecondsTextPaint)
            }
            if (i % 15 == 0 && mStyle > 0 || i % 5 == 0 && mStyle > 1) {
                canvas.drawText(String.valueOf(if (i == 0) 12 else i / 5),
                        mBounds!!.centerX() + x * hourTextRadius,
                        mBounds!!.centerY() + y * hourTextRadius - (mHourTextPaint.descent() + mHourTextPaint.ascent()) / 2,
                        mHourTextPaint)
            }
        }
    }

    fun setStyle(style: Int) {
        mStyle = style
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
