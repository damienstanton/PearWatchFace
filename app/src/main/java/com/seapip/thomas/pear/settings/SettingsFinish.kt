package com.seapip.thomas.pear.settings

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable

import com.seapip.thomas.pear.R

class SettingsFinish(context: Context, bounds: Rect) : SettingsOverlay(bounds, bounds, "", Paint.Align.CENTER) {
    internal var mDrawable: Drawable
    internal var mPaint: Paint
    internal var mBounds: Rect

    init {
        mDrawable = context.getResources().getDrawable(R.drawable.ic_check_black_150dp)
        val size = (bounds.width() * 0.375f) as Int
        mBounds = Rect(bounds.centerX() - size / 2,
                bounds.centerY() - size / 2,
                bounds.centerX() + size / 2,
                bounds.centerY() + size / 2)
        mDrawable.setBounds(mBounds)
        mPaint = Paint()
        mPaint.setAntiAlias(true)
        mPaint.setColor(Color.parseColor("#69F0AE"))
    }

    @Override
    fun draw(canvas: Canvas) {
        canvas.drawCircle(mBounds.centerX(), mBounds.centerY(), mBounds.width() / 2, mPaint)
        mDrawable.draw(canvas)
    }

    @Override
    fun contains(x: Int, y: Int): Boolean {
        if (mBounds.contains(x, y)) {
            getRunnable().run()
        }
        return false
    }
}
