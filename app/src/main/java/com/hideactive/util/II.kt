package com.hideactive.util

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.support.v4.graphics.drawable.DrawableCompat

/**
 * @author zhouchunjie
 * @date 2018/9/11
 */
class II {

    private fun getStateDrawable(drawable: Drawable, colors: IntArray, states: Array<IntArray>): Drawable {
        var drawable = drawable
        val colorList = ColorStateList(states, colors)
        val state = drawable.constantState
        drawable = DrawableCompat.wrap(if (state == null) drawable else state.newDrawable()).mutate()
        DrawableCompat.setTintList(drawable, colorList)
        return drawable
    }

    private fun getStateListDrawable(drawable: Drawable, states: Array<IntArray>): StateListDrawable {
        val stateListDrawable = StateListDrawable()
        for (state in states) {
            stateListDrawable.addState(state, drawable)
        }
        return stateListDrawable
    }

    companion object {

        fun tintDrawable(drawable: Drawable, colors: Int): Drawable {
            val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, colors)
            return wrappedDrawable
        }
    }
}
