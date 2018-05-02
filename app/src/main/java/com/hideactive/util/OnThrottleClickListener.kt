package com.hideactive.util

import android.view.View

/**
 * 过滤重复点击事件
 *
 * @author zhouchunjie
 * @date 2018/4/16
 */
abstract class OnThrottleClickListener : View.OnClickListener {

    private var lastClickTime = 0L
    private var viewId = 0

    final override fun onClick(v: View?) {
        if (v == null) return
        val currentTime = System.currentTimeMillis()
        val isSameView = v.id == viewId
        viewId = v.id
        if (currentTime - lastClickTime < 500 && isSameView) return
        lastClickTime = currentTime
        onThrottleClick(v)
    }

    abstract fun onThrottleClick(view: View?)
}