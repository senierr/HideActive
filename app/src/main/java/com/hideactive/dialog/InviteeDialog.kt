package com.hideactive.dialog

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import com.hideactive.R
import com.senierr.repository.bean.Channel
import kotlinx.android.synthetic.main.dialog_invitee.*

/**
 * 视频邀请Dialog
 *
 * @author zhouchunjie
 * @date 2018/2/27
 */
class InviteeDialog(context: Context, val channel: Channel) : AlertDialog(context, R.style.BaseDialog) {

    private var onActionListener: OnActionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_invitee)

        setCancelable(false)
        setCanceledOnTouchOutside(false)

        val name = if (!TextUtils.isEmpty(channel.owner.nickname)) {
            channel.owner.nickname
        } else {
            channel.owner.account
        }
        tv_message.text = String.format(context.getString(R.string.hint_invitee), name)

        btn_reject.setOnClickListener {
            onActionListener?.onReject(channel)
            cancel()
        }
        btn_accept.setOnClickListener {
            onActionListener?.onAccept(channel)
            cancel()
        }
    }

    fun setOnAcceptListener(onActionListener: OnActionListener?) {
        this.onActionListener = onActionListener
    }

    interface OnActionListener {
        fun onReject(channel: Channel)
        fun onAccept(channel: Channel)
    }
}