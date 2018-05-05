package com.hideactive.ext

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * Activity扩展函数
 *
 * @author zhouchunjie
 * @date 2018/5/5
 */

/**
 * 动态显示软键盘
 */
fun Activity.showSoftInput() {
    var view = this.currentFocus
    if (view == null) view = View(this)
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
}

/**
 * 动态隐藏软键盘
 *
 * @param activity activity
 */
fun Activity.hideSoftInput() {
    var view = this.currentFocus
    if (view == null) view = View(this)
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}