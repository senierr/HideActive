package com.hideactive.ext

import android.view.View

/**
 * 视图扩展函数
 *
 * @author zhouchunjie
 * @date 2018/5/11
 */

fun View.isVisible(isVisible: Boolean) {
    if (isVisible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.INVISIBLE
    }
}

fun View.isGone(isGone: Boolean) {
    if (isGone) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}