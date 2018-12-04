package com.seapip.thomas.pear.modular

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
import android.text.format.DateFormat
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.WindowInsets

import com.seapip.thomas.pear.module.ComplicationModule
import com.seapip.thomas.pear.module.DigitalClockModule
import com.seapip.thomas.pear.module.Module

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
        private var mTopLeftComplicationModule: ComplicationModule? = null
        private var mCenterComplicationModule: ComplicationModule? = null
        private var mBottomLeftComplicationModule: ComplicationModule? = null
        private var mBottomCenterComplicationModule: ComplicationModule? = null
        private var mBottomRightComplicationModule: ComplicationModule? = null
        private var mDigitalClockModule: DigitalClockModule? = null

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

            mTopLeftComplicationModule = ComplicationModule(getApplicationContext())
            mCenterComplicationModule = ComplicationModule(getApplicationContext())
            mBottomLeftComplicationModule = ComplicationModule(getApplicationContext())
            mBottomCenterComplicationModule = ComplicationModule(getApplicationContext())
            mBottomRightComplicationModule = ComplicationModule(getApplicationContext())
            mDigitalClockModule = DigitalClockModule(mCalendar,
                    DateFormat.is24HourFormat(this@WatchFaceService))

            mModules = ArrayList()
            mModules!!.add(mTopLeftComplicationModule)
            mModules!!.add(mCenterComplicationModule)
            mModules!!.add(mBottomLeftComplicationModule)
            mModules!!.add(mBottomCenterComplicationModule)
            mModules!!.add(mBottomRightComplicationModule)
            mModules!!.add(mDigitalClockModule)

            val color = mPrefs!!.getInt("settings_modular_color_value",
                    Color.parseColor("#00BCD4"))
            for (module in mModules!!) {
                module.setColor(color)
            }
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
            var inset = if (mIsRound) (mWidth - Math.sqrt(mWidth * mWidth / 2) as Int) / 2 else MODULE_SPACING
            if (SETTINGS_MODE == 3) {
                inset += 20
            }

            val bounds = Rect(inset, inset, mWidth - inset, mHeight - inset)

            mTopLeftComplicationModule!!.setBounds(Rect(
                    bounds.left,
                    bounds.top,
                    bounds.left + (bounds.width() - MODULE_SPACING * 2) / 3,
                    bounds.top + (bounds.height() - MODULE_SPACING * 2) / 3)
            )
            mCenterComplicationModule!!.setBounds(Rect(
                    bounds.left,
                    bounds.top + (bounds.height() - MODULE_SPACING * 2) / 3 + MODULE_SPACING,
                    bounds.right,
                    bounds.bottom - (bounds.height() - MODULE_SPACING * 2) / 3 - MODULE_SPACING)
            )
            mBottomLeftComplicationModule!!.setBounds(Rect(
                    bounds.left,
                    bounds.bottom - (bounds.height() - MODULE_SPACING * 2) / 3,
                    bounds.left + (bounds.width() - MODULE_SPACING * 2) / 3,
                    bounds.bottom)
            )
            mBottomCenterComplicationModule!!.setBounds(Rect(
                    bounds.left + (bounds.width() - MODULE_SPACING * 2) / 3 + MODULE_SPACING,
                    bounds.bottom - (bounds.height() - MODULE_SPACING * 2) / 3,
                    bounds.right - (bounds.width() - MODULE_SPACING * 2) / 3 - MODULE_SPACING,
                    bounds.bottom
            ))
            mBottomRightComplicationModule!!.setBounds(Rect(
                    bounds.right - (bounds.width() - MODULE_SPACING * 2) / 3,
                    bounds.bottom - (bounds.height() - MODULE_SPACING * 2) / 3,
                    bounds.right,
                    bounds.bottom)
            )
            mDigitalClockModule!!.setBounds(Rect(
                    bounds.right - (bounds.width() - MODULE_SPACING * 2) / 3 * 2 - MODULE_SPACING,
                    bounds.top,
                    bounds.right,
                    bounds.top + bounds.height() / 3 - MODULE_SPACING / 2)
            )

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
                    val color = mPrefs!!.getInt("settings_modular_color_value", Color.parseColor("#00BCD4"))
                    for (module in mModules!!) {
                        module.setColor(color)
                    }
                    setBounds()
                    mWatchFaceStyleBuilder!!.setHideStatusBar(true)
                    setWatchFaceStyle(mWatchFaceStyleBuilder!!.build())
                    SETTINGS_MODE = 2
                }
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
                mDigitalClockModule!!.setTimeFormat24(
                        DateFormat.is24HourFormat(this@WatchFaceService))
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

        val COMPLICATION_SUPPORTED_TYPES = arrayOf(intArrayOf(ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON), intArrayOf(ComplicationData.TYPE_LONG_TEXT), intArrayOf(ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON), intArrayOf(ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON), intArrayOf(ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON))
        val MODULE_SPACING = 10

        private val TOP_LEFT_COMPLICATION = 0
        private val CENTER_COMPLICATION = 1
        private val BOTTOM_LEFT_COMPLICATION = 2
        private val BOTTOM_CENTER_COMPLICATION = 3
        private val BOTTOM_RIGHT_COMPLICATION = 4
        val COMPLICATION_IDS = intArrayOf(TOP_LEFT_COMPLICATION, CENTER_COMPLICATION, BOTTOM_LEFT_COMPLICATION, BOTTOM_CENTER_COMPLICATION, BOTTOM_RIGHT_COMPLICATION)
        private val INTERACTIVE_UPDATE_RATE_MS: Long = 32
        private val MSG_UPDATE_TIME = 0

        var SETTINGS_MODE = 0

        var ROUND = false
    }
}
