package com.seapip.thomas.pear.settings

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils

class SettingsOverlay(private val mBounds: Rect, private val mScreenBounds: Rect, title: String, private val mAlign: Paint.Align) {
    private var mTitle: String? = null
    var intent: Intent? = null
    var runnable: Runnable? = null
    var data: Intent? = null
    val requestCode: Int
    var active: Boolean = false
        set(active) {
            field = active
            mBoxPaint.setColor(if (active) mActiveColor else mColor)
        }
    private var mRound: Boolean = false
    private var mInsetTitle: Boolean = false
    private var mBottomTitle: Boolean = false
    private var mDisabled: Boolean = false

    /* Colors */
    private val mColor: Int
    private val mActiveColor: Int

    /* Fonts */
    private val mFontBold: Typeface

    /* Paint */
    private val mBoxPaint: Paint
    private val mOverlayRemovePaint: Paint
    private val mTitleTextPaint: TextPaint
    private val mTitlePaint: Paint

    /* Path */
    private var mBoxPath: Path? = null
    private var mTitlePath: Path? = null

    init {
        requestCode = this.hashCode()
        mDisabled = false

        /* Colors */
        mColor = Color.argb(102, 255, 255, 255)
        mActiveColor = Color.parseColor("#69F0AE")

        /* Fonts */
        mFontBold = Typeface.create("sans-serif", Typeface.BOLD)

        /* Paint */
        mBoxPaint = Paint()
        mBoxPaint.setColor(mColor)
        mBoxPaint.setStrokeWidth(2)
        mBoxPaint.setStyle(Paint.Style.STROKE)
        mBoxPaint.setAntiAlias(true)
        mOverlayRemovePaint = Paint()
        mOverlayRemovePaint.setAntiAlias(true)
        mOverlayRemovePaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))
        mTitleTextPaint = TextPaint()
        mTitleTextPaint.setColor(Color.BLACK)
        mTitleTextPaint.setTypeface(mFontBold)
        mTitleTextPaint.setTextSize(18)
        mTitleTextPaint.setAntiAlias(true)
        mTitleTextPaint.setTextAlign(mAlign)
        mTitlePaint = Paint()
        mTitlePaint.setColor(mActiveColor)
        mTitlePaint.setStyle(Paint.Style.FILL)
        mTitlePaint.setAntiAlias(true)

        /* Path */
        mBoxPath = roundedRect(mBounds, 16)
        setTitle(title)
    }

    fun draw(canvas: Canvas) {
        if (!mDisabled) {
            canvas.drawPath(mBoxPath, mOverlayRemovePaint)
            canvas.drawPath(mBoxPath, mBoxPaint)
            if (active && mTitle != null) {
                canvas.drawPath(mTitlePath, mTitlePaint)
                val titleRect = RectF()
                mTitlePath!!.computeBounds(titleRect, false)
                var textX = titleRect.centerX()
                when (mAlign) {
                    LEFT -> textX = titleRect.left + 8
                    RIGHT -> textX = titleRect.right - 8
                }
                canvas.drawText(mTitle!!.toUpperCase(),
                        textX,
                        titleRect.centerY() - (mTitleTextPaint.descent() + mTitleTextPaint.ascent()) / 2,
                        mTitleTextPaint)
            }
        }
    }

    fun contains(x: Int, y: Int): Boolean {
        return if (mDisabled) {
            false
        } else mBounds.contains(x, y)
    }

    fun setTitle(title: String?) {
        mTitle = title
        var width = mTitleTextPaint.measureText(title!!.toUpperCase()) as Int
        if (width > mScreenBounds.width()) {
            width = mScreenBounds.width() - 14
            mTitle = TextUtils.ellipsize(title, mTitleTextPaint, width - 38, TextUtils.TruncateAt.END).toString()
        }
        val titleRect = Rect(mBounds.centerX() - width / 2 - 8,
                mBounds.top - 30,
                mBounds.centerX() + width / 2 + 8,
                mBounds.top - 6)
        when (mAlign) {
            LEFT -> {
                titleRect.left = mBounds.left - 1
                titleRect.right = mBounds.left + width - 1 + 16
            }
            RIGHT -> {
                titleRect.left = mBounds.right - width + 1 - 16
                titleRect.right = mBounds.right + 1
            }
        }
        if (mRound && mInsetTitle) {
            titleRect.top = mBounds.bottom - 50
            titleRect.bottom = mBounds.bottom - 26
        } else if (mInsetTitle) {
            titleRect.top = mBounds.bottom - 30
            titleRect.bottom = mBounds.bottom - 6
        } else if (mBottomTitle) {
            titleRect.top = mBounds.bottom + 6
            titleRect.bottom = mBounds.bottom + 30
        }
        mTitlePath = roundedRect(titleRect, 4)
    }

    fun setDisabled(disabled: Boolean) {
        mDisabled = disabled
    }

    fun setRound(round: Boolean) {
        if (round) {
            mRound = true
            var radius = mBounds.width()
            if (mBounds.height() < radius) {
                radius = mBounds.height()
            }
            mBoxPath = roundedRect(mBounds, radius / 2)
        }
    }

    fun setInsetTitle(insetTitle: Boolean) {
        mInsetTitle = insetTitle
        setTitle(mTitle)
    }

    fun setBottomTitle(bottomTitle: Boolean) {
        mBottomTitle = bottomTitle
        setTitle(mTitle)
    }

    private fun roundedRect(bounds: Rect, radius: Int): Path {
        val path = Path()
        path.moveTo(bounds.left + radius, bounds.top)
        path.lineTo(bounds.right - radius, bounds.top)
        path.arcTo(bounds.right - 2 * radius, bounds.top,
                bounds.right, bounds.top + 2 * radius,
                -90, 90, false)
        path.lineTo(bounds.right, bounds.bottom - radius)
        path.arcTo(bounds.right - 2 * radius, bounds.bottom - 2 * radius,
                bounds.right, bounds.bottom,
                0, 90, false)
        path.lineTo(bounds.left + radius, bounds.bottom)
        path.arcTo(bounds.left, bounds.bottom - 2 * radius,
                bounds.left + 2 * radius, bounds.bottom,
                90, 90, false)
        path.lineTo(bounds.left, bounds.top + radius)
        path.arcTo(bounds.left, bounds.top,
                bounds.left + 2 * radius, bounds.top + 2 * radius,
                180, 90, false)
        return path
    }
}
