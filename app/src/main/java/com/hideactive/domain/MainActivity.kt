package com.hideactive.domain

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.comm.ErrorHandler
import com.hideactive.dialog.EditDialog
import com.hideactive.dialog.InviteeDialog
import com.hideactive.ext.bindToLifecycle
import com.hideactive.util.JustalkHelper
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
        val userWrapper = object : ViewHolderWrapper<User>() {
            override fun onCreateViewHolder(p0: ViewGroup): RVHolder {
                return RVHolder.create(p0, R.layout.item_user)
            }

            override fun onBindViewHolder(p0: RVHolder, p1: User) {
                val tvName = p0.getView<TextView>(R.id.tv_name)

                val remark = userService.getRemark(p1.objectId)
                var name = if (!TextUtils.isEmpty(remark)) {
                    remark
                } else if (!TextUtils.isEmpty(p1.nickname)) {
                    p1.nickname
                } else {
                    p1.account
                }

                currentUser?.let {
                    if (p1.objectId == it.objectId) {
                        name = "$name(当前用户)"
                    }
                }

                tvName.text = name
            }
        }
        userWrapper.onItemClickListener = object : ViewHolderWrapper.OnItemClickListener() {
            override fun onClick(viewHolder: RVHolder?, position: Int) {
                if (position == 0) {
                    return
                }
                val user = multiTypeAdapter.dataList[position] as User
                createChannel(user)
            }

            override fun onLongClick(viewHolder: RVHolder?, position: Int): Boolean {
                if (position == 0) {
                    return false
                }
                updateRemark(position)
                return true
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
     * 加载数据
     */
    private fun loadData() {
        userService.getCacheUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    currentUser = it
                    // 设置账户信息
                    ZegoLiveRoom.setUser(it.objectId, it.account!!)
                    JustalkHelper.setUser(it.objectId)

                    return@flatMap userService.getOtherUsers(it.objectId)
                            .subscribeOn(Schedulers.io())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    srl_refresh.isRefreshing = false
                }
                .subscribe({
                    multiTypeAdapter.dataList.clear()

                    multiTypeAdapter.dataList.add(currentUser)

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
                        if (it.inviteeId == currentUser?.objectId) {
                            channel = it
                        }
                    }
                    channel?.let {
                        val inviteeDialog = InviteeDialog(this, it)
                        inviteeDialog.setOnAcceptListener(object : InviteeDialog.OnActionListener {
                            override fun onAccept(channel: Channel) {
                                openChannel(channel)
                            }

                            override fun onReject(channel: Channel) {
                                channelService.deleteChannel(it.objectId)
                                        .subscribeOn(Schedulers.io())
                                        .subscribe()
                                        .bindToLifecycle(this@MainActivity)
                            }
                        })
                        inviteeDialog.show()
                    }
                }, {
                    ErrorHandler.showNetworkError(this, it)
                })
                .bindToLifecycle(this)
    }

    /**
     * 更新备注
     */
    private fun updateRemark(position: Int) {
        val user = multiTypeAdapter.dataList[position] as User
        val editDialog = EditDialog(this, defaultText = userService.getRemark(user.objectId))
        editDialog.setOnEditListener(object : EditDialog.OnEditListener {
            override fun onConfirm(text: String) {
                userService.setRemark(user.objectId, text)
                multiTypeAdapter.notifyItemChanged(position)
            }
        })
        editDialog.show()
    }

    /**
     * 创建视频通话
     */
    private fun createChannel(user: User) {
        val bottomSheetDialog = BottomSheetDialog(this@MainActivity)
        bottomSheetDialog.setContentView(R.layout.layout_lines)
        val btnLine1 = bottomSheetDialog.findViewById<Button>(R.id.btn_line_1)
        val btnLine2 = bottomSheetDialog.findViewById<Button>(R.id.btn_line_2)
        val btnLine3 = bottomSheetDialog.findViewById<Button>(R.id.btn_line_3)

        val onLineSwitchListener = View.OnClickListener { view ->
            val line = when (view.id) {
                R.id.btn_line_1 -> 1
                R.id.btn_line_2 -> 2
                R.id.btn_line_3 -> 3
                else -> 1
            }
            userService.getCacheUser()
                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        return@flatMap channelService.create(it, user, line)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        bottomSheetDialog.cancel()
                    }
                    .subscribe({
                        openChannel(it)
                    }, {
                        ErrorHandler.showNetworkError(this, it)
                    })
                    .bindToLifecycle(this)
        }
        btnLine1?.setOnClickListener(onLineSwitchListener)
        btnLine2?.setOnClickListener(onLineSwitchListener)
        btnLine3?.setOnClickListener(onLineSwitchListener)

        bottomSheetDialog.show()
    }

    /**
     * 打开频道
     */
    private fun openChannel(channel: Channel) {
        when (channel.line) {
            1 -> {
                AgoraActivity.startChat(this, channel.objectId)
            }
            2 -> {
                val intent = Intent(this, ZegoActivity::class.java)
                intent.putExtra("channelId", channel.objectId)
                intent.putExtra("userId", channel.owner.objectId)
                startActivity(intent)
            }
            3 -> {
                JustTalkActivity.startChat(this,
                        channel.objectId,
                        channel.inviteeId)
            }
        }
    }
}