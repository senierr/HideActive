package com.hideactive.domain

import android.content.Intent
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
import com.hideactive.util.JustalkHelper
import com.module.library.util.OnThrottleClickListener
import com.senierr.adapter.internal.MultiTypeAdapter
import com.senierr.adapter.internal.RVHolder
import com.senierr.adapter.internal.ViewHolderWrapper
import com.senierr.repository.Repository
import com.senierr.repository.bean.Channel
import com.senierr.repository.bean.User
import com.senierr.repository.service.api.IChannelService
import com.senierr.repository.service.api.IUserService
import com.zego.zegoliveroom.ZegoLiveRoom
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

/**
 * 首页
 *
 * @author zhouchunjie
 * @date 2018/8/31
 */
class MainActivity : BaseActivity() {

    private val channelService = Repository.getService<IChannelService>()
    private val userService = Repository.getService<IUserService>()
    private val multiTypeAdapter = MultiTypeAdapter()

    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        checkChannel()

        // 自动刷新
        srl_refresh.postDelayed({
            srl_refresh.isRefreshing = true
            loadData()
        }, 300)
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

                val remark = userService.getRemark(p1.objectId)
                val name = if (!TextUtils.isEmpty(remark)) {
                    remark
                } else if (!TextUtils.isEmpty(p1.nickname)) {
                    p1.nickname
                } else {
                    p1.account
                }

                tvPortrait.text = name?.get(0).toString()
                tvName.text = name
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

                    // 设置账户信息
                    ZegoLiveRoom.setUser(it.objectId, it.account!!)
                    JustalkHelper.setUser(it.objectId)

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
     * 检查是否有邀请
     */
    private fun checkChannel() {
        Observable.interval(0, 10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    return@flatMap channelService.getServerData()
                }
                .flatMap {
                    return@flatMap channelService.getAllAvailableChannel(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list->
                    var channel: Channel? = null
                    list.forEach {
                        if (it.invitee.objectId == currentUser?.objectId) {
                            channel = it
                        }
                    }
                    channel?.let {
                        val inviteeDialog = InviteeDialog(this, it)
                        inviteeDialog.setOnAcceptListener(object : InviteeDialog.OnAcceptListener {
                            override fun onAccept(channel: Channel) {
                                // 接受邀请
                                when (channel.line) {
                                    "1" -> {
                                        AgoraActivity.startChat(this@MainActivity, channel.objectId)
                                    }
                                    "2" -> {
                                        val intent = Intent(this@MainActivity, ZegoActivity::class.java)
                                        intent.putExtra("channelId", channel.objectId)
                                        intent.putExtra("userId", channel.invitee.objectId)
                                        startActivity(intent)
                                    }
                                    "3" -> {
                                        JustTalkActivity.startChat(this@MainActivity,
                                                channel.objectId,
                                                channel.owner.objectId)
                                    }
                                }
                            }
                        })
                        inviteeDialog.show()
                    }
                }, {
                    ErrorHandler.showNetworkError(this, it)
                })
                .bindToLifecycle(this)
    }
}