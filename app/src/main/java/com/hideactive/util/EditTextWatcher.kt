package com.hideactive.util

import android.text.Editable
import android.text.TextWatcher

/**
 * 编辑框监听
 *
 * @author zhouchunjie
 * @date 2018/3/12
 */
abstract class EditTextWatcher : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(p0: Editable?) {
    }
}