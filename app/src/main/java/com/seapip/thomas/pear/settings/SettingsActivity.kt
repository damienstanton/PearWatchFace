package com.seapip.thomas.pear.settings

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.wearable.complications.ComplicationHelperActivity
import android.support.wearable.complications.ComplicationProviderInfo
import android.support.wearable.complications.ProviderChooserIntent
import android.support.wearable.view.DotsPageIndicator
import android.support.wearable.view.GridViewPager

import com.seapip.thomas.pear.ColorActivity
import com.seapip.thomas.pear.R
import com.seapip.thomas.pear.modular.WatchFaceService

import java.util.ArrayList

class SettingsActivity : Activity() {
    internal var preferences: SharedPreferences
    internal var editor: SharedPreferences.Editor

    val adapter: SettingsAdapter?
        get() = null

    @Override
    protected fun onCreate(@Nullable savedInstanceState: Bundle) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext().getApplicationContext())
        editor = preferences.edit()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grid)
        (findViewById(R.id.indicator) as DotsPageIndicator)
                .setPager(findViewById(R.id.pager) as GridViewPager)
    }

    fun setSettingsMode(mode: Boolean) {}

    @Override
    protected fun onResume() {
        super.onResume()
        setSettingsMode(true)
    }

    @Override
    protected fun onStop() {
        super.onStop()
        setSettingsMode(false)
    }

    fun getSettingModuleOverlays(row: Int, col: Int): ArrayList<SettingsOverlay> {
        return adapter!!.getSettingModuleOverlays(row, col)
    }

    fun setColorOverlay(settingsOverlay: SettingsOverlay,
                        nameKey: String, valueKey: String,
                        defaultName: String, defaultValue: Int) {
        val intent = Intent(getApplicationContext(), ColorActivity::class.java)
        intent.putExtra("color", preferences.getInt(valueKey, defaultValue))
        intent.putExtra("color_names_id", R.array.color_names)
        intent.putExtra("color_values_id", R.array.color_values)
        settingsOverlay.setIntent(intent)
        settingsOverlay.setTitle(preferences.getString(nameKey, defaultName))
        val runnable = settingsOverlay.getRunnable()
        settingsOverlay.setRunnable(object : Runnable() {
            @Override
            fun run() {
                val data = settingsOverlay.getData()
                val name = data.getStringExtra("color_name")
                editor.putString(nameKey, name)
                val value = data.getIntExtra("color_value", 0)
                editor.putInt(valueKey, value)
                editor.apply()
                intent.putExtra("color", value)
                settingsOverlay.setTitle(name)
                if (runnable != null) {
                    runnable!!.run()
                }
                setSettingsMode(true)
            }
        })

    }

    fun setComplicationOverlay(settingsOverlay: SettingsOverlay,
                               watchFaceService: Class,
                               id: Int,
                               supportedTypes: IntArray) {
        settingsOverlay.setIntent(ComplicationHelperActivity.createProviderChooserHelperIntent(
                getApplicationContext(),
                ComponentName(getApplicationContext().getApplicationContext(),
                        watchFaceService),
                id,
                supportedTypes))
        val runnable = settingsOverlay.getRunnable()
        settingsOverlay.setRunnable(object : Runnable() {
            @Override
            fun run() {
                var title = "OFF"
                val data = settingsOverlay.getData()
                if (data != null) {
                    val providerInfo = data!!.getParcelableExtra(ProviderChooserIntent.EXTRA_PROVIDER_INFO)
                    if (providerInfo != null) {
                        title = providerInfo!!.providerName
                    }
                }
                settingsOverlay.setTitle(title)
                if (runnable != null) {
                    runnable!!.run()
                }
            }
        })
    }
}