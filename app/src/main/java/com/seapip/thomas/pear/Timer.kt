package com.seapip.thomas.pear

import android.content.Context
import android.os.PowerManager

import java.util.Calendar
import java.util.Date

import android.content.Context.POWER_SERVICE

class Timer(context: Context) {
    private val mWakeLock: PowerManager.WakeLock
    private var mCalendar: Calendar? = null
    var time: Long = 0
        private set
    var lapTime: Long = 0
        private set
    private var mLapCalendar: Calendar? = null
    var isPaused: Boolean = false
        private set
    private var mPausedCalendar: Calendar? = null
    var isRunning: Boolean = false
        private set

    init {
        mWakeLock = (context.getSystemService(POWER_SERVICE) as PowerManager)
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "PearStopwatchModuleWakeLock")
    }

    fun reset(calendar: Calendar) {
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire()
        }
        mCalendar = calendar.clone() as Calendar
        mLapCalendar = null
        time = 0
        lapTime = -1
        isRunning = true
        isPaused = false
    }

    fun start(calendar: Calendar) {
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire()
        }
        val start = mPausedCalendar!!.getTime()
        val now = calendar.getTime()
        val diff = (now.getTime() - start.getTime()) as Int
        mCalendar!!.add(Calendar.MILLISECOND, diff)
        if (mLapCalendar != null) {
            mLapCalendar!!.add(Calendar.MILLISECOND, diff)
        }
        isPaused = false
    }

    fun pause(calendar: Calendar) {
        if (mWakeLock.isHeld()) {
            mWakeLock.release()
        }
        mPausedCalendar = calendar.clone() as Calendar
        isPaused = true
    }

    fun update(calendar: Calendar) {
        val now = calendar.getTime()
        val start = mCalendar!!.getTime()
        time = now.getTime() - start.getTime()
        if (mLapCalendar != null) {
            val lap = mLapCalendar!!.getTime()
            lapTime = now.getTime() - lap.getTime()
        }
    }

    fun stop() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release()
        }
        isRunning = false
    }

    fun lap(calendar: Calendar) {
        mLapCalendar = calendar.clone() as Calendar
        lapTime = 0
    }
}
