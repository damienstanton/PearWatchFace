package com.seapip.thomas.pear.sport_digital

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

import com.seapip.thomas.pear.sport_digital.WatchFaceService.MODULE_SPACING

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
                val spacing = MODULE_SPACING - 2
                val inset = (if (WatchFaceService.ROUND)
                    (width - Math.sqrt(width * width / 2) as Int) / 2
                else
                    MODULE_SPACING) + 20
                val bounds = Rect(inset, inset, width - inset, height - inset)

                val styleModules = ArrayList()
                val styleOverlay = SettingsOverlay(
                        Rect(
                                bounds.right - (bounds.width() - MODULE_SPACING * 2) / 3 * 2 - MODULE_SPACING,
                                bounds.top,
                                bounds.right,
                                bounds.bottom),
                        bounds, "Style",
                        Paint.Align.RIGHT)
                styleOverlay.setActive(true)
                styleOverlay.setRunnable(object : Runnable() {
                    @Override
                    fun run() {
                        var style = preferences.getInt("settings_sport_digital_style", 0)
                        style++
                        style = if (style > 2) 0 else style
                        preferences.edit().putInt("settings_sport_digital_style", style).apply()
                        setSettingsMode(true)
                    }
                })
                styleModules.add(styleOverlay)

                val colorStyle = preferences.getInt("settings_sport_digital_color_style", 0)
                val colorName = preferences.getString("settings_sport_digital_color_name", "Cyan")
                val colorStyleModules = ArrayList()
                val colorStyleOverlay = SettingsOverlay(bounds,
                        bounds,
                        if (colorStyle == 0) colorName else "White/$colorName",
                        Paint.Align.LEFT)
                colorStyleOverlay.setRunnable(object : Runnable() {
                    @Override
                    fun run() {
                        var colorStyle = preferences.getInt("settings_sport_digital_color_style", 0)
                        val colorName = preferences.getString("settings_sport_digital_color_name", "Cyan")
                        colorStyle = if (colorStyle == 0) 1 else 0
                        preferences.edit().putInt("settings_sport_digital_color_style", colorStyle).apply()
                        colorStyleOverlay.setTitle(if (colorStyle == 0) colorName else "White/$colorName")
                        setSettingsMode(true)
                    }
                })
                colorStyleOverlay.setActive(true)
                colorStyleModules.add(colorStyleOverlay)

                val colorModules = ArrayList()
                val colorOverlay = SettingsOverlay(bounds, bounds, "",
                        Paint.Align.LEFT)
                colorOverlay.setRunnable(object : Runnable() {
                    @Override
                    fun run() {
                        val colorStyle = preferences.getInt("settings_sport_digital_color_style", 0)
                        val colorName = preferences.getString("settings_sport_digital_color_name", "Lime")
                        colorStyleOverlay.setTitle(if (colorStyle == 0) colorName else "White/$colorName")
                    }
                })
                setColorOverlay(colorOverlay,
                        "settings_sport_digital_color_name",
                        "settings_sport_digital_color_value",
                        "Lime",
                        Color.parseColor("#CDDC39"))
                colorOverlay.setActive(true)
                colorModules.add(colorOverlay)

                mComplicationModules = ArrayList()
                val complicationTopLefOverlay = SettingsOverlay(
                        Rect(bounds.left,
                                bounds.top,
                                bounds.left + (bounds.width() - spacing * 2) / 3,
                                bounds.top + (bounds.height() - spacing * 2) / 3),
                        bounds,
                        "OFF", Paint.Align.LEFT)
                setComplicationOverlay(complicationTopLefOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[0],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[0])
                val complicationCenterLeftOverlay = SettingsOverlay(
                        Rect(bounds.left,
                                bounds.top + (bounds.height() - spacing * 2) / 3 + spacing,
                                bounds.left + (bounds.width() - spacing * 2) / 3,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3 - spacing),
                        bounds,
                        "OFF",
                        Paint.Align.LEFT)
                setComplicationOverlay(complicationCenterLeftOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[1],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[1])
                val complicationBottomLeftOverlay = SettingsOverlay(
                        Rect(bounds.left,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3,
                                bounds.left + (bounds.width() - spacing * 2) / 3,
                                bounds.bottom),
                        bounds,
                        "OFF",
                        Paint.Align.LEFT)
                setComplicationOverlay(complicationBottomLeftOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[2],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[2])
                mComplicationModules!!.add(complicationTopLefOverlay)
                mComplicationModules!!.add(complicationCenterLeftOverlay)
                mComplicationModules!!.add(complicationBottomLeftOverlay)
                complicationTopLefOverlay.setActive(true)

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
                row.addPages(SettingsPage(colorStyleModules))
                row.addPages(SettingsPage(colorModules))
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