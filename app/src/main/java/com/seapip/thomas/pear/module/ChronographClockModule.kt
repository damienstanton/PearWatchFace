package com.seapip.thomas.pear.module

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

import java.util.Calendar

class ChronographClockModule(private val mCalendar: Calendar, private var mScale: Int) : Module {
    private var mBounds: Rect? = null
    private var mAmbient: Boolean = false
    private var mBurnInProtection: Boolean = false
    private var mValue: Long = 0
    private var mLapValue: Long = 0

    /* Paint */
    private val mTickPaint: Paint
    private val mValueTickPaint: Paint
    private val mHandPaint: Paint
    private val mHollowHandPaint: Paint
    private val mConnectHandCenterPaint: Paint
    private val mHandCenterPaint: Paint
    private val mValueCenterPaint: Paint
    private val mLapValueCenterPaint: Paint
    private val mMinuteValueCenterPaint: Paint
    private val mLapMinuteValueCenterPaint: Paint
    private val mValueHandPaint: Paint
    private val mLapValueHandPaint: Paint
    private val mCenterPaint: Paint
    private val mSecondsHandPaint: Paint
    private val mSecondsCenterPaint: Paint
    private val mTextPaint: Paint

    init {
        mValue = 0

        /* Paint */
        mTickPaint = Paint()
        mTickPaint.setAntiAlias(true)
        mTickPaint.setColor(Color.parseColor("#707070"))
        mValueTickPaint = Paint()
        mValueTickPaint.setAntiAlias(true)
        mValueTickPaint.setColor(Color.WHITE)
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
        mValueCenterPaint = Paint()
        mValueCenterPaint.setAntiAlias(true)
        mValueCenterPaint.setStrokeCap(Paint.Cap.ROUND)
        mLapValueCenterPaint = Paint()
        mLapValueCenterPaint.setAntiAlias(true)
        mLapValueCenterPaint.setStrokeCap(Paint.Cap.ROUND)
        mMinuteValueCenterPaint = Paint()
        mMinuteValueCenterPaint.setAntiAlias(true)
        mMinuteValueCenterPaint.setStrokeCap(Paint.Cap.ROUND)
        mLapMinuteValueCenterPaint = Paint()
        mLapMinuteValueCenterPaint.setAntiAlias(true)
        mLapMinuteValueCenterPaint.setStrokeCap(Paint.Cap.ROUND)
        mValueHandPaint = Paint()
        mValueHandPaint.setAntiAlias(true)
        mLapValueHandPaint = Paint()
        mLapValueHandPaint.setAntiAlias(true)
        mCenterPaint = Paint()
        mCenterPaint.setAntiAlias(true)
        mCenterPaint.setColor(Color.BLACK)
        mCenterPaint.setStrokeCap(Paint.Cap.ROUND)
        mSecondsHandPaint = Paint()
        mSecondsHandPaint.setAntiAlias(true)
        mSecondsHandPaint.setColor(Color.WHITE)
        mSecondsCenterPaint = Paint()
        mSecondsCenterPaint.setAntiAlias(true)
        mSecondsCenterPaint.setColor(Color.WHITE)
        mSecondsCenterPaint.setStrokeCap(Paint.Cap.ROUND)
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
        var stroke = (bounds.width() * 0.042f) as Int
        if (stroke % 2 != 0) {
            stroke--
        }
        mHandPaint.setStrokeWidth(stroke)
        mHollowHandPaint.setStrokeWidth(stroke - 6)
        stroke = bounds.width() * 0.016f
        if (stroke % 2 != 0) {
            stroke--
        }
        mConnectHandCenterPaint.setStrokeWidth(stroke)
        stroke = bounds.width() * 0.048f
        if (stroke % 2 != 0) {
            stroke--
        }
        mHandCenterPaint.setStrokeWidth(stroke)
        stroke = bounds.width() * 0.036f
        if (stroke % 2 != 0) {
            stroke--
        }
        mValueCenterPaint.setStrokeWidth(stroke)
        mLapValueCenterPaint.setStrokeWidth(stroke)
        mValueHandPaint.setStrokeWidth(bounds.width() * 0.007f)
        mLapValueHandPaint.setStrokeWidth(bounds.width() * 0.007f)
        stroke = bounds.width() * 0.012f
        if (stroke % 2 != 0) {
            stroke--
        }
        mCenterPaint.setStrokeWidth(stroke)
        mSecondsHandPaint.setStrokeWidth(bounds.width() * 0.007f)
        stroke = bounds.width() * 0.022f
        if (stroke % 2 != 0) {
            stroke--
        }
        mMinuteValueCenterPaint.setStrokeWidth(stroke)
        mLapMinuteValueCenterPaint.setStrokeWidth(stroke)
        mSecondsCenterPaint.setStrokeWidth(stroke)
        mTextPaint.setTextSize(bounds.width() * 0.04f)
    }

    @Override
    fun draw(canvas: Canvas) {
        val hours = mCalendar.get(Calendar.HOUR)
        val minutes = mCalendar.get(Calendar.MINUTE)
        val milliSeconds = (if (mAmbient)
            0
        else
            mCalendar.get(Calendar.SECOND) * 1000 + mCalendar.get(Calendar.MILLISECOND)).toFloat()

        /* Value minutes dial */
        val dialY = mBounds!!.height() * 0.19f
        var outerRadius = mBounds!!.height() * 0.14f
        val textRadius = outerRadius - mBounds!!.width() * 0.065f
        for (i in 0..59) {
            val rot = (i * Math.PI * 2 / 60) as Float
            val x = Math.sin(rot) as Float
            val y = -Math.cos(rot) as Float
            val valueTick = (mScale == 3 && i % 10 == 0
                    || (mScale == 6 || mScale == 30) && i % 4 == 0
                    || mScale == 60 && i % 10 == 0)
            val innerRadius = outerRadius - mBounds!!.height() * if (i % 2 == 0) 0.024f else 0.016f
            canvas.drawLine(mBounds!!.centerX() + x * innerRadius,
                    mBounds!!.centerY() - dialY + y * innerRadius,
                    mBounds!!.centerX() + x * outerRadius,
                    mBounds!!.centerY() - dialY + y * outerRadius,
                    if (valueTick) mValueTickPaint else mTickPaint)
            val scale = if (mScale == 3) 180 else mScale
            if (mScale == 3 && i % 10 == 0
                    || mScale == 6 && i % 20 == 0
                    || mScale == 30 && i % 12 == 0
                    || mScale == 60 && i % 10 == 0) {
                canvas.drawText(String.valueOf(if (i == 0) scale / 2 else scale * i / 60 / 2),
                        mBounds!!.centerX() + x * textRadius,
                        mBounds!!.centerY() - dialY + y * textRadius - (mTextPaint.descent() + mTextPaint.ascent()) / 2,
                        mTextPaint)
            }
        }

        /* Value minutes dial */
        var rot = (mValue * Math.PI * 2 / (mScale * 30000)) as Float
        var outerX = Math.sin(rot) as Float * outerRadius
        var outerY = -Math.cos(rot) as Float * outerRadius
        canvas.drawLine(mBounds!!.centerX(),
                mBounds!!.centerY() - dialY,
                mBounds!!.centerX() + outerX,
                mBounds!!.centerY() - dialY + outerY,
                mValueHandPaint)
        if (mLapValue > -1) {
            rot = mLapValue * Math.PI * 2 / (mScale * 30000)
            outerX = Math.sin(rot) as Float * outerRadius
            outerY = -Math.cos(rot) as Float * outerRadius
            canvas.drawLine(mBounds!!.centerX(),
                    mBounds!!.centerY() - dialY,
                    mBounds!!.centerX() + outerX,
                    mBounds!!.centerY() - dialY + outerY,
                    mLapValueHandPaint)
        }

        /* Value minutes dial center */
        canvas.drawLine(mBounds!!.centerX(), mBounds!!.centerY() - dialY,
                mBounds!!.centerX() + 0.0001f, mBounds!!.centerY() - dialY,
                if (mLapValue < 0) mMinuteValueCenterPaint else mLapMinuteValueCenterPaint)

        /* Seconds dial */
        for (i in 0..59) {
            rot = i * Math.PI * 2 / 60
            val x = Math.sin(rot) as Float
            val y = -Math.cos(rot) as Float
            val innerRadius = outerRadius - mBounds!!.height() * if (i % 5 == 0) 0.024f else 0.016f
            canvas.drawLine(mBounds!!.centerX() + x * innerRadius,
                    mBounds!!.centerY() + dialY + y * innerRadius,
                    mBounds!!.centerX() + x * outerRadius,
                    mBounds!!.centerY() + dialY + y * outerRadius,
                    if (i % 5 == 0) mValueTickPaint else mTickPaint)
            if (i % 15 == 0) {
                canvas.drawText(String.valueOf(if (i == 0) 60 else i),
                        mBounds!!.centerX() + x * textRadius,
                        mBounds!!.centerY() + dialY + y * textRadius - (mTextPaint.descent() + mTextPaint.ascent()) / 2,
                        mTextPaint)
            }
        }

        /* Seconds hand */
        if (!mAmbient) {
            rot = milliSeconds * Math.PI * 2 / 60000
            outerX = Math.sin(rot) as Float * outerRadius
            outerY = -Math.cos(rot) as Float * outerRadius
            canvas.drawLine(mBounds!!.centerX(),
                    mBounds!!.centerY() + dialY,
                    mBounds!!.centerX() + outerX,
                    mBounds!!.centerY() + dialY + outerY,
                    mSecondsHandPaint)

            /* Seconds center */
            canvas.drawLine(mBounds!!.centerX(), mBounds!!.centerY() + dialY,
                    mBounds!!.centerX() + 0.0001f, mBounds!!.centerY() + dialY,
                    mSecondsCenterPaint)
        }


        /* Hour hand */
        outerRadius = mBounds!!.width() * 0.28f - mHandPaint.getStrokeWidth()
        var innerRadius = mBounds!!.width() * 0.04f + mHandPaint.getStrokeWidth()
        rot = (hours * 5 + minutes / 12) * Math.PI * 2 / 60
        var innerX = Math.sin(rot) as Float * innerRadius
        var innerY = -Math.cos(rot) as Float * innerRadius
        outerX = Math.sin(rot) as Float * outerRadius
        outerY = -Math.cos(rot) as Float * outerRadius
        canvas.drawLine(mBounds!!.centerX(), mBounds!!.centerY(),
                mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                mConnectHandCenterPaint)
        canvas.drawLine(mBounds!!.centerX() + innerX, mBounds!!.centerY() + innerY,
                mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                mHandPaint)
        if (mHollowHandPaint.getStrokeWidth() > 0) {
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
        if (mHollowHandPaint.getStrokeWidth() > 0) {
            canvas.drawLine(mBounds!!.centerX() + innerX, mBounds!!.centerY() + innerY,
                    mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                    mHollowHandPaint)
        }

        /* Hands center */
        canvas.drawLine(mBounds!!.centerX(), mBounds!!.centerY(),
                mBounds!!.centerX() + 0.0001f, mBounds!!.centerY(),
                mHandCenterPaint)

        /* Value hand */
        outerRadius = mBounds!!.width() / 2
        innerRadius = mBounds!!.width() * -0.08f
        rot = mValue * Math.PI * 2 / mScale / 1000
        innerX = Math.sin(rot) as Float * innerRadius
        innerY = -Math.cos(rot) as Float * innerRadius
        outerX = Math.sin(rot) as Float * outerRadius
        outerY = -Math.cos(rot) as Float * outerRadius
        canvas.drawLine(mBounds!!.centerX() + innerX, mBounds!!.centerY() + innerY,
                mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                mValueHandPaint)
        if (mLapValue > -1) {
            rot = mLapValue * Math.PI * 2 / mScale / 1000
            innerX = Math.sin(rot) as Float * innerRadius
            innerY = -Math.cos(rot) as Float * innerRadius
            outerX = Math.sin(rot) as Float * outerRadius
            outerY = -Math.cos(rot) as Float * outerRadius
            canvas.drawLine(mBounds!!.centerX() + innerX, mBounds!!.centerY() + innerY,
                    mBounds!!.centerX() + outerX, mBounds!!.centerY() + outerY,
                    mLapValueHandPaint)
        }

        /* Value center */
        canvas.drawLine(mBounds!!.centerX(), mBounds!!.centerY(),
                mBounds!!.centerX() + 0.0001f, mBounds!!.centerY(),
                if (mLapValue < 0) mValueCenterPaint else mLapValueCenterPaint)

        /* Center */
        canvas.drawLine(mBounds!!.centerX(), mBounds!!.centerY(),
                mBounds!!.centerX() + 0.0001f, mBounds!!.centerY(),
                mCenterPaint)
    }

    fun setValue(value: Long) {
        mValue = value
    }

    fun setLapValue(lapValue: Long) {
        mLapValue = lapValue
    }

    fun setScale(scale: Int) {
        mScale = scale
    }

    @Override
    fun setColor(color: Int) {
        mValueCenterPaint.setColor(color)
        mValueHandPaint.setColor(color)
        mMinuteValueCenterPaint.setColor(color)
    }

    fun setAccentColor(accentColor: Int) {
        mLapValueCenterPaint.setColor(accentColor)
        mLapValueHandPaint.setColor(accentColor)
        mLapMinuteValueCenterPaint.setColor(accentColor)
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
