package com.hideactive.dialog

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.hideactive.R
import com.module.library.util.ToastUtil
import kotlinx.android.synthetic.main.dialog_edit.*

/**
 * 编辑Dialog
 *
 * @author zhouchunjie
 * @date 2018/2/27
 */
class EditDialog(
        context: Context,
        val titleResId: Int = R.string.edit,
        val defaultText: String? = ""
) : AlertDialog(context, R.style.BaseDialog) {

    private var onCancelListener: View.OnClickListener? = null
    private var onEditListener: OnEditListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit)

        setCancelable(true)
        setCanceledOnTouchOutside(true)

        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        tv_title.setText(titleResId)
        defaultText?.let {
            et_edit.editableText.append(it)
        }

        btn_cancel.setOnClickListener {
            onCancelListener?.onClick(it)
            cancel()
        }
        btn_confirm.setOnClickListener {
            val text = et_edit.text.toString()
            if (!TextUtils.isEmpty(text)) {
                onEditListener?.onConfirm(text)
                cancel()
            } else {
                ToastUtil.showLong(context, R.string.please_input_content)
            }
        }
    }

    fun setOnCancelListener(onCancelListener: View.OnClickListener?) {
        this.onCancelListener = onCancelListener
    }

    fun setOnEditListener(onEditListener: OnEditListener?) {
        this.onEditListener = onEditListener
    }

    interface OnEditListener {
        fun onConfirm(text: String)
    }
}