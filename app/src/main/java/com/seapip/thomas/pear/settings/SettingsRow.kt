package com.seapip.thomas.pear.settings

import java.util.ArrayList

class SettingsRow {

    internal var mPagesRow = ArrayList<SettingsPage>()

    fun addPages(page: SettingsPage) {
        mPagesRow.add(page)
    }

    fun getPages(index: Int): SettingsPage {
        return mPagesRow.get(index)
    }

    fun size(): Int {
        return mPagesRow.size()
    }
}