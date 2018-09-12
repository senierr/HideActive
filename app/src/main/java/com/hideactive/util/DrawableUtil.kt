package com.hideactive.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat

/**
 * Drawable工具类
 *
 * @author zhouchunjie
 * @date 2018/9/11
 */
object DrawableUtil {

    fun getStateDrawable(drawable: Drawable, colors: IntArray, states: Array<IntArray>): Drawable {
        val colorList = ColorStateList(states, colors)
        val state = drawable.constantState
        val wrappedDrawable = DrawableCompat.wrap(if (state == null) drawable else state.newDrawable()).mutate()
        DrawableCompat.setTintList(wrappedDrawable, colorList)
        return wrappedDrawable
    }

    fun getStateListDrawable(drawable: Drawable, states: Array<IntArray>): StateListDrawable {
        val stateListDrawable = StateListDrawable()
        for (state in states) {
            stateListDrawable.addState(state, drawable)
        }
        return stateListDrawable
    }

    fun tintDrawable(drawable: Drawable, colors: Int): Drawable {
        val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
        DrawableCompat.setTint(wrappedDrawable, colors)
        return wrappedDrawable
    }

    fun tintDrawable(context: Context, drawableId: Int, colors: Int): Drawable? {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        if (drawable != null) {
            val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, colors)
            return wrappedDrawable
        }
        return null
    }
}