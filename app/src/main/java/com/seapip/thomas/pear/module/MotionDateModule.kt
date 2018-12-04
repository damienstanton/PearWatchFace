package com.seapip.thomas.pear.module

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface

import java.util.Calendar
import java.util.Locale

class MotionDateModule(private val mCalendar: Calendar, private var mDate: Int) : Module {
    private var mBounds: Rect? = null
    private var mAmbient: Boolean = false

    /* Fonts */
    private val mFontLight: Typeface

    /* Paint */
    private val mTextPaint: Paint

    init {

        /* Fonts */
        mFontLight = Typeface.create("sans-serif-light", Typeface.NORMAL)

        /* Paint */
        mTextPaint = Paint()
        mTextPaint.setAntiAlias(true)
        mTextPaint.setTextAlign(Paint.Align.RIGHT)
        mTextPaint.setTypeface(mFontLight)

    }

    @Override
    fun setBounds(bounds: Rect) {
        mBounds = bounds
        mTextPaint.setTextSize(bounds.height() * 0.80f)
    }

    @Override
    fun draw(canvas: Canvas) {
        val dayOfWeek = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
        val dayOfMonth = String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH))
        val month = mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        var date = ""
        when (mDate) {
            1 -> date = dayOfWeek.toUpperCase() + " " + dayOfMonth
            2 -> date = month.toUpperCase() + " " + dayOfMonth
            3 -> date = dayOfMonth
        }

        canvas.drawText(date,
                mBounds!!.right - mBounds!!.height() * 0.05f,
                mBounds!!.centerY() - (mTextPaint.descent() + mTextPaint.ascent()) / 2,
                mTextPaint)
    }

    @Override
    fun setColor(color: Int) {
        mTextPaint.setColor(color)
        mTextPaint.setAlpha(192)
    }

    fun setDate(date: Int) {
        mDate = date
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
