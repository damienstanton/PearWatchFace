package com.seapip.thomas.pear.modular

import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.wearable.complications.ComplicationHelperActivity
import android.support.wearable.complications.ComplicationProviderInfo
import android.support.wearable.complications.ProviderInfoRetriever
import android.support.wearable.view.GridViewPager
import android.util.DisplayMetrics

import com.seapip.thomas.pear.ColorActivity
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

        adapter = object : SettingsAdapter(getFragmentManager()) {
            @Override
            fun initPages(): ArrayList<SettingsRow> {
                val pages = ArrayList()

                val metrics = getApplicationContext().getResources().getDisplayMetrics()
                val width = metrics.widthPixels
                val height = metrics.heightPixels
                val spacing = WatchFaceService.MODULE_SPACING - 2
                val inset = (if (WatchFaceService.ROUND)
                    (width - Math.sqrt(width * width / 2) as Int) / 2
                else
                    WatchFaceService.MODULE_SPACING) + 20
                val bounds = Rect(inset, inset, width - inset, height - inset)

                val colorModules = ArrayList()
                val colorOverlay = SettingsOverlay(bounds, bounds, "",
                        Paint.Align.LEFT)
                setColorOverlay(colorOverlay,
                        "settings_modular_color_name",
                        "settings_modular_color_value",
                        "Cyan",
                        Color.parseColor("#00BCD4"))
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
                val complicationCenterOverlay = SettingsOverlay(
                        Rect(bounds.left,
                                bounds.top + (bounds.height() - spacing * 2) / 3 + spacing,
                                bounds.right,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3 - spacing),
                        bounds,
                        "OFF",
                        Paint.Align.CENTER)
                setComplicationOverlay(complicationCenterOverlay,
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
                val complicationBottomCenterOverlay = SettingsOverlay(
                        Rect(bounds.left + (bounds.width() - spacing * 2) / 3 + spacing,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3,
                                bounds.right - (bounds.width() - spacing * 2) / 3 - spacing,
                                bounds.bottom),
                        bounds,
                        "OFF",
                        Paint.Align.CENTER)
                setComplicationOverlay(complicationBottomCenterOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[3],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[3])
                val complicationBottomRightOverlay = SettingsOverlay(
                        Rect(bounds.right - (bounds.width() - spacing * 2) / 3,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3,
                                bounds.right,
                                bounds.bottom),
                        bounds,
                        "OFF",
                        Paint.Align.RIGHT)
                setComplicationOverlay(complicationBottomRightOverlay,
                        WatchFaceService::class.java,
                        WatchFaceService.COMPLICATION_IDS[4],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[4])
                mComplicationModules!!.add(complicationTopLefOverlay)
                mComplicationModules!!.add(complicationCenterOverlay)
                mComplicationModules!!.add(complicationBottomLeftOverlay)
                mComplicationModules!!.add(complicationBottomCenterOverlay)
                mComplicationModules!!.add(complicationBottomRightOverlay)
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