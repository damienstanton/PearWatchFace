package com.seapip.thomas.pear.module

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationHelperActivity
import android.support.wearable.complications.ComplicationText

import com.seapip.thomas.pear.DrawableTools
import com.seapip.thomas.pear.modular.WatchFaceService

class ComplicationModule(private val mContext: Context) : Module {
    private var mBounds: Rect? = null
    private var mComplicationData: ComplicationData? = null
    private var mCurrentTimeMillis: Long = 0
    private var mAmbient: Boolean = false
    private var mBurnInProtection: Boolean = false
    private var mLowBitAmbient: Boolean = false
    private var mRangeValue = false

    /* Paint */
    private val mRangeCirclePaint: Paint
    private val mRangeArcPaint: Paint
    private val mRangeValuePaint: Paint
    private val mShortTextTitlePaint: Paint
    private val mShortTextTextPaint: Paint
    private val mShortTextHorizontalTitlePaint: Paint
    private val mShortTextHorizontalTextPaint: Paint
    private val mLongTextTitlePaint: Paint
    private val mLongTextTextPaint: Paint

    val tapAction: PendingIntent?
        get() {
            var intent: PendingIntent? = null
            if (mComplicationData != null) {
                intent = mComplicationData!!.getTapAction()
                if (mComplicationData!!.getType() === ComplicationData.TYPE_NO_PERMISSION) {
                    val componentName = ComponentName(
                            mContext, WatchFaceService::class.java)
                    val permissionRequestIntent = ComplicationHelperActivity.createPermissionRequestHelperIntent(
                            mContext, componentName)
                    mContext.startActivity(permissionRequestIntent)
                } else if (mComplicationData!!.getType() === ComplicationData.TYPE_RANGED_VALUE && intent == null) {
                    mRangeValue = !mRangeValue
                }
            }
            return intent
        }

    init {

        /* Paint */
        mRangeCirclePaint = Paint()
        mRangeCirclePaint.setAntiAlias(true)
        mRangeCirclePaint.setStyle(Paint.Style.STROKE)
        mRangeArcPaint = Paint()
        mRangeArcPaint.setAntiAlias(true)
        mRangeArcPaint.setStyle(Paint.Style.STROKE)
        mRangeArcPaint.setStrokeCap(Paint.Cap.ROUND)
        mRangeValuePaint = Paint()
        mRangeValuePaint.setAntiAlias(true)
        mRangeValuePaint.setTextAlign(Paint.Align.CENTER)
        mShortTextTitlePaint = Paint()
        mShortTextTitlePaint.setAntiAlias(true)
        mShortTextTitlePaint.setTextAlign(Paint.Align.CENTER)
        mShortTextTitlePaint.setColor(Color.WHITE)
        mShortTextTextPaint = Paint()
        mShortTextTextPaint.setAntiAlias(true)
        mShortTextTextPaint.setTextAlign(Paint.Align.CENTER)
        mShortTextHorizontalTitlePaint = Paint()
        mShortTextHorizontalTitlePaint.setAntiAlias(true)
        mShortTextHorizontalTitlePaint.setColor(Color.WHITE)
        mShortTextHorizontalTitlePaint.setTextAlign(Paint.Align.LEFT)
        mShortTextHorizontalTextPaint = Paint()
        mShortTextHorizontalTextPaint.setAntiAlias(true)
        mShortTextHorizontalTextPaint.setTextAlign(Paint.Align.RIGHT)
        mLongTextTitlePaint = Paint()
        mLongTextTitlePaint.setAntiAlias(true)
        mLongTextTextPaint = Paint()
        mLongTextTextPaint.setAntiAlias(true)
        mLongTextTextPaint.setColor(Color.WHITE)
    }

    @Override
    fun setBounds(bounds: Rect) {
        mBounds = bounds
        mRangeCirclePaint.setStrokeWidth(Math.round(bounds.height() * 0.05f))
        mRangeArcPaint.setStrokeWidth(Math.round(bounds.height() * 0.05f))
        mRangeValuePaint.setTextSize(bounds.height() * 0.25f)
        mShortTextTitlePaint.setTextSize(bounds.height() * 0.22f)
        mLongTextTitlePaint.setTextSize(bounds.height() * 0.35f)
        mLongTextTextPaint.setTextSize(bounds.height() * 0.30f)
    }

    @Override
    fun draw(canvas: Canvas) {
        if (mComplicationData != null && mComplicationData!!.isActive(mCurrentTimeMillis)) {
            when (mComplicationData!!.getType()) {
                ComplicationData.TYPE_RANGED_VALUE -> drawRangedValue(canvas)
                ComplicationData.TYPE_SHORT_TEXT -> if (mBounds!!.width() > mBounds!!.height()) {
                    drawShortTextHorizontal(canvas)
                } else {
                    drawShortText(canvas)
                }
                ComplicationData.TYPE_LONG_TEXT -> drawLongText(canvas)
                ComplicationData.TYPE_SMALL_IMAGE -> drawSmallImage(canvas)
                ComplicationData.TYPE_ICON -> drawIcon(canvas)
            }
        }
    }

    private fun drawRangedValue(canvas: Canvas) {
        val `val` = mComplicationData!!.getValue()
        val min = mComplicationData!!.getMinValue()
        val max = mComplicationData!!.getMaxValue()
        val icon = if (mAmbient && mBurnInProtection &&
                mComplicationData!!.getBurnInProtectionIcon() != null)
            mComplicationData!!.getBurnInProtectionIcon()
        else
            mComplicationData!!.getIcon()

        canvas.drawCircle(mBounds!!.centerX(), mBounds!!.centerY(),
                mBounds!!.height() * 0.32f - mRangeCirclePaint.getStrokeWidth() / 2,
                mRangeCirclePaint)

        val padding = 0.18f * mBounds!!.height() + mRangeCirclePaint.getStrokeWidth() / 2
        canvas.drawArc(mBounds!!.left + padding, mBounds!!.top + padding,
                mBounds!!.right - padding, mBounds!!.bottom - padding,
                -90, (360 * (`val` - min) / (max - min)).toFloat(), false, mRangeArcPaint)

        if (mRangeValue || icon == null) {
            var valString = String.valueOf(`val`.toInt())
            if (`val` > 99999) {
                valString = String.valueOf(`val` / 1000000).substring(0, 3) + "M"
            } else if (`val` > 999) {
                valString = String.valueOf(`val` / 1000).substring(0, 3)
                valString += "K"
            }
            if (valString.length() > 2 && valString.charAt(2) === '.') {
                valString = valString.substring(0, 2) + valString.substring(3, valString.length())
            }
            canvas.drawText(valString, mBounds!!.centerX(),
                    mBounds!!.centerY() - (mRangeValuePaint.descent() + mRangeValuePaint.ascent()) / 2,
                    mRangeValuePaint)
        } else {
            val drawable = icon.loadDrawable(mContext)
            if (drawable != null) {
                drawable!!.setTint(mRangeArcPaint.getColor())
                val size = (mBounds!!.height() * 0.3) as Int
                drawable!!.setBounds(mBounds!!.centerX() - size / 2, mBounds!!.centerY() - size / 2,
                        mBounds!!.centerX() + size / 2, mBounds!!.centerY() + size / 2)
                drawable!!.draw(canvas)
            }
        }
    }

    private fun drawShortTextHorizontal(canvas: Canvas) {
        val title = mComplicationData!!.getShortTitle()
        val text = mComplicationData!!.getShortText()
        val textString = text.getText(mContext, mCurrentTimeMillis).toString().toUpperCase()
        val padding = 0.18f * mBounds!!.height()

        if (title != null) {
            val titleString = title!!.getText(mContext, mCurrentTimeMillis).toString().toUpperCase()
            if (titleString.length() > 3) {
                drawShortText(canvas)
                return
            }
            val titleWidth = mLongTextTitlePaint.measureText(titleString, 0, titleString.length())
            val textWidth = mLongTextTitlePaint.measureText(textString, 0, textString.length())
            val center = textWidth / (titleWidth + textWidth) * (mBounds!!.width() * 0.95f - padding * 2)
            val scale = center / textWidth
            mShortTextHorizontalTitlePaint.setTextSize(mLongTextTitlePaint.getTextSize() * scale)
            mShortTextHorizontalTextPaint.setTextSize(mLongTextTitlePaint.getTextSize() * scale)
            val y = mBounds!!.centerY() - (mShortTextHorizontalTextPaint.descent() + mShortTextHorizontalTextPaint.ascent()) / 2
            canvas.drawText(titleString,
                    mBounds!!.left + padding,
                    y,
                    mShortTextHorizontalTitlePaint)
            canvas.drawText(textString,
                    mBounds!!.right - padding,
                    y,
                    mShortTextHorizontalTextPaint)
        } else {
            drawShortText(canvas)
        }

    }

    private fun drawShortText(canvas: Canvas) {
        val title = mComplicationData!!.getShortTitle()
        val text = mComplicationData!!.getShortText()
        val icon = if (mAmbient && mBurnInProtection &&
                mComplicationData!!.getBurnInProtectionIcon() != null)
            mComplicationData!!.getBurnInProtectionIcon()
        else
            mComplicationData!!.getIcon()

        var textY = mBounds!!.bottom - mBounds!!.height() * 0.2f
        var textPaint = mShortTextTextPaint

        if (icon != null) {
            val drawable = icon.loadDrawable(mContext)
            if (drawable != null) {
                drawable!!.setTint(mShortTextTextPaint.getColor())
                val size = (mBounds!!.height() * 0.4) as Int
                drawable!!.setBounds(mBounds!!.centerX() - size / 2,
                        mBounds!!.top + (mBounds!!.height() * 0.15f) as Int,
                        mBounds!!.centerX() + size / 2,
                        mBounds!!.top + (mBounds!!.height() * 0.15f) as Int + size)
                drawable!!.draw(canvas)
            }
            textPaint = mShortTextTitlePaint
        } else if (title != null) {
            val titleString = title!!.getText(mContext, mCurrentTimeMillis).toString().toUpperCase()
            canvas.drawText(titleString,
                    mBounds!!.centerX(),
                    mBounds!!.top + mBounds!!.height() * 0.2f - mShortTextTitlePaint.descent() - mShortTextTitlePaint.ascent(),
                    mShortTextTitlePaint)
        } else {
            textY = mBounds!!.centerY() - (mShortTextTextPaint.descent() + mShortTextTextPaint.ascent()) / 2
        }

        val textString = text.getText(mContext, mCurrentTimeMillis).toString().toUpperCase()
        mShortTextTextPaint.setTextSize(mBounds!!.height() * (0.60f - 0.06f * if (textString.length() === 1) 2 else textString.length()))
        canvas.drawText(textString,
                mBounds!!.centerX(),
                textY,
                textPaint)
    }

    private fun drawLongText(canvas: Canvas) {
        val title = mComplicationData!!.getLongTitle()
        val text = mComplicationData!!.getLongText()

        var textY = mBounds!!.centerY() - (mShortTextTextPaint.descent() + mShortTextTextPaint.ascent()) / 2

        if (title != null) {
            val titleString = title!!.getText(mContext, mCurrentTimeMillis).toString()
            canvas.drawText(titleString,
                    mBounds!!.left + 0.20f * mBounds!!.height(),
                    mBounds!!.top + mBounds!!.height() * 0.20f - mLongTextTitlePaint.descent() - mLongTextTitlePaint.ascent(),
                    mLongTextTitlePaint)
            textY = mBounds!!.bottom - mBounds!!.height() * 0.20f
        }

        val textString = text.getText(mContext, mCurrentTimeMillis).toString()
        canvas.drawText(textString,
                mBounds!!.left + 0.20f * mBounds!!.height(),
                textY,
                mLongTextTextPaint)
    }

    private fun drawSmallImage(canvas: Canvas) {
        val icon = mComplicationData!!.getSmallImage()
        if (icon != null && !(mAmbient && mBurnInProtection)) {
            var drawable = icon!!.loadDrawable(mContext)
            if (drawable != null) {
                val size = (mBounds!!.height() * 0.64) as Int
                if (mAmbient) {
                    drawable = DrawableTools.convertToGrayscale(drawable)
                }
                if (mComplicationData!!.getImageStyle() === ComplicationData.IMAGE_STYLE_PHOTO) {
                    drawable = DrawableTools.convertToCircle(drawable)
                }
                drawable!!.setBounds(mBounds!!.centerX() - size / 2, mBounds!!.centerY() - size / 2,
                        mBounds!!.centerX() + size / 2, mBounds!!.centerY() + size / 2)
                drawable!!.draw(canvas)
            }
        }
    }

    private fun drawIcon(canvas: Canvas) {
        val icon = if (mAmbient && mBurnInProtection &&
                mComplicationData!!.getBurnInProtectionIcon() != null)
            mComplicationData!!.getBurnInProtectionIcon()
        else
            mComplicationData!!.getIcon()
        if (icon != null) {
            val drawable = icon.loadDrawable(mContext)
            if (drawable != null) {
                val size = (mBounds!!.height() * 0.4) as Int
                drawable!!.setTint(mShortTextTextPaint.getColor())
                drawable!!.setBounds(mBounds!!.centerX() - size / 2, mBounds!!.centerY() - size / 2,
                        mBounds!!.centerX() + size / 2, mBounds!!.centerY() + size / 2)
                drawable!!.draw(canvas)
            }
        }
    }

    @Override
    fun setColor(color: Int) {
        mRangeCirclePaint.setColor(Color.argb(64,
                Color.red(color),
                Color.green(color),
                Color.blue(color)))
        mRangeArcPaint.setColor(color)
        mRangeValuePaint.setColor(color)
        mShortTextTextPaint.setColor(color)
        mShortTextHorizontalTextPaint.setColor(color)
        mLongTextTitlePaint.setColor(color)
    }

    fun setComplicationData(complicationData: ComplicationData) {
        mComplicationData = complicationData
    }

    fun setCurrentTimeMillis(currentTimeMillis: Long) {
        mCurrentTimeMillis = currentTimeMillis
    }

    fun contains(x: Int, y: Int): Boolean {
        return mBounds!!.contains(x, y)
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
        mLowBitAmbient = lowBitAmbient
    }
}
