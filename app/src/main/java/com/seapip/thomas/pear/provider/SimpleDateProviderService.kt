package com.seapip.thomas.pear.provider

import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationManager
import android.support.wearable.complications.ComplicationProviderService
import android.support.wearable.complications.ComplicationText
import android.util.Log

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Example Watch Face Complication data provider provides a random number on every update.
 */
class SimpleDateProviderService : ComplicationProviderService() {

    /*
     * Called when a complication has been activated. The method is for any one-time
     * (per complication) set-up.
     *
     * You can continue sending data for the active complicationId until onComplicationDeactivated()
     * is called.
     */
    @Override
    fun onComplicationActivated(
            complicationId: Int, dataType: Int, complicationManager: ComplicationManager) {
        Log.d(TAG, "onComplicationActivated(): $complicationId")
        super.onComplicationActivated(complicationId, dataType, complicationManager)
    }

    /*
     * Called when the complication needs updated data from your provider. There are four scenarios
     * when this will happen:
     *
     *   1. An active watch face complication is changed to use this provider
     *   2. A complication using this provider becomes active
     *   3. The period of time you specified in the manifest has elapsed (UPDATE_PERIOD_SECONDS)
     *   4. You triggered an update from your own class via the
     *       ProviderUpdateRequester.requestUpdate() method.
     */
    @Override
    fun onComplicationUpdate(
            complicationId: Int, dataType: Int, complicationManager: ComplicationManager) {
        Log.d(TAG, "onComplicationUpdate() id: $complicationId")

        val mCalendar = Calendar.getInstance()
        mCalendar.setTimeZone(TimeZone.getDefault())

        val dayOfMonth = String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH))

        var complicationData: ComplicationData? = null

        when (dataType) {
            ComplicationData.TYPE_SHORT_TEXT -> complicationData = ComplicationData.Builder(ComplicationData.TYPE_SHORT_TEXT)
                    .setShortText(ComplicationText.plainText(dayOfMonth))
                    .build()
            else -> if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Unexpected complication type $dataType")
            }
        }

        if (complicationData != null) {
            complicationManager.updateComplicationData(complicationId, complicationData)
        }
    }

    /*
     * Called when the complication has been deactivated. If you are updating the complication
     * manager outside of this class with updates, you will want to update your class to stop.
     */
    @Override
    fun onComplicationDeactivated(complicationId: Int) {
        Log.d(TAG, "onComplicationDeactivated(): $complicationId")
        super.onComplicationDeactivated(complicationId)
    }

    companion object {

        private val TAG = "SimpleDateProvider"
    }
}