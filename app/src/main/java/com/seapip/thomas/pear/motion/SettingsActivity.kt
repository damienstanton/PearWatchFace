package com.seapip.thomas.pear.motion

import android.content.ComponentName
import android.content.SharedPreferences
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
                val inset = (if (WatchFaceService.ROUND) (width - Math.sqrt(width * width / 2) as Int) / 2 else WatchFaceService.MODULE_SPACING) + 20
                val screenInset = 20
                val screenBounds = Rect(screenInset, screenInset, width - screenInset, height - screenInset)
                val bounds = Rect(inset, inset, width - inset, height - inset)

                val backgroundModules = ArrayList()
                val scene = preferences.getInt("settings_motion_scene", 0)
                val sceneTitle: String
                when (scene) {
                    0 -> sceneTitle = "Jellyfish"
                    1 -> sceneTitle = "Flowers"
                    2 -> sceneTitle = "Cities"
                    else -> sceneTitle = "Jellyfish"
                }
                val backgroundOverlay = SettingsOverlay(screenBounds,
                        screenBounds,
                        sceneTitle,
                        Paint.Align.CENTER)
                val sceneRunnable = object : Runnable() {
                    @Override
                    fun run() {
                        var scene = preferences.getInt("settings_motion_scene", 0)
                        scene++
                        scene = if (scene > 2) 0 else scene
                        preferences.edit().putInt("settings_motion_scene", scene).apply()
                        when (scene) {
                            0 -> backgroundOverlay.setTitle("Jellyfish")
                            1 -> backgroundOverlay.setTitle("Flowers")
                            2 -> backgroundOverlay.setTitle("Cities")
                        }
                        setSettingsMode(true)
                    }
                }
                backgroundOverlay.setRunnable(sceneRunnable)
                backgroundOverlay.setRound(WatchFaceService.ROUND)
                backgroundOverlay.setInsetTitle(true)
                backgroundOverlay.setActive(true)
                backgroundModules.add(backgroundOverlay)

                val dateModules = ArrayList()
                val date = preferences.getInt("settings_motion_date", 0)
                val dateTitle: String
                when (date) {
                    0 -> dateTitle = "Off"
                    1 -> dateTitle = "Day of week"
                    2 -> dateTitle = "Day of month"
                    3 -> dateTitle = "Day"
                    else -> dateTitle = "Off"
                }
                val dateOverlay = SettingsOverlay(Rect(
                        bounds.left + WatchFaceService.MODULE_SPACING * 2,
                        bounds.top + bounds.height() / 3 - WatchFaceService.MODULE_SPACING / 2 * 3,
                        bounds.right,
                        bounds.bottom - (bounds.height() - WatchFaceService.MODULE_SPACING * 2) / 3 - 3 * WatchFaceService.MODULE_SPACING),
                        bounds,
                        dateTitle,
                        Paint.Align.RIGHT)
                dateOverlay.setRunnable(object : Runnable() {
                    @Override
                    fun run() {
                        var date = preferences.getInt("settings_motion_date", 0)
                        date++
                        date = if (date > 3) 0 else date
                        preferences.edit().putInt("settings_motion_date", date).apply()
                        when (date) {
                            0 -> dateOverlay.setTitle("Off")
                            1 -> dateOverlay.setTitle("Day of week")
                            2 -> dateOverlay.setTitle("Day of month")
                            3 -> dateOverlay.setTitle("Day")
                            else -> dateOverlay.setTitle("Off")
                        }
                        setSettingsMode(true)
                    }
                })
                dateOverlay.setActive(true)
                dateModules.add(dateOverlay)

                val spacing = WatchFaceService.MODULE_SPACING - 2
                mComplicationModules = ArrayList()
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
                        WatchFaceService.COMPLICATION_IDS[0],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[0])
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
                        WatchFaceService.COMPLICATION_IDS[1],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[1])
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
                        WatchFaceService.COMPLICATION_IDS[2],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[2])
                mComplicationModules!!.add(complicationBottomLeftOverlay)
                mComplicationModules!!.add(complicationBottomCenterOverlay)
                mComplicationModules!!.add(complicationBottomRightOverlay)
                complicationBottomLeftOverlay.setActive(true)

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
                row.addPages(SettingsPage(backgroundModules))
                row.addPages(SettingsPage(dateModules))
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