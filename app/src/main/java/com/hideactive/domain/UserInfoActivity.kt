package com.hideactive.domain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.comm.ErrorHandler
import com.hideactive.dialog.EditDialog
import com.hideactive.dialog.LoadingDialog
import com.hideactive.ext.bindToLifecycle
import com.module.library.util.OnThrottleClickListener
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobError
import com.senierr.repository.bean.User
import com.senierr.repository.service.api.IChannelService
import com.senierr.repository.service.api.IPushService
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

    private lateinit var userId: String     // 用户ID
    private var isCurrent: Boolean = false  // 是否登录用户

    private lateinit var loadingDialog: LoadingDialog
    private val userService = Repository.getService<IUserService>()
    private val channelService = Repository.getService<IChannelService>()
    private val pushService = Repository.getService<IPushService>()

    private var user: User? = null  // 用户信息缓存

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

    override fun onDestroy() {
        loadingDialog.dismiss()
        super.onDestroy()
    }

    /**
     * 初始化界面
     */
    private fun initView() {
        loadingDialog = LoadingDialog(this)

        tv_title.setText(R.string.detail)
        btn_left.setOnClickListener(onThrottleClickListener)

        rl_nickname.setOnClickListener(onThrottleClickListener)
        rl_remark.setOnClickListener(onThrottleClickListener)

        btn_logout.setOnClickListener(onThrottleClickListener)
        btn_video_talk.setOnClickListener(onThrottleClickListener)
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
                    user?.let {
                        val editDialog = EditDialog(this@UserInfoActivity, defaultText = it.nickname)
                        editDialog.setOnEditListener(object : EditDialog.OnEditListener {
                            override fun onConfirm(text: String) {
                                updateNickname(userId, text)
                            }
                        })
                        editDialog.show()
                    }
                }
                R.id.rl_remark -> {
                    user?.let {
                        val editDialog = EditDialog(this@UserInfoActivity,
                                defaultText = userService.getRemark(it.objectId))
                        editDialog.setOnEditListener(object : EditDialog.OnEditListener {
                            override fun onConfirm(text: String) {
                                updateRemark(userId, text)
                            }
                        })
                        editDialog.show()
                    }
                }
                R.id.btn_logout -> {
                    logout()
                }
                R.id.btn_video_talk -> {
                    createChannel()
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
        // 备注
        if (isCurrent) {
            rl_remark.visibility = View.GONE
        } else {
            rl_remark.visibility = View.VISIBLE
            tv_remark.text = userService.getRemark(userId)
        }
        // 底部功能按钮
        if (isCurrent) {
            btn_logout.visibility = View.VISIBLE
            btn_video_talk.visibility = View.GONE
        } else {
            btn_logout.visibility = View.GONE
            btn_video_talk.visibility = View.VISIBLE
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
                    loadingDialog.show()
                }
                .doFinally {
                    loadingDialog.hide()
                }
                .subscribe({
                    // 缓存内存
                    user = it
                    refreshUserInfo(it)
                }, {
                    ErrorHandler.showNetworkError(this, it)
                })
                .bindToLifecycle(this)
    }

    /**
     * 更新用户昵称
     */
    private fun updateNickname(userId: String, nickname: String) {
        userService.updateUserNickname(userId, nickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loadingDialog.show()
                }
                .doFinally {
                    loadingDialog.hide()
                }
                .subscribe({ _ ->
                    user?.let {
                        it.nickname = nickname
                        refreshUserInfo(it)
                    }
                }, {
                    ErrorHandler.showNetworkError(this, it)
                })
                .bindToLifecycle(this)
    }

    /**
     * 更新备注
     */
    private fun updateRemark(userId: String, remark: String) {
        userService.setRemark(userId, remark)
        user?.let {
            refreshUserInfo(it)
        }
    }

    /**
     * 注销登录
     */
    private fun logout() {
        userService.logout(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loadingDialog.show()
                }
                .doFinally {
                    loadingDialog.hide()
                }
                .subscribe({
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }, {
                    ErrorHandler.showNetworkError(this, it)
                })
                .bindToLifecycle(this)
    }

    /**
     * 创建视频通话
     */
    private fun createChannel() {
        user?.let { invitee ->
            userService.getCurrentUser()
                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        return@flatMap channelService.create(it, invitee, "1")
                    }
                    .flatMap {
                        val pushToken = it.invitee.pushToken
                        if (!it.invitee.isOnline) {
                            throw BmobError(error = getString(R.string.invitee_not_online))
                        } else if (pushToken == null) {
                            throw BmobError(error = getString(R.string.invitee_not_register_push))
                        } else {
                            return@flatMap pushService.pushMessage(pushToken, Gson().toJson(it))
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                    }, {
                        ErrorHandler.showNetworkError(this, it)
                    })
                    .bindToLifecycle(this)
        }
    }
}