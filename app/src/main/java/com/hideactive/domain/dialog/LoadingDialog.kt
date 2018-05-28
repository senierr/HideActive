package com.hideactive.domain.dialog

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.hideactive.R

/**
 * 加载框提示
 *
 * @author zhouchunjie
 * @date 2018/2/27
 */
class LoadingDialog(context: Context) : AlertDialog(context, R.style.BaseDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.dialog_loading)

        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }
}