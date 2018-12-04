package com.seapip.thomas.pear.module

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

class SimpleTicksModule(private var mStyle: Int) : Module {
    private var mBounds: Rect? = null
    private var mAmbient: Boolean = false
    private var mBurnInProtection: Boolean = false

    /* Paint */
    private val mSecondsTickPaint: Paint
    private val mMinuteTickPaint: Paint
    private val mHourTickPaint: Paint
    private val mHollowHourTickPaint: Paint
    private val mTickTextPaint: Paint

    init {
        mSecondsTickPaint = Paint()
        mSecondsTickPaint.setAntiAlias(true)
        mSecondsTickPaint.setColor(Color.parseColor("#707070"))
        mMinuteTickPaint = Paint()
        mMinuteTickPaint.setAntiAlias(true)
        mMinuteTickPaint.setColor(Color.WHITE)
        mHourTickPaint = Paint()
        mHourTickPaint.setAntiAlias(true)
        mHourTickPaint.setStrokeCap(Paint.Cap.ROUND)
        mHourTickPaint.setColor(Color.parseColor("#b2b2b2"))
        mHollowHourTickPaint = Paint()
        mHollowHourTickPaint.setAntiAlias(true)
        mHollowHourTickPaint.setStrokeCap(Paint.Cap.ROUND)
        mHollowHourTickPaint.setColor(Color.BLACK)
        mTickTextPaint = Paint()
        mTickTextPaint.setAntiAlias(true)
        mTickTextPaint.setColor(Color.WHITE)
        mTickTextPaint.setTextAlign(Paint.Align.CENTER)
    }

    @Override
    fun setBounds(bounds: Rect) {
        mBounds = bounds
        mSecondsTickPaint.setStrokeWidth(bounds.width() * 0.005f)
        mMinuteTickPaint.setStrokeWidth(bounds.width() * 0.007f)
        var stroke = (bounds.width() * 0.026f) as Int
        if (stroke % 2 != 0) {
            stroke--
        }
        mHourTickPaint.setStrokeWidth(stroke)
        mHollowHourTickPaint.setStrokeWidth(stroke - 6)
        mTickTextPaint.setTextSize(bounds.width() * 0.05f)
    }

    @Override
    fun draw(canvas: Canvas) {
        val outerRadius = mBounds!!.width() / 2
        val innerRadius = outerRadius - mBounds!!.width() * 0.038f
        val hourOuterRadius = mBounds!!.width() / 2 - mBounds!!.width() * 0.055f - mHourTickPaint.getStrokeWidth()
        val hourInnerRadius = hourOuterRadius - mBounds!!.width() * 0.12f + mHourTickPaint.getStrokeWidth()
        val textRadius = mBounds!!.width() / 2 + mBounds!!.width() * 0.05f
        for (i in 0..239) {
            val rot = (i * Math.PI * 2 / 240) as Float
            val x = Math.sin(rot) as Float
            val y = -Math.cos(rot) as Float
            if (mStyle == 1 && i % 4 == 0
                    || mStyle == 2 && i % 2 == 0 && i % 20 != 0
                    || mStyle == 3 && i % 20 != 0) {
                canvas.drawLine(mBounds!!.centerX() + x * innerRadius,
                        mBounds!!.centerY() + y * innerRadius,
                        mBounds!!.centerX() + x * outerRadius,
                        mBounds!!.centerY() + y * outerRadius,
                        mSecondsTickPaint)
            }
            if (i % 20 == 0) {
                if (mStyle > 1) {
                    canvas.drawLine(mBounds!!.centerX() + x * innerRadius,
                            mBounds!!.centerY() + y * innerRadius,
                            mBounds!!.centerX() + x * outerRadius,
                            mBounds!!.centerY() + y * outerRadius,
                            mMinuteTickPaint)
                    canvas.drawLine(mBounds!!.centerX() + x * hourInnerRadius,
                            mBounds!!.centerY() + y * hourInnerRadius,
                            mBounds!!.centerX() + x * hourOuterRadius,
                            mBounds!!.centerY() + y * hourOuterRadius,
                            mHourTickPaint)
                    if (mAmbient && mBurnInProtection && mHollowHourTickPaint.getStrokeWidth() > 0) {
                        canvas.drawLine(mBounds!!.centerX() + x * hourInnerRadius,
                                mBounds!!.centerY() + y * hourInnerRadius,
                                mBounds!!.centerX() + x * hourOuterRadius,
                                mBounds!!.centerY() + y * hourOuterRadius,
                                mHollowHourTickPaint)
                    }
                }
                if (mStyle > 2) {
                    canvas.drawText(String.valueOf(if (i / 4 == 0) 60 else i / 4),
                            mBounds!!.centerX() + x * textRadius,
                            mBounds!!.centerY() + y * textRadius - (mTickTextPaint.descent() + mTickTextPaint.ascent()) / 2,
                            mTickTextPaint)
                }
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
