package com.seapip.thomas.pear.module

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable

class ButtonModule(private val mDrawable: Drawable) : Module {
    private var mBounds: Rect? = null
    private var mAmbient: Boolean = false
    private var mBurnInProtection: Boolean = false
    private var mDrawableBurnInProtection: Drawable?

    /* Paint */
    private val mCirclePaint: Paint

    init {

        /* Paint */
        mCirclePaint = Paint()
        mCirclePaint.setAntiAlias(true)
    }

    constructor(drawable: Drawable, drawableBurnInProtection: Drawable) : this(drawable) {
        mDrawableBurnInProtection = drawableBurnInProtection
    }

    fun contains(x: Int, y: Int): Boolean {
        return mBounds!!.contains(x, y)
    }

    @Override
    fun setBounds(bounds: Rect) {
        mBounds = bounds
        val size = (bounds.width() * 0.6f) as Int
        mDrawable.setBounds(bounds.centerX() - size / 2,
                bounds.centerY() - size / 2,
                bounds.centerX() + size / 2,
                bounds.centerY() + size / 2)
        if (mDrawableBurnInProtection != null) {
            mDrawableBurnInProtection!!.setBounds(bounds.centerX() - size / 2,
                    bounds.centerY() - size / 2,
                    bounds.centerX() + size / 2,
                    bounds.centerY() + size / 2)
        }
    }

    @Override
    fun draw(canvas: Canvas) {
        if (!(mAmbient && mBurnInProtection)) {
            canvas.drawCircle(mBounds!!.centerX(), mBounds!!.centerY(),
                    mBounds!!.width() / 2, mCirclePaint)
            mDrawable.draw(canvas)
        } else if (mDrawableBurnInProtection != null) {
            mDrawableBurnInProtection!!.draw(canvas)
        } else {
            mDrawable.draw(canvas)
        }
    }

    @Override
    fun setColor(color: Int) {
        mDrawable.setTint(color)
        if (mDrawableBurnInProtection != null) {
            mDrawableBurnInProtection!!.setTint(color)
        }
        mCirclePaint.setColor(color)
        mCirclePaint.setAlpha(52)
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
