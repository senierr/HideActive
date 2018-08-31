package com.hideactive.domain.user

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hideactive.R
import com.hideactive.domain.base.BaseActivity
import com.module.library.util.OnThrottleClickListener
import com.senierr.repository.Repository
import com.senierr.repository.bean.User
import com.senierr.repository.service.api.IUserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_info.*

/**
 * 登录
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
class UserInfoActivity : BaseActivity() {

    private val userService = Repository.getService<IUserService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        initView()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    /**
     * 初始化界面
     */
    private fun initView() {
        tb_top.setTitle(R.string.user_info)
        tb_top.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance)
        setSupportActionBar(tb_top)
        tb_top.setNavigationIcon(R.drawable.ic_back_white)
        tb_top.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * 防抖动点击事件
     */
    private val onThrottleClickListener = object : OnThrottleClickListener() {
        override fun onThrottleClick(view: View?) {
            when(view?.id) {
                // 密码可见
                R.id.btn_eye -> {
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
                .apply(RequestOptions().override(160, 160))
                .into(iv_portrait)
        // 账号
        tv_account.text = user.account
        // 昵称
        tv_nickname.text = user.nickname
    }

    /**
     * 加载数据
     */
    private fun loadData() {
        userService.getLocalUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    refreshUserInfo(it)
                }
                .flatMap {
                    return@flatMap userService.getRemoteUser(it.objectId)
                            .subscribeOn(Schedulers.io())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    refreshUserInfo(it)
                }, {
                    showNetworkError(it)
                })
    }
}