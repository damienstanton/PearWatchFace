package com.seapip.thomas.pear.settings

import android.app.Fragment
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

import android.app.Activity.RESULT_OK

class SettingsFragment : Fragment(), View.OnTouchListener {
    /* Paint */
    internal var mOverlayPaint: Paint
    private var mView: View? = null
    private var mRow: Int = 0
    private var mCol: Int = 0
    private var mPrefs: SharedPreferences? = null

    private val settingModuleOverlays: ArrayList<SettingsOverlay>
        get() = (getActivity() as SettingsActivity).getSettingModuleOverlays(mRow, mCol)

    @Override
    fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup, savedInstanceState: Bundle): View {
        mRow = getArguments().getInt("row")
        mCol = getArguments().getInt("col")
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext())

        mOverlayPaint = Paint()
        mOverlayPaint.setColor(Color.argb(192, 0, 0, 0))
        mView = object : View(getContext()) {
            @Override
            protected fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)
                canvas.drawRect(0, 0, getWidth(), getHeight(), mOverlayPaint)
                for (moduleOverlay in settingModuleOverlays) {
                    moduleOverlay.draw(canvas)
                }
            }
        }
        mView!!.setOnTouchListener(this)
        return mView
    }


    @Override
    fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.getAction() === MotionEvent.ACTION_UP) {
            var active: SettingsOverlay? = null
            for (moduleOverlay in settingModuleOverlays) {
                if (moduleOverlay.contains(event.getX() as Int, event.getY() as Int)) {
                    active = moduleOverlay
                }
            }
            if (active != null) {
                if (active!!.getActive()) {
                    val intent = active!!.getIntent()
                    val runnable = active!!.getRunnable()
                    if (intent != null) {
                        startActivityForResult(intent, active!!.getRequestCode())
                    } else if (runnable != null) {
                        runnable!!.run()
                    }
                }
                for (moduleOverlay in settingModuleOverlays) {
                    moduleOverlay.setActive(false)
                }
                active!!.setActive(true)
            }
            mView!!.invalidate()
        }
        return true
    }


    @Override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            for (moduleOverlay in settingModuleOverlays) {
                if (moduleOverlay.getRequestCode() === requestCode) {
                    val runnable = moduleOverlay.getRunnable()
                    if (runnable != null) {
                        moduleOverlay.setData(data)
                        runnable!!.run()
                    }
                }
            }
        }
    }

    private fun updateSettings() {
        (getActivity() as SettingsActivity).setSettingsMode(true)
    }

    companion object {

        val COLOR_REQUEST = 10
    }
}
