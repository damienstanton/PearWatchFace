package com.seapip.thomas.pear.settings

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.wearable.view.FragmentGridPagerAdapter

import java.util.ArrayList

class SettingsAdapter(fragmentManager: FragmentManager) : FragmentGridPagerAdapter(fragmentManager) {
    private val pages: ArrayList<SettingsRow>

    val rowCount: Int
        @Override
        get() = pages.size()

    init {
        pages = initPages()
    }

    fun initPages(): ArrayList<SettingsRow> {
        return ArrayList()
    }

    @Override
    fun getFragment(row: Int, col: Int): Fragment {
        val settingsFragment = SettingsFragment()
        val bundle = Bundle()
        bundle.putInt("row", row)
        bundle.putInt("col", col)
        settingsFragment.setArguments(bundle)
        return settingsFragment
    }

    fun getSettingModuleOverlays(row: Int, col: Int): ArrayList<SettingsOverlay> {
        val page = pages.get(row).getPages(col)
        return page.getSettingOverlays()
    }

    @Override
    fun getColumnCount(row: Int): Int {
        return pages.get(row).size()
    }
}