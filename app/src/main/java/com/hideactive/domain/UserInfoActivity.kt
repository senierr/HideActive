package com.hideactive.domain

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.comm.ErrorHandler
import com.hideactive.dialog.LoadingDialog
import com.hideactive.ext.bindToLifecycle
import com.hideactive.widget.ClearEditText
import com.module.library.util.OnThrottleClickListener
import com.module.library.util.ToastUtil
import com.senierr.repository.Repository
import com.senierr.repository.bean.User
import com.senierr.repository.service.api.IUserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.layout_top_bar_normal.*

/**
 * 登录
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
class UserInfoActivity : BaseActivity() {

    private val userService = Repository.getService<IUserService>()

    private lateinit var userId: String
    private var isCurrent: Boolean = false

    companion object {
        fun openUserInfo(context: Context, userId: String, isCurrent: Boolean) {
            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("isCurrent", isCurrent)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        userId = intent.getStringExtra("userId")
        isCurrent = intent.getBooleanExtra("isCurrent", false)

        initView()
    }

    override fun onStart() {
        super.onStart()
        loadUser()
    }

    /**
     * 初始化界面
     */
    private fun initView() {
        tv_title.setText(R.string.detail)
        btn_left.setOnClickListener(onThrottleClickListener)

        rl_nickname.setOnClickListener(onThrottleClickListener)
    }

    /**
     * 防抖动点击事件
     */
    private val onThrottleClickListener = object : OnThrottleClickListener() {
        override fun onThrottleClick(view: View?) {
            when(view?.id) {
                R.id.btn_left -> {
                    finish()
                }
                R.id.rl_nickname -> {
                    val alertDialog = AlertDialog.Builder(this@UserInfoActivity)
                            .setTitle(R.string.edit_nickname)
                            .setView(R.layout.dialog_edit_text)
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { dialog, _ ->
                                val editText = (dialog as AlertDialog).findViewById<ClearEditText>(R.id.et_content)
                                val nickname = editText?.text.toString()
                                if (TextUtils.isEmpty(nickname)) {
                                    ToastUtil.showLong(this@UserInfoActivity, R.string.nickname_empty)
                                } else {
                                    updateUserNickname(userId, nickname)
                                }
                            })
                            .create()
                    alertDialog.show()
                }
            }
        }
    }

    /**
     * 刷新用户信息
     */
    private fun refreshUserInfo(user: User) {
        // 头像
        Glide.with(this)
                .load(user.portrait)
                .apply(RequestOptions()
                        .error(R.drawable.ic_default_portrait)
                        .override(160, 160))
                .into(iv_portrait)
        // 账号
        tv_account.text = user.account
        // 昵称
        if (user.nickname == null) {
            tv_nickname.setText(R.string.click_to_edit)
        } else {
            tv_nickname.text = user.nickname
        }
    }

    /**
     * 加载用户信息
     */
    private fun loadUser() {
        userService.getRemoteUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
//                    loadingDialog.show()
                }
                .subscribe({
                    refreshUserInfo(it)
                }, {
                    ErrorHandler.showNetworkError(this, it)
                })
                .bindToLifecycle(this)
    }

    /**
     * 更新用户昵称
     */
    private fun updateUserNickname(objectId: String, nickname: String) {
        userService.updateUserNickname(objectId, nickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    return@flatMap userService.getRemoteUser(objectId)
                            .subscribeOn(Schedulers.io())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    refreshUserInfo(it)
                }, {
                    ErrorHandler.showNetworkError(this, it)
                })
                .bindToLifecycle(this)
    }
}