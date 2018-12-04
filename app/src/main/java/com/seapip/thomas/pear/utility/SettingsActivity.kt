package com.seapip.thomas.pear.utility

import android.content.ComponentName
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.wearable.complications.ComplicationProviderInfo
import android.support.wearable.complications.ProviderInfoRetriever
import android.support.wearable.view.GridViewPager
import android.util.DisplayMetrics

import com.seapip.thomas.pear.R
import com.seapip.thomas.pear.settings.SettingsAdapter
import com.seapip.thomas.pear.settings.SettingsFinish
import com.seapip.thomas.pear.settings.SettingsOverlay
import com.seapip.thomas.pear.settings.SettingsPage
import com.seapip.thomas.pear.settings.SettingsRow

import java.util.ArrayList
import java.util.concurrent.Executor

class SettingsActivity : com.seapip.thomas.pear.settings.SettingsActivity() {
    @get:Override
    var adapter: SettingsAdapter? = null
        private set
    private var mComplicationModules: ArrayList<SettingsOverlay>? = null
    private var mProviderInfoRetriever: ProviderInfoRetriever? = null

    @Override
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext().getApplicationContext())
        adapter = object : SettingsAdapter(getFragmentManager()) {
            @Override
            fun initPages(): ArrayList<SettingsRow> {
                val pages = ArrayList()

                val metrics = getApplicationContext().getResources().getDisplayMetrics()
                val width = metrics.widthPixels
                val height = metrics.heightPixels
                val inset = 20
                val bounds = Rect(inset, inset, width - inset, height - inset)
                val insetBounds = Rect(30, 30, width - 30, height - 30)
                val screenBounds = Rect(25, 25, width - 25, height - 25)


                val styleModules = ArrayList()
                val styleOverlay = SettingsOverlay(bounds, bounds, "Detail",
                        Paint.Align.CENTER)
                styleOverlay.setRound(true)
                styleOverlay.setInsetTitle(true)
                styleOverlay.setActive(true)
                styleOverlay.setRunnable(object : Runnable() {
                    @Override
                    fun run() {
                        var style = preferences.getInt("settings_utility_style", 0)
                        style++
                        style = if (style > 3) 0 else style
                        preferences.edit().putInt("settings_utility_style", style).apply()
                        setSettingsMode(true)
                    }
                })
                styleModules.add(styleOverlay)

                val colorModules = ArrayList()
                val colorOverlay = SettingsOverlay(Rect(
                        bounds.left + (0.18f * bounds.width()) as Int,
                        bounds.top + (0.18f * bounds.height()) as Int,
                        bounds.right - (0.18f * bounds.width()) as Int,
                        bounds.bottom - (0.18f * bounds.height()) as Int),
                        bounds, "Color", Paint.Align.CENTER)
                setColorOverlay(colorOverlay,
                        "settings_utility_color_name",
                        "settings_utility_color_value",
                        "Cyan",
                        Color.parseColor("#00BCD4"))
                colorOverlay.setRound(true)
                colorOverlay.setActive(true)
                colorModules.add(colorOverlay)

                val accentColorModules = ArrayList()
                val accentColorOverlay = SettingsOverlay(
                        Rect(bounds.centerX() - (bounds.width() * 0.08f) as Int,
                                bounds.bottom - (bounds.height() * 0.60f) as Int,
                                bounds.centerX() + (bounds.width() * 0.08f) as Int,
                                bounds.bottom),
                        bounds, "Color", Paint.Align.CENTER)
                setColorOverlay(accentColorOverlay,
                        "settings_utility_accent_color_name",
                        "settings_utility_accent_color_value",
                        "Lime",
                        Color.parseColor("#CDDC39"))
                accentColorOverlay.setRound(true)
                accentColorOverlay.setActive(true)
                accentColorModules.add(accentColorOverlay)

                mComplicationModules = ArrayList()
                val offset = (insetBounds.height() * 0.18f) as Int
                val size = (insetBounds.width() * 0.20f) as Int
                val topComplicationOverlay = SettingsOverlay(
                        Rect(insetBounds.centerX() - size / 2,
                                insetBounds.top + offset,
                                insetBounds.centerX() + size / 2,
                                insetBounds.top + offset + size),
                        bounds,
                        "Off",
                        Paint.Align.CENTER)
                setComplicationOverlay(topComplicationOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[0],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[0])
                val topLeftComplicationOverlay = SettingsOverlay(
                        Rect(screenBounds.left,
                                screenBounds.top,
                                screenBounds.left + size,
                                screenBounds.top + size),
                        screenBounds,
                        "Off",
                        Paint.Align.LEFT)
                setComplicationOverlay(topLeftComplicationOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[1],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[1])
                topLeftComplicationOverlay.setBottomTitle(true)
                val topRightComplicationOverlay = SettingsOverlay(
                        Rect(screenBounds.right - size,
                                screenBounds.top,
                                screenBounds.right,
                                screenBounds.top + size),
                        screenBounds,
                        "Off",
                        Paint.Align.RIGHT)
                setComplicationOverlay(topRightComplicationOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[2],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[2])
                topRightComplicationOverlay.setBottomTitle(true)
                if (WatchFaceService.ROUND) {
                    topComplicationOverlay.setActive(true)
                } else {
                    topLeftComplicationOverlay.setActive(true)
                }
                val leftComplicationOverlay = SettingsOverlay(
                        Rect(insetBounds.left + offset,
                                insetBounds.centerY() - size / 2,
                                insetBounds.left + offset + size,
                                insetBounds.centerY() + size / 2),
                        bounds,
                        "Off",
                        Paint.Align.LEFT)
                setComplicationOverlay(leftComplicationOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[3],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[3])
                val rightComplicationOverlay = SettingsOverlay(
                        Rect(insetBounds.right - offset - size,
                                insetBounds.centerY() - size / 2,
                                insetBounds.right - offset,
                                insetBounds.centerY() + size / 2),
                        bounds,
                        "Off",
                        Paint.Align.RIGHT)
                setComplicationOverlay(rightComplicationOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[4],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[4])
                val bottomComplicationOverlay = SettingsOverlay(
                        Rect(insetBounds.centerX() - size / 2,
                                insetBounds.bottom - offset - size,
                                insetBounds.centerX() + size / 2,
                                insetBounds.bottom - offset),
                        bounds,
                        "Off",
                        Paint.Align.CENTER)
                setComplicationOverlay(bottomComplicationOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[5],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[5])
                val bottomLeftComplicationOverlay = SettingsOverlay(
                        Rect(screenBounds.left,
                                screenBounds.bottom - size,
                                screenBounds.left + size,
                                screenBounds.bottom),
                        screenBounds,
                        "Off",
                        Paint.Align.LEFT)
                setComplicationOverlay(bottomLeftComplicationOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[6],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[6])
                val bottomRightComplicationOverlay = SettingsOverlay(
                        Rect(screenBounds.right - size,
                                screenBounds.bottom - size,
                                screenBounds.right,
                                screenBounds.bottom),
                        screenBounds,
                        "Off",
                        Paint.Align.RIGHT)
                setComplicationOverlay(bottomRightComplicationOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[7],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[7])
                if (WatchFaceService.ROUND) {
                    topLeftComplicationOverlay.setDisabled(true)
                    topRightComplicationOverlay.setDisabled(true)
                    bottomLeftComplicationOverlay.setDisabled(true)
                    bottomRightComplicationOverlay.setDisabled(true)
                }
                mComplicationModules!!.add(topComplicationOverlay)
                mComplicationModules!!.add(topLeftComplicationOverlay)
                mComplicationModules!!.add(topRightComplicationOverlay)
                mComplicationModules!!.add(leftComplicationOverlay)
                mComplicationModules!!.add(rightComplicationOverlay)
                mComplicationModules!!.add(bottomComplicationOverlay)
                mComplicationModules!!.add(bottomLeftComplicationOverlay)
                mComplicationModules!!.add(bottomRightComplicationOverlay)

                val finishModules = ArrayList()
                val finishOverlay = SettingsFinish(getApplicationContext(),
                        Rect(0, 0, width, height))
                finishOverlay.setRunnable(object : Runnable() {
                    @Override
                    fun run() {
                        finish()
                    }
                })

                finishModules.add(finishOverlay)

                val row = SettingsRow()
                row.addPages(SettingsPage(styleModules))
                row.addPages(SettingsPage(colorModules))
                row.addPages(SettingsPage(accentColorModules))
                row.addPages(SettingsPage(mComplicationModules))
                row.addPages(SettingsPage(finishModules))
                pages.add(row)

                return pages
            }
        }
        (findViewById(R.id.pager) as GridViewPager).setAdapter(adapter)

        mProviderInfoRetriever = ProviderInfoRetriever(getApplicationContext(), object : Executor() {
            @Override
            fun execute(@NonNull r: Runnable) {
                Thread(r).start()
            }
        })
        mProviderInfoRetriever!!.init()
        mProviderInfoRetriever!!.retrieveProviderInfo(
                object : ProviderInfoRetriever.OnProviderInfoReceivedCallback() {
                    @Override
                    fun onProviderInfoReceived(i: Int, @Nullable complicationProviderInfo: ComplicationProviderInfo?) {
                        var title = "OFF"
                        if (complicationProviderInfo != null) {
                            title = complicationProviderInfo!!.providerName
                        }
                        mComplicationModules!!.get(i).setTitle(title)
                    }

                },
                ComponentName(getApplicationContext(), WatchFaceService::class.java),
                WatchFaceService.COMPLICATION_IDS
        )
        setSettingsMode(true)
    }

    @Override
    protected fun onDestroy() {
        super.onDestroy()
        mProviderInfoRetriever!!.release()
    }

    @Override
    fun setSettingsMode(mode: Boolean) {
        WatchFaceService.SETTINGS_MODE = if (mode) 3 else 1
    }
}