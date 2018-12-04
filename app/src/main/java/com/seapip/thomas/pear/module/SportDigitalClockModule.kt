package com.seapip.thomas.pear.module

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface

import java.util.Calendar

class SportDigitalClockModule(private val mContext: Context, private val mCalendar: Calendar,
                              private var mTimeFormat24: Boolean, private var mStyle: Int) : Module {
    private var mBounds: Rect? = null
    private var mAmbient: Boolean = false
    private var mBurnInProtection: Boolean = false

    /* Fonts */
    private val mFontBold: Typeface

    /* Paint */
    private val mHourTextPaint: Paint
    private val mMinuteTextPaint: Paint

    init {

        /* Fonts */
        mFontBold = Typeface.createFromAsset(mContext.getAssets(), "fonts/Sport.ttf")

        /* Paint */
        mHourTextPaint = Paint()
        mHourTextPaint.setAntiAlias(true)
        mHourTextPaint.setTextAlign(Paint.Align.RIGHT)
        mHourTextPaint.setTypeface(mFontBold)
        mHourTextPaint.setStyle(Paint.Style.FILL_AND_STROKE)
        mMinuteTextPaint = Paint()
        mMinuteTextPaint.setAntiAlias(true)
        mMinuteTextPaint.setTextAlign(Paint.Align.RIGHT)
        mMinuteTextPaint.setTypeface(mFontBold)
        mMinuteTextPaint.setStyle(Paint.Style.FILL_AND_STROKE)
        setStyle(mStyle)
    }

    fun setTimeFormat24(timeFormat24: Boolean) {
        mTimeFormat24 = timeFormat24
    }

    @Override
    fun setBounds(bounds: Rect) {
        mBounds = bounds
        mHourTextPaint.setTextSize(bounds.height() * 0.54f)
        mHourTextPaint.setStrokeWidth(bounds.height() * 0.015f)
        mMinuteTextPaint.setTextSize(bounds.height() * 0.54f)
        mMinuteTextPaint.setStrokeWidth(bounds.height() * 0.015f)
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

        canvas.drawText(hourString,
                mBounds!!.right - mBounds!!.width() * 0.1f,
                mBounds!!.top + mBounds!!.width() * 0.07f - (mHourTextPaint.descent() + mHourTextPaint.ascent()),
                mHourTextPaint)
        canvas.drawText(minuteString,
                mBounds!!.right - mBounds!!.width() * 0.1f,
                mBounds!!.bottom - mBounds!!.width() * 0.1f,
                mMinuteTextPaint)
    }

    fun setStyle(style: Int) {
        mStyle = style
        changeStyle(mStyle)
    }

    private fun changeStyle(style: Int) {
        when (style) {
            0 //Both filled
            -> {
                mHourTextPaint.setStyle(Paint.Style.FILL_AND_STROKE)
                mMinuteTextPaint.setStyle(Paint.Style.FILL_AND_STROKE)
            }
            1 //Top filled and bottom stroke
            -> {
                mHourTextPaint.setStyle(Paint.Style.FILL_AND_STROKE)
                mMinuteTextPaint.setStyle(Paint.Style.STROKE)
            }
            2 //Top and bottom stroke
            -> {
                mHourTextPaint.setStyle(Paint.Style.STROKE)
                mMinuteTextPaint.setStyle(Paint.Style.STROKE)
            }
        }
    }

    @Override
    fun setColor(color: Int) {
        mHourTextPaint.setColor(color)
        mMinuteTextPaint.setColor(color)
    }

    @Override
    fun setAmbient(ambient: Boolean) {
        mAmbient = ambient
        if (mBurnInProtection) {
            if (mAmbient) {
                changeStyle(2)
            } else {
                changeStyle(mStyle)
            }
        }
    }

    @Override
    fun setBurnInProtection(burnInProtection: Boolean) {
        mBurnInProtection = burnInProtection
    }

    @Override
    fun setLowBitAmbient(lowBitAmbient: Boolean) {
    }
}
