package com.seapip.thomas.pear.module

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface

import java.util.Calendar

class DigitalClockModule(private val mCalendar: Calendar, private var mTimeFormat24: Boolean) : Module {
    private var mBounds: Rect? = null
    private var mAmbient: Boolean = false

    /* Fonts */
    private val mFontLight: Typeface

    /* Paint */
    private val mTextPaint: Paint
    private val backgroundPaint: Paint

    init {

        /* Fonts */
        mFontLight = Typeface.create("sans-serif-light", Typeface.NORMAL)

        /* Paint */
        mTextPaint = Paint()
        mTextPaint.setAntiAlias(true)
        mTextPaint.setTextAlign(Paint.Align.RIGHT)
        mTextPaint.setTypeface(mFontLight)
        backgroundPaint = Paint()
        backgroundPaint.setColor(Color.argb(20, 0, 255, 0))
    }

    fun setTimeFormat24(timeFormat24: Boolean) {
        mTimeFormat24 = timeFormat24
    }

    @Override
    fun setBounds(bounds: Rect) {
        mBounds = bounds
        mTextPaint.setTextSize(bounds.height() * 0.80f)
    }

    @Override
    fun draw(canvas: Canvas) {
        val hourString: String
        if (mTimeFormat24) {
            hourString = String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY))
        } else {
            var hour = mCalendar.get(Calendar.HOUR)
            if (hour == 0) {
                hour = 12
            }
            hourString = String.valueOf(hour)
        }
        var minuteString = String.valueOf(mCalendar.get(Calendar.MINUTE))
        if (minuteString.length() === 1) {
            minuteString = "0$minuteString"
        }

        val x = mBounds!!.right - mBounds!!.height() * 0.05f
        val y = mBounds!!.centerY() - (mTextPaint.descent() + mTextPaint.ascent()) / 2

        mTextPaint.setAlpha(255)
        canvas.drawText("$hourString $minuteString", x, y, mTextPaint)
        val alpha = if (mAmbient)
            164
        else
            Math.abs(mCalendar.get(Calendar.MILLISECOND) / 1000f - 0.5f) * 2 * 128 + 128
        mTextPaint.setAlpha(alpha)
        canvas.drawText(":", x - mTextPaint.measureText(minuteString), y, mTextPaint)
    }

    @Override
    fun setColor(color: Int) {
        mTextPaint.setColor(color)
    }

    @Override
    fun setAmbient(ambient: Boolean) {
        mAmbient = ambient
    }

    @Override
    fun setBurnInProtection(burnInProtection: Boolean) {
    }

    @Override
    fun setLowBitAmbient(lowBitAmbient: Boolean) {
    }
}
