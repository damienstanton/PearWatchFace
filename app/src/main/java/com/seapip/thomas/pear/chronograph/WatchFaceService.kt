package com.seapip.thomas.pear.chronograph

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

import com.seapip.thomas.pear.R
import com.seapip.thomas.pear.Timer
import com.seapip.thomas.pear.module.ButtonModule
import com.seapip.thomas.pear.module.ChronographClockModule
import com.seapip.thomas.pear.module.ChronographTicksModule
import com.seapip.thomas.pear.module.ComplicationModule
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
        private var mTopRightComplicationModule: ComplicationModule? = null
        private var mRightComplicationModule: ComplicationModule? = null
        private var mLeftComplicationModule: ComplicationModule? = null
        private var mBottomLeftComplicationModule: ComplicationModule? = null
        private var mBottomRightComplicationModule: ComplicationModule? = null
        private var mChronographTicksModule: ChronographTicksModule? = null
        private var mStartButtonModule: ButtonModule? = null
        private var mContinueButtonModule: ButtonModule? = null
        private var mPauseButtonModule: ButtonModule? = null
        private var mStopButtonModule: ButtonModule? = null
        private var mLapButtonModule: ButtonModule? = null
        private var mChronographClockModule: ChronographClockModule? = null

        /* Timer */
        private var mTimer: Timer? = null

        @Override
        fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)

            mWatchFaceStyleBuilder = WatchFaceStyle.Builder(this@WatchFaceService)
                    .setStatusBarGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
                    .setViewProtectionMode(WatchFaceStyle.PROTECT_STATUS_BAR)
                    .setAcceptsTapEvents(true)
            setWatchFaceStyle(mWatchFaceStyleBuilder!!.build())

            val context = getApplicationContext()
            mCalendar = Calendar.getInstance()
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context)


            setActiveComplications(COMPLICATION_IDS)

            val scale = mPrefs!!.getInt("settings_chronograph_scale", 60)
            mTimer = Timer(getApplicationContext())

            mTopLeftComplicationModule = ComplicationModule(context)
            mTopRightComplicationModule = ComplicationModule(context)
            mRightComplicationModule = ComplicationModule(context)
            mLeftComplicationModule = ComplicationModule(context)
            mBottomLeftComplicationModule = ComplicationModule(context)
            mBottomRightComplicationModule = ComplicationModule(context)
            mChronographTicksModule = ChronographTicksModule(12)
            mStartButtonModule = ButtonModule(context.getDrawable(R.drawable.ic_timer_black_24dp))
            mContinueButtonModule = ButtonModule(
                    context.getDrawable(R.drawable.ic_continue_black_24dp),
                    context.getDrawable(R.drawable.ic_continue_burninprotection_black_24px))
            mPauseButtonModule = ButtonModule(context.getDrawable(R.drawable.ic_pause_black_24dp))
            mStopButtonModule = ButtonModule(context.getDrawable(R.drawable.ic_stop_black_24dp))
            mLapButtonModule = ButtonModule(context.getDrawable(R.drawable.ic_lap_black_24dp))
            mChronographClockModule = ChronographClockModule(mCalendar, scale)

            mModules = ArrayList()
            mModules!!.add(mTopLeftComplicationModule)
            mModules!!.add(mTopRightComplicationModule)
            mModules!!.add(mRightComplicationModule)
            mModules!!.add(mBottomLeftComplicationModule)
            mModules!!.add(mBottomRightComplicationModule)
            mModules!!.add(mLeftComplicationModule)
            mModules!!.add(mChronographTicksModule)
            mModules!!.add(mStartButtonModule)
            mModules!!.add(mContinueButtonModule)
            mModules!!.add(mPauseButtonModule)
            mModules!!.add(mStopButtonModule)
            mModules!!.add(mLapButtonModule)
            mModules!!.add(mChronographClockModule)

            val color = mPrefs!!.getInt("settings_chronograph_color_value", Color.parseColor("#00BCD4"))
            val accentColor = mPrefs!!.getInt("settings_chronograph_accent_color_value",
                    Color.parseColor("#CDDC39"))
            for (module in mModules!!) {
                module.setColor(Color.parseColor("#747474"))
            }
            mRightComplicationModule!!.setColor(color)
            mLeftComplicationModule!!.setColor(color)
            mStartButtonModule!!.setColor(color)
            mContinueButtonModule!!.setColor(color)
            mPauseButtonModule!!.setColor(accentColor)
            mStopButtonModule!!.setColor(Color.WHITE)
            mLapButtonModule!!.setColor(Color.WHITE)
            mChronographClockModule!!.setColor(accentColor)
            mChronographClockModule!!.setAccentColor(color)
            mChronographClockModule!!.setLapValue(-1)
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
            if (mAmbient && mTimer!!.isRunning() && !mTimer!!.isPaused()) {
                mTimer!!.pause(mCalendar)
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
            mTopLeftComplicationModule!!.setBounds(Rect(screenBounds.left,
                    screenBounds.top,
                    screenBounds.left + size,
                    screenBounds.top + size))
            mTopRightComplicationModule!!.setBounds(Rect(screenBounds.right - size,
                    screenBounds.top,
                    screenBounds.right,
                    screenBounds.top + size))
            mRightComplicationModule!!.setBounds(Rect(bounds.right - offset - size - size / 4,
                    bounds.centerY() - size / 2,
                    bounds.right - offset + size / 4,
                    bounds.centerY() + size / 2))
            mBottomLeftComplicationModule!!.setBounds(Rect(screenBounds.left,
                    screenBounds.bottom - size,
                    screenBounds.left + size,
                    screenBounds.bottom))
            mBottomRightComplicationModule!!.setBounds(Rect(screenBounds.right - size,
                    screenBounds.bottom - size,
                    screenBounds.right,
                    screenBounds.bottom))
            mChronographTicksModule!!.setBounds(bounds)
            val leftDialBounds = Rect(bounds.left + offset,
                    bounds.centerY() - size / 2,
                    bounds.left + offset + size,
                    bounds.centerY() + size / 2)
            mLeftComplicationModule!!.setBounds(leftDialBounds)
            mStartButtonModule!!.setBounds(leftDialBounds)
            mContinueButtonModule!!.setBounds(leftDialBounds)
            mPauseButtonModule!!.setBounds(leftDialBounds)
            val rightDialBounds = Rect(bounds.right - offset - size,
                    bounds.centerY() - size / 2,
                    bounds.right - offset,
                    bounds.centerY() + size / 2)
            mLapButtonModule!!.setBounds(rightDialBounds)
            mStopButtonModule!!.setBounds(rightDialBounds)
            mChronographClockModule!!.setBounds(bounds)
        }

        @Override
        fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            when (tapType) {
                TAP_TYPE_TOUCH -> {
                }
                TAP_TYPE_TOUCH_CANCEL -> {
                }
                TAP_TYPE_TAP -> {
                    // The user has completed the tap gesture.
                    if (!mTimer!!.isRunning()) {
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
                    }
                    if (mStartButtonModule!!.contains(x, y)) {
                        if (mTimer!!.isRunning()) {
                            if (mTimer!!.isPaused()) {
                                mTimer!!.start(mCalendar)
                            } else {
                                mTimer!!.pause(mCalendar)
                            }
                        } else {
                            mTimer!!.reset(mCalendar)
                            val scale = mPrefs!!.getInt("settings_chronograph_scale", 60)
                            mChronographTicksModule!!.setScale(scale)
                        }
                    } else if (mTimer!!.isRunning() && mStopButtonModule!!.contains(x, y)) {
                        if (mTimer!!.isPaused()) {
                            mTimer!!.stop()
                            mChronographTicksModule!!.setScale(12)
                            mChronographClockModule!!.setValue(0)
                            mChronographClockModule!!.setLapValue(-1)
                        } else {
                            mTimer!!.lap(mCalendar)
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
                    mChronographTicksModule!!.setScale(12)
                    mWatchFaceStyleBuilder!!.setHideStatusBar(false)
                    setWatchFaceStyle(mWatchFaceStyleBuilder!!.build())
                    SETTINGS_MODE = 0
                }
                3 -> {
                    if (mTimer!!.isRunning()) {
                        mTimer!!.stop()
                    }
                    setBounds()
                    val scale = mPrefs!!.getInt("settings_chronograph_scale", 60)
                    val color = mPrefs!!.getInt("settings_chronograph_color_value",
                            Color.parseColor("#00BCD4"))
                    val accentColor = mPrefs!!.getInt("settings_chronograph_accent_color_value",
                            Color.parseColor("#CDDC39"))
                    mRightComplicationModule!!.setColor(color)
                    mStartButtonModule!!.setColor(color)
                    mContinueButtonModule!!.setColor(color)
                    mPauseButtonModule!!.setColor(accentColor)
                    mChronographTicksModule!!.setScale(scale)
                    mChronographClockModule!!.setScale(scale)
                    mChronographClockModule!!.setValue(0)
                    mChronographClockModule!!.setLapValue(-1)
                    mChronographClockModule!!.setColor(accentColor)
                    mChronographClockModule!!.setAccentColor(color)
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

            for (module in mModules!!) {
                if (module is ComplicationModule) {
                    (module as ComplicationModule).setCurrentTimeMillis(now)
                }
            }

            canvas.drawColor(Color.BLACK)
            mChronographTicksModule!!.draw(canvas)
            if (mTimer!!.isRunning()) {
                if (mTimer!!.isPaused()) {
                    mContinueButtonModule!!.draw(canvas)
                    mStopButtonModule!!.draw(canvas)
                } else {
                    mTimer!!.update(mCalendar)
                    mChronographClockModule!!.setValue(mTimer!!.getTime())
                    mChronographClockModule!!.setLapValue(mTimer!!.getLapTime())
                    mPauseButtonModule!!.draw(canvas)
                    mLapButtonModule!!.draw(canvas)
                }
            } else {
                if (mAmbient || SETTINGS_MODE > 1) {
                    mLeftComplicationModule!!.draw(canvas)
                } else {
                    mStartButtonModule!!.draw(canvas)
                }
                mRightComplicationModule!!.draw(canvas)
            }
            if (!mIsRound) {
                mTopLeftComplicationModule!!.draw(canvas)
                mTopRightComplicationModule!!.draw(canvas)
                mBottomLeftComplicationModule!!.draw(canvas)
                mBottomRightComplicationModule!!.draw(canvas)
            }
            mChronographClockModule!!.draw(canvas)
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
                if (mTimer!!.isRunning() && !mTimer!!.isPaused()) {
                    mTimer!!.pause(mCalendar)
                }
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
        private val TOP_LEFT_COMPLICATION = 0
        private val TOP_RIGHT_COMPLICATION = 1
        private val RIGHT_COMPLICATION = 2
        private val BOTTOM_LEFT_COMPLICATION = 3
        private val BOTTOM_RIGHT_COMPLICATION = 4
        private val LEFT_COMPLICATION = 5
        val COMPLICATION_IDS = intArrayOf(TOP_LEFT_COMPLICATION, TOP_RIGHT_COMPLICATION, RIGHT_COMPLICATION, BOTTOM_LEFT_COMPLICATION, BOTTOM_RIGHT_COMPLICATION, LEFT_COMPLICATION)
        private val MSG_UPDATE_TIME = 0

        var SETTINGS_MODE = 0

        var ROUND = false
    }
}
