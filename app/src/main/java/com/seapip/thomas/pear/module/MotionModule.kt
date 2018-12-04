package com.seapip.thomas.pear.module

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Movie
import android.graphics.Paint
import android.graphics.Rect

import com.seapip.thomas.pear.R

import java.io.InputStream
import java.util.Random

class MotionModule(private val mContext: Context, scene: Int) : Module {
    private var mBounds: Rect? = null
    private var mMovie: Movie? = null
    private var mDuration: Int = 0
    private var mPosition: Int = 0
    private var mStep: Int = 0
    private var mAmbient: Boolean = false
    private var mBitmap: Bitmap? = null
    private var mBitmapScaled: Bitmap? = null
    private var mCanvas: Canvas? = null
    private val mRandom: Random
    private var mLastRandom: Int = 0
    private val mMovies: Array<IntArray>
    private var mBlocked: Boolean = false
    private var mScene: Int = 0

    /* Paint */
    private val mFadeInPaint: Paint

    init {

        mRandom = Random()
        mLastRandom = 0

        val jellyfish = IntArray(6)
        jellyfish[0] = R.drawable.jellyfish_1
        jellyfish[1] = R.drawable.jellyfish_2
        jellyfish[2] = R.drawable.jellyfish_3
        jellyfish[3] = R.drawable.jellyfish_4
        jellyfish[4] = R.drawable.jellyfish_5
        jellyfish[5] = R.drawable.jellyfish_6

        val flower = IntArray(7)
        flower[0] = R.drawable.flower_1
        flower[1] = R.drawable.flower_2
        flower[2] = R.drawable.flower_3
        flower[3] = R.drawable.flower_4
        flower[4] = R.drawable.flower_5
        flower[5] = R.drawable.flower_6
        flower[6] = R.drawable.flower_7

        val city = IntArray(5)
        city[0] = R.drawable.city_1
        city[1] = R.drawable.city_2
        city[2] = R.drawable.city_3
        city[3] = R.drawable.city_4
        city[4] = R.drawable.city_5

        mMovies = arrayOfNulls(4)
        mMovies[0] = jellyfish
        mMovies[1] = flower
        mMovies[2] = city

        /* Paint */
        mFadeInPaint = Paint()
        mFadeInPaint.setColor(Color.BLACK)
        mFadeInPaint.setAlpha(255)

        setScene(scene)
    }

    @Override
    fun setBounds(bounds: Rect) {
        mBounds = bounds
    }

    @Override
    fun draw(canvas: Canvas) {
        if (!mAmbient && !mBlocked) {
            if (mPosition <= mDuration) {
                mMovie!!.setTime(mPosition)
                if (mDuration - mPosition < 500) {
                    mStep -= 5
                    if (mStep < 30) {
                        mStep = 30
                    }
                }
                mPosition += mStep
                var alpha = mFadeInPaint.getAlpha() - 8
                if (alpha < 92) {
                    alpha = 92
                }
                mFadeInPaint.setAlpha(alpha)
                mMovie!!.draw(mCanvas, 0, 0)
                mBitmapScaled = Bitmap.createScaledBitmap(mBitmap, mBounds!!.width(),
                        mBounds!!.height(), true)
            } else {
                mMovie = null
            }
            canvas.drawBitmap(mBitmapScaled, mBounds!!.left, mBounds!!.top, null)
        }
        canvas.drawRect(mBounds, mFadeInPaint)
    }

    fun setScene(scene: Int) {
        mScene = scene
        start()
    }

    private fun start() {
        mBlocked = true
        mPosition = 0
        mStep = 60
        mFadeInPaint.setAlpha(255)
        var random: Int
        do {
            random = mRandom.nextInt(mMovies[mScene].size)
        } while (random == mLastRandom)
        mLastRandom = random
        val inputStream = mContext.getResources().openRawResource(+mMovies[mScene][random])
        mMovie = Movie.decodeStream(inputStream)
        mDuration = mMovie!!.duration()
        mBitmap = Bitmap.createBitmap(mMovie!!.width(), mMovie!!.height(), Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)
        mBlocked = false
    }

    fun tap(x: Int, y: Int) {
        if (mBounds!!.contains(x, y)) {
            start()
        }
    }

    @Override
    fun setColor(color: Int) {
    }

    @Override
    fun setAmbient(ambient: Boolean) {
        mAmbient = ambient
        if (!ambient) {
            start()
        } else {
            mFadeInPaint.setAlpha(255)
        }
    }

    @Override
    fun setBurnInProtection(burnInProtection: Boolean) {
    }

    @Override
    fun setLowBitAmbient(lowBitAmbient: Boolean) {
    }
}
