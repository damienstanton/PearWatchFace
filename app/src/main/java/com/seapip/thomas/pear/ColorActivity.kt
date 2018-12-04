package com.seapip.thomas.pear

import android.app.Activity
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen

import org.jraf.android.androidwearcolorpicker.app.ColorPickActivity

class ColorActivity : PreferenceActivity() {

    @Override
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        val colorPreferenceFragment = ColorPreferenceFragment()
        val bundle = getIntent().getExtras()
        colorPreferenceFragment.setArguments(bundle)
        getFragmentManager().beginTransaction().replace(android.R.id.content, colorPreferenceFragment).commit()
    }

    class ColorPreferenceFragment : PreferenceFragment() {

        internal var oldColor: Int = 0
        internal var colorNamesId: Int = 0
        internal var colorValuesId: Int = 0

        @Override
        fun onCreate(savedInstanceState: Bundle) {
            super.onCreate(savedInstanceState)

            val bundle = getArguments()

            oldColor = bundle.getInt("color")
            colorNamesId = bundle.getInt("color_names_id")
            colorValuesId = bundle.getInt("color_values_id")

            val preferenceScreen = getPreferenceManager().createPreferenceScreen(getContext())
            setPreferenceScreen(preferenceScreen)
        }

        @Override
        fun onStart() {
            super.onStart()

            getPreferenceScreen().removeAll()

            val customPreference = Preference(getContext())
            customPreference.setTitle("Custom")
            customPreference.setOnPreferenceClickListener(object : Preference.OnPreferenceClickListener() {
                @Override
                fun onPreferenceClick(preference: Preference): Boolean {
                    startActivityForResult(ColorPickActivity.IntentBuilder().oldColor(oldColor).build(getContext()), 0)
                    return false
                }
            })
            setStyleIcon(customPreference, getContext().getDrawable(R.drawable.ic_colorize_black_24dp), Color.WHITE)
            getPreferenceScreen().addPreference(customPreference)

            val colorNames = getResources().getStringArray(colorNamesId)
            val colorValues = getResources().obtainTypedArray(colorValuesId)
            for (x in colorNames.indices) {
                val preference = Preference(getContext())
                val name = colorNames[x]
                val color = colorValues.getColor(x, 0)
                preference.setOnPreferenceClickListener(object : Preference.OnPreferenceClickListener() {
                    @Override
                    fun onPreferenceClick(preference: Preference): Boolean {
                        val intent = Intent()
                        intent.putExtra("color_name", name)
                        intent.putExtra("color_value", color)
                        getActivity().setResult(Activity.RESULT_OK, intent)
                        getActivity().finish()
                        return false
                    }
                })
                preference.setTitle(name)
                setStyleIcon(preference, getContext().getDrawable(R.drawable.ic_circle_black_24dp).mutate(), color)

                getPreferenceScreen().addPreference(preference)

            }
            colorValues.recycle()
        }

        private fun setStyleIcon(preference: Preference, icon: Drawable, color: Int) {
            val layerDrawable = getContext().getDrawable(R.drawable.config_icon) as LayerDrawable
            icon.setTint(color)
            if (layerDrawable.setDrawableByLayerId(R.id.nested_icon, icon)) {
                preference.setIcon(layerDrawable)
            }
        }

        @Override
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == RESULT_OK) {
                val intent = Intent()
                intent.putExtra("color_name", "Custom")
                intent.putExtra("color_value", ColorPickActivity.getPickedColor(data))
                getActivity().setResult(Activity.RESULT_OK, intent)
                getActivity().finish()
            }
        }
    }
}
