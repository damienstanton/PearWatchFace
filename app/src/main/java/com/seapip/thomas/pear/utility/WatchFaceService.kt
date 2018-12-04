package com.seapip.thomas.pear.utility

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.support.wearable.complications.ComplicationData
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.WindowInsets

import com.seapip.thomas.pear.module.AnalogClockModule
import com.seapip.thomas.pear.module.ColorTicksModule
import com.seapip.thomas.pear.module.ComplicationModule
import com.seapip.thomas.pear.module.Module
import com.seapip.thomas.pear.module.UtilityTicksModule

import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.Calendar
import java.util.TimeZone

class WatchFaceService : CanvasWatchFaceService() {

    private var mPrefs: SharedPreferences? = null

    @Override
    fun onCreateEngine(): Engine {
        return Engine()
    }

    private class EngineHandler(reference: WatchFaceService.Engine) : Handler() {
        private val mWeakReference: WeakReference<WatchFaceService.Engine>

        init {
            mWeakReference = WeakReference(reference)
        }

        @Override
        fun handleMessage(msg: Message) {
            val engine = mWeakReference.get()
            if (engine != null) {
                when (msg.what) {
                    MSG_UPDATE_TIME -> engine!!.handleUpdateTimeMessage()
                }
            }
        }
    }

    private inner class Engine : CanvasWatchFaceService.Engine() {
        private val mUpdateTimeHandler = EngineHandler(this)

        private var mCalendar: Calendar? = null
        private val mTimeZoneReceiver = object : BroadcastReceiver() {
            @Override
            fun onReceive(context: Context, intent: Intent) {
                mCalendar!!.setTimeZone(TimeZone.getDefault())
                invalidate()
            }
        }
        private var mRegisteredTimeZoneReceiver = false

        private var mWatchFaceStyleBuilder: WatchFaceStyle.Builder? = null

        /* Display */
        private var mWidth: Int = 0
        private var mHeight: Int = 0
        private var mIsRound: Boolean = false
        private var mAmbient: Boolean = false

        /*Modules */
        private var mModules: ArrayList<Module>? = null
        private var mTopComplicationModule: ComplicationModule? = null
        private var mTopLeftComplicationModule: ComplicationModule? = null
        private var mTopRightComplicationModule: ComplicationModule? = null
        private var mLeftComplicationModule: ComplicationModule? = null
        private var mRightComplicationModule: ComplicationModule? = null
        private var mBottomComplicationModule: ComplicationModule? = null
        private var mBottomLeftComplicationModule: ComplicationModule? = null
        private var mBottomRightComplicationModule: ComplicationModule? = null
        private var mUtilityTicksModule: UtilityTicksModule? = null
        private var mAnalogClockModule: AnalogClockModule? = null

        @Override
        fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)

            mWatchFaceStyleBuilder = WatchFaceStyle.Builder(this@WatchFaceService)
                    .setStatusBarGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
                    .setViewProtectionMode(WatchFaceStyle.PROTECT_STATUS_BAR)
                    .setAcceptsTapEvents(true)

            setWatchFaceStyle(mWatchFaceStyleBuilder!!.build())

            mCalendar = Calendar.getInstance()
            mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())

            setActiveComplications(COMPLICATION_IDS)

            val style = mPrefs!!.getInt("settings_utility_style", 0)

            mTopComplicationModule = ComplicationModule(getApplicationContext())
            mTopLeftComplicationModule = ComplicationModule(getApplicationContext())
            mTopRightComplicationModule = ComplicationModule(getApplicationContext())
            mLeftComplicationModule = ComplicationModule(getApplicationContext())
            mRightComplicationModule = ComplicationModule(getApplicationContext())
            mBottomComplicationModule = ComplicationModule(getApplicationContext())
            mBottomLeftComplicationModule = ComplicationModule(getApplicationContext())
            mBottomRightComplicationModule = ComplicationModule(getApplicationContext())
            mUtilityTicksModule = UtilityTicksModule(style)
            mAnalogClockModule = AnalogClockModule(mCalendar)

            mModules = ArrayList()
            mModules!!.add(mTopComplicationModule)
            mModules!!.add(mTopLeftComplicationModule)
            mModules!!.add(mTopRightComplicationModule)
            mModules!!.add(mLeftComplicationModule)
            mModules!!.add(mRightComplicationModule)
            mModules!!.add(mBottomComplicationModule)
            mModules!!.add(mBottomLeftComplicationModule)
            mModules!!.add(mBottomRightComplicationModule)
            mModules!!.add(mUtilityTicksModule)
            mModules!!.add(mAnalogClockModule)

            val color = mPrefs!!.getInt("settings_utility_color_value", Color.parseColor("#00BCD4"))
            val accentColor = mPrefs!!.getInt("settings_utility_accent_color_value",
                    Color.parseColor("#00BCD4"))
            mUtilityTicksModule!!.setStyle(style)
            for (module in mModules!!) {
                module.setColor(Color.parseColor("#747474"))
            }
            mTopComplicationModule!!.setColor(color)
            mLeftComplicationModule!!.setColor(color)
            mBottomComplicationModule!!.setColor(color)
            mRightComplicationModule!!.setColor(color)
            mAnalogClockModule!!.setColor(accentColor)
        }

        @Override
        fun onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            super.onDestroy()
        }

        @Override
        fun onPropertiesChanged(properties: Bundle) {
            super.onPropertiesChanged(properties)
            for (module in mModules!!) {
                module.setBurnInProtection(properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false))
                module.setLowBitAmbient(properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false))
            }
        }

        @Override
        fun onComplicationDataUpdate(complicationId: Int,
                                     complicationData: ComplicationData) {
            (mModules!!.get(complicationId) as ComplicationModule).setComplicationData(complicationData)
            invalidate()
        }

        @Override
        fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }

        @Override
        fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            mAmbient = inAmbientMode
            for (module in mModules!!) {
                module.setAmbient(inAmbientMode)
            }
            updateTimer()
        }

        @Override
        fun onApplyWindowInsets(insets: WindowInsets) {
            super.onApplyWindowInsets(insets)
            mIsRound = insets.isRound()
            ROUND = mIsRound
            setBounds()
        }

        @Override
        fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            mWidth = width
            mHeight = height
            setBounds()
        }

        private fun setBounds() {
            var inset = 10
            if (SETTINGS_MODE == 3) {
                inset += 20
            }

            val bounds = Rect(inset, inset, mWidth - inset, mHeight - inset)
            val screenBounds = Rect(inset - 5, inset - 5,
                    mWidth - inset + 5, mHeight - inset + 5)

            val offset = (bounds.height() * 0.18f) as Int
            val size = (bounds.width() * 0.20f) as Int
            mTopComplicationModule!!.setBounds(Rect(bounds.centerX() - size / 2,
                    bounds.top + offset,
                    bounds.centerX() + size / 2,
                    bounds.top + offset + size))
            mTopLeftComplicationModule!!.setBounds(Rect(screenBounds.left,
                    screenBounds.top,
                    screenBounds.left + size,
                    screenBounds.top + size))
            mTopRightComplicationModule!!.setBounds(Rect(screenBounds.right - size,
                    screenBounds.top,
                    screenBounds.right,
                    screenBounds.top + size))
            mLeftComplicationModule!!.setBounds(Rect(bounds.left + offset,
                    bounds.centerY() - size / 2,
                    bounds.left + offset + size,
                    bounds.centerY() + size / 2))
            mRightComplicationModule!!.setBounds(Rect(bounds.right - offset - size,
                    bounds.centerY() - size / 2,
                    bounds.right - offset,
                    bounds.centerY() + size / 2))
            mBottomComplicationModule!!.setBounds(Rect(bounds.centerX() - size / 2,
                    bounds.bottom - offset - size,
                    bounds.centerX() + size / 2,
                    bounds.bottom - offset))
            mBottomLeftComplicationModule!!.setBounds(Rect(screenBounds.left,
                    screenBounds.bottom - size,
                    screenBounds.left + size,
                    screenBounds.bottom))
            mBottomRightComplicationModule!!.setBounds(Rect(screenBounds.right - size,
                    screenBounds.bottom - size,
                    screenBounds.right,
                    screenBounds.bottom))
            mUtilityTicksModule!!.setBounds(bounds)
            mAnalogClockModule!!.setBounds(bounds)
        }

        @Override
        fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            when (tapType) {
                TAP_TYPE_TOUCH -> {
                }
                TAP_TYPE_TOUCH_CANCEL -> {
                }
                TAP_TYPE_TAP ->
                    // The user has completed the tap gesture.
                    for (module in mModules!!) {
                        if (module is ComplicationModule && (module as ComplicationModule).contains(x, y)) {
                            val intent = (module as ComplicationModule).getTapAction()
                            if (intent != null) {
                                try {
                                    intent!!.send()
                                } catch (e: PendingIntent.CanceledException) {
                                }

                            }
                        }
                    }
            }// The user has started touching the screen.
            // The user has started a different gesture or otherwise cancelled the tap.
            invalidate()
        }

        @Override
        fun onDraw(canvas: Canvas, bounds: Rect) {
            val now = System.currentTimeMillis()
            mCalendar!!.setTimeInMillis(now)

            when (SETTINGS_MODE) {
                1 -> {
                    setBounds()
                    mWatchFaceStyleBuilder!!.setHideStatusBar(false)
                    setWatchFaceStyle(mWatchFaceStyleBuilder!!.build())
                    SETTINGS_MODE = 0
                }
                3 -> {
                    setBounds()
                    val style = mPrefs!!.getInt("settings_utility_style", 0)
                    val color = mPrefs!!.getInt("settings_utility_color_value",
                            Color.parseColor("#00BCD4"))
                    val accentColor = mPrefs!!.getInt("settings_utility_accent_color_value",
                            Color.parseColor("#CDDC39"))
                    mUtilityTicksModule!!.setStyle(style)
                    mTopComplicationModule!!.setColor(color)
                    mLeftComplicationModule!!.setColor(color)
                    mBottomComplicationModule!!.setColor(color)
                    mRightComplicationModule!!.setColor(color)
                    mAnalogClockModule!!.setColor(accentColor)
                    mWatchFaceStyleBuilder!!.setHideStatusBar(true)
                    setWatchFaceStyle(mWatchFaceStyleBuilder!!.build())
                    SETTINGS_MODE = 2
                }
            }

            if (SETTINGS_MODE > 1) {
                mCalendar!!.set(Calendar.HOUR, 10)
                mCalendar!!.set(Calendar.MINUTE, 10)
                mCalendar!!.set(Calendar.SECOND, 30)
                mCalendar!!.set(Calendar.MILLISECOND, 0)
            }

            canvas.drawColor(Color.BLACK)
            for (module in mModules!!) {
                if (module is ComplicationModule) {
                    (module as ComplicationModule).setCurrentTimeMillis(now)
                }
                module.draw(canvas)
            }
        }

        @Override
        fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                registerReceiver()
                /* Update time zone in case it changed while we weren't visible. */
                mCalendar!!.setTimeZone(TimeZone.getDefault())
                invalidate()
            } else {
                unregisterReceiver()
            }

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer()
        }

        private fun registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = true
            val filter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
            this@WatchFaceService.registerReceiver(mTimeZoneReceiver, filter)
        }

        private fun unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = false
            this@WatchFaceService.unregisterReceiver(mTimeZoneReceiver)
        }

        /**
         * Starts/stops the [.mUpdateTimeHandler] timer based on the state of the watch face.
         */
        private fun updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }

        /**
         * Returns whether the [.mUpdateTimeHandler] timer should be running. The timer
         * should only run in active mode.
         */
        private fun shouldTimerBeRunning(): Boolean {
            return isVisible() && !mAmbient
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private fun handleUpdateTimeMessage() {
            invalidate()
            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }
    }

    companion object {

        val COMPLICATION_SUPPORTED_TYPES = arrayOf(intArrayOf(ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON), intArrayOf(ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON), intArrayOf(ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON), intArrayOf(ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON), intArrayOf(ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON), intArrayOf(ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON), intArrayOf(ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON), intArrayOf(ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON))
        val INTERACTIVE_UPDATE_RATE_MS: Long = 20
        private val TOP_COMPLICATION = 0
        private val TOP_LEFT_COMPLICATION = 1
        private val TOP_RIGHT_COMPLICATION = 2
        private val LEFT_COMPLICATION = 3
        private val RIGHT_COMPLICATION = 4
        private val BOTTOM_COMPLICATION = 5
        private val BOTTOM_LEFT_COMPLICATION = 6
        private val BOTTOM_RIGHT_COMPLICATION = 7
        val COMPLICATION_IDS = intArrayOf(TOP_COMPLICATION, TOP_LEFT_COMPLICATION, TOP_RIGHT_COMPLICATION, LEFT_COMPLICATION, RIGHT_COMPLICATION, BOTTOM_COMPLICATION, BOTTOM_LEFT_COMPLICATION, BOTTOM_RIGHT_COMPLICATION)
        private val MSG_UPDATE_TIME = 0

        var SETTINGS_MODE = 0

        var ROUND = false
    }
}
