package com.seapip.thomas.pear.module

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

interface Module {
    fun setBounds(bounds: Rect)
    fun draw(canvas: Canvas)
    fun setColor(color: Int)
    fun setAmbient(ambient: Boolean)
    fun setBurnInProtection(burnInProtection: Boolean)
    fun setLowBitAmbient(lowBitAmbient: Boolean)
}
