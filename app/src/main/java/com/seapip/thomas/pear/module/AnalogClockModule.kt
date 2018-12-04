package com.seapip.thomas.pear.module

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

import java.util.Calendar

class AnalogClockModule(private val mCalendar: Calendar) : Module {
    private var mBounds: Rect? = null
    private var mAmbient: Boolean = false
    private var mBurnInProtection: Boolean = false

    /* Paint */
    private val mHandPaint: Paint
    private val mHollowHandPaint: Paint
    private val mConnectHandCenterPaint: Paint
    private val mHandCenterPaint: Paint
    private val mSecondsCenterPaint: Paint
    private val mSecondsHandPaint: Paint
    private val mCenterPaint: Paint

    init {

        /* Paint */
        mHandPaint = Paint()
        mHandPaint.setAntiAlias(true)
        mHandPaint.setColor(Color.WHITE)
        mHandPaint.setStrokeCap(Paint.Cap.ROUND)
        mHollowHandPaint = Paint()
        mHollowHandPaint.setAntiAlias(true)
        mHollowHandPaint.setColor(Color.BLACK)
        mHollowHandPaint.setStrokeCap(Paint.Cap.ROUND)
        mConnectHandCenterPaint = Paint()
        mConnectHandCenterPaint.setAntiAlias(true)
        mConnectHandCenterPaint.setColor(Color.WHITE)
        mConnectHandCenterPaint.setStrokeCap(Paint.Cap.ROUND)
        mHandCenterPaint = Paint()
        mHandCenterPaint.setAntiAlias(true)
        mHandCenterPaint.setColor(Color.WHITE)
        mHandCenterPaint.setStrokeCap(Paint.Cap.ROUND)
        mSecondsCenterPaint = Paint()
        mSecondsCenterPaint.setAntiAlias(true)
        mSecondsCenterPaint.setStrokeCap(Paint.Cap.ROUND)
        mSecondsHandPaint = Paint()
        mSecondsHandPaint.setAntiAlias(true)
        mCenterPaint = Paint()
        mCenterPaint.setAntiAlias(true)
        mCenterPaint.setColor(Color.BLACK)
        mCenterPaint.setStrokeCap(Paint.Cap.ROUND)
    }

    @Override
    fun setBounds(bounds: Rect) {
        mBounds = bounds
        var stroke = (mBounds!!.width() * 0.042f) as Int
        if (stroke % 2 != 0) {
            stroke--
        }
        mHandPaint.setStrokeWidth(stroke)
        mHollowHandPaint.setStrokeWidth(stroke - 6)
        stroke = mBounds!!.width() * 0.016f
        if (stroke % 2 != 0) {
            stroke--
        }
        mConnectHandCenterPaint.setStrokeWidth(stroke)
        stroke = mBounds!!.width() * 0.048f
        if (stroke % 2 != 0) {
            stroke--
        }
        mHandCenterPaint.setStrokeWidth(stroke)
        stroke = mBounds!!.width() * 0.036f
        if (stroke % 2 != 0) {
            stroke--
        }
        mSecondsCenterPaint.setStrokeWidth(stroke)
        mSecondsHandPaint.setStrokeWidth(mBounds!!.width() * 0.007f)
        stroke = mBounds!!.width() * 0.012f
        if (stroke % 2 != 0) {
            stroke--
        }
        mCenterPaint.setStrokeWidth(stroke)
    }

    @Override
    fun draw(canvas: Canvas) {
        val hours = mCalendar.get(Calendar.HOUR)
        val minutes = mCalendar.get(Calendar.MINUTE)
        val milliSeconds = (if (mAmbient)
            0
        else
            mCalendar.get(Calendar.SECOND) * 1000 + mCalendar.get(Calendar.MILLISECOND)).toFloat()

        /* Hour hand */
        var outerRadius = mBounds!!.width() * 0.28f - mHandPaint.getStrokeWidth()
        var innerRadius = mBounds!!.width() * 0.04f + mHandPaint.getStrokeWidth()
        var rot = ((hours * 5 + minutes / 12) * Math.PI * 2 / 60) as Float
        var innerX = Math.sin(rot) as Float * innerRadius
        var innerY = -Math.cos(rot) as Float * innerRadius
        var outerX = Math.sin(rot) as Float * outerRadius
        var outerY = -Math.cos(rot) as Float * outerRadius
        canvas.drawLine(mBounds!!.centerX(), mBounds!!.centerY(),
                mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                mConnectHandCenterPaint)
        canvas.drawLine(mBounds!!.centerX() + innerX, mBounds!!.centerY() + innerY,
                mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                mHandPaint)
        if (mAmbient && mBurnInProtection && mHollowHandPaint.getStrokeWidth() > 0) {
            canvas.drawLine(mBounds!!.centerX() + innerX, mBounds!!.centerY() + innerY,
                    mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                    mHollowHandPaint)
        }

        /* Minute hand */
        outerRadius = mBounds!!.width() * 0.46f - mHandPaint.getStrokeWidth()
        rot = (minutes + milliSeconds / 60000) * Math.PI * 2 / 60
        innerX = Math.sin(rot) as Float * innerRadius
        innerY = -Math.cos(rot) as Float * innerRadius
        outerX = Math.sin(rot) as Float * outerRadius
        outerY = -Math.cos(rot) as Float * outerRadius
        canvas.drawLine(mBounds!!.centerX(), mBounds!!.centerY(),
                mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                mConnectHandCenterPaint)
        canvas.drawLine(mBounds!!.centerX() + innerX, mBounds!!.centerY() + innerY,
                mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                mHandPaint)
        if (mAmbient && mBurnInProtection && mHollowHandPaint.getStrokeWidth() > 0) {
            canvas.drawLine(mBounds!!.centerX() + innerX, mBounds!!.centerY() + innerY,
                    mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                    mHollowHandPaint)
        }

        /* Hands center */
        canvas.drawLine(mBounds!!.centerX(), mBounds!!.centerY(),
                mBounds!!.centerX() + 0.0001f, mBounds!!.centerY(),
                mHandCenterPaint)

        /* Seconds hand */
        if (!mAmbient) {
            outerRadius = mBounds!!.width() / 2
            innerRadius = mBounds!!.width() * -0.08f
            rot = milliSeconds * Math.PI * 2 / 60000
            innerX = Math.sin(rot) as Float * innerRadius
            innerY = -Math.cos(rot) as Float * innerRadius
            outerX = Math.sin(rot) as Float * outerRadius
            outerY = -Math.cos(rot) as Float * outerRadius
            canvas.drawLine(mBounds!!.centerX() + innerX, mBounds!!.centerY() + innerY,
                    mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                    mSecondsHandPaint)

            /* Seconds center */
            canvas.drawLine(mBounds!!.centerX(), mBounds!!.centerY(),
                    mBounds!!.centerX() + 0.0001f, mBounds!!.centerY(),
                    mSecondsCenterPaint)
        }

        /* Center */
        canvas.drawLine(mBounds!!.centerX(), mBounds!!.centerY(),
                mBounds!!.centerX() + 0.0001f, mBounds!!.centerY(),
                mCenterPaint)
    }

    @Override
    fun setColor(color: Int) {
        mSecondsCenterPaint.setColor(color)
        mSecondsHandPaint.setColor(color)
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
