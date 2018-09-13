package com.hideactive.domain

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.comm.ErrorHandler
import com.hideactive.dialog.InviteeDialog
import com.hideactive.ext.bindToLifecycle
import com.module.library.util.OnThrottleClickListener
import com.senierr.adapter.internal.MultiTypeAdapter
import com.senierr.adapter.internal.RVHolder
import com.senierr.adapter.internal.ViewHolderWrapper
import com.senierr.repository.Repository
import com.senierr.repository.bean.Channel
import com.senierr.repository.bean.User
import com.senierr.repository.service.api.IUserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 首页
 *
 * @author zhouchunjie
 * @date 2018/8/31
 */
class MainActivity : BaseActivity() {

    private val userService = Repository.getService<IUserService>()
    private val multiTypeAdapter = MultiTypeAdapter()

    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        EventBus.getDefault().register(this)
    }

    override fun onStart() {
        super.onStart()
        // 自动刷新
        srl_refresh.postDelayed({
            srl_refresh.isRefreshing = true
            loadData()
        }, 300)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun initView() {
        btn_portrait.visibility = View.GONE
        btn_portrait.setOnClickListener(onThrottleClickListener)
        btn_setting.setOnClickListener(onThrottleClickListener)

        val userWrapper = object : ViewHolderWrapper<User>() {
            override fun onCreateViewHolder(p0: ViewGroup): RVHolder {
                return RVHolder.create(p0, R.layout.item_user)
            }

            override fun onBindViewHolder(p0: RVHolder, p1: User) {
                val tvPortrait = p0.getView<TextView>(R.id.tv_portrait)
                val tvName = p0.getView<TextView>(R.id.tv_name)
                val tvRemark = p0.getView<TextView>(R.id.tv_remark)

                val name = if (!TextUtils.isEmpty(p1.nickname)) {
                    p1.nickname
                } else {
                    p1.account
                }
                val remark = userService.getRemark(p1.objectId)

                tvPortrait.text = name?.get(0).toString()
                tvName.text = name
                if (!TextUtils.isEmpty(remark)) {
                    tvRemark.text = "($remark)"
                }
            }
        }
        userWrapper.onItemClickListener = object : ViewHolderWrapper.OnItemClickListener() {
            override fun onClick(viewHolder: RVHolder?, position: Int) {
                val user = multiTypeAdapter.dataList[position] as User
                UserInfoActivity.openUserInfo(this@MainActivity, user.objectId, false)
            }
        }
        multiTypeAdapter.bind(User::class.java, userWrapper)

        rv_users.layoutManager = LinearLayoutManager(this)
        rv_users.adapter = multiTypeAdapter

        srl_refresh.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary)
        srl_refresh.setOnRefreshListener {
            loadData()
        }
    }

    /**
     * 防抖动点击事件
     */
    private val onThrottleClickListener = object : OnThrottleClickListener() {
        override fun onThrottleClick(view: View?) {
            when(view?.id) {
                R.id.btn_portrait -> {
                    currentUser?.let {
                        UserInfoActivity.openUserInfo(this@MainActivity, it.objectId, true)
                    }
                }
                R.id.btn_setting -> {

                }
            }
        }
    }

    /**
     * 加载数据
     */
    private fun loadData() {
        userService.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    currentUser = it
                    btn_portrait.visibility = View.VISIBLE
                    return@flatMap userService.getFriends(it.objectId)
                            .subscribeOn(Schedulers.io())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    srl_refresh.isRefreshing = false
                }
                .subscribe({
                    multiTypeAdapter.dataList.clear()
                    multiTypeAdapter.dataList.addAll(it)
                    multiTypeAdapter.notifyDataSetChanged()
                }, {
                    ErrorHandler.showNetworkError(this, it)
                })
                .bindToLifecycle(this)
    }

    /**
     * 显示邀请提示
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showInviteeDialog(channel: Channel) {
        val inviteeDialog = InviteeDialog(this, channel)
        inviteeDialog.show()
    }
}