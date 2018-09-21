package com.hideactive.util

import android.content.Context
import com.juphoon.cloud.*


/**
 * Zego辅助类
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
object JustalkHelper : JCClientCallback, JCMediaDeviceCallback, JCMediaChannelCallback {

    private const val APP_KEY = "bad42d880918973472ef5097"

    lateinit var client: JCClient
    lateinit var mediaDevice: JCMediaDevice
    lateinit var mediaChannel: JCMediaChannel
    var channelCallback: ChannelCallback? = null


    interface ChannelCallback {
        fun onJoin(p0: Boolean, p1: Int, p2: String?)
        fun onParticipantJoin(p0: JCMediaChannelParticipant?)
    }

    /**
     * 初始化
     */
    fun init(context: Context) {
        client = JCClient.create(context, APP_KEY, this, null)
        mediaDevice = JCMediaDevice.create(client, this)
        mediaChannel = JCMediaChannel.create(JustalkHelper.client, JustalkHelper.mediaDevice, this)
        // 设置频道人数
        mediaChannel.setConfig(JCMediaChannel.CONFIG_CAPACITY, "3")
        mediaChannel.enableAudioOutput(true)
        mediaChannel.enableRecord()
        // 发送本地音频流
        mediaChannel.enableUploadAudioStream(true)
        // 发送本地视频流
        mediaChannel.enableUploadVideoStream(true)
    }

    /**
     * 设置用户
     */
    fun setUser(userId: String) {
        client.login(userId, "123456")
    }

    override fun onLogout(p0: Int) {
    }

    override fun onLogin(p0: Boolean, p1: Int) {
        LogUtil.logE("onLogin: $p0")
    }

    override fun onClientStateChange(p0: Int, p1: Int) {
    }

    override fun onAudioOutputTypeChange(p0: Boolean) {
    }

    override fun onCameraUpdate() {
    }

    override fun onParticipantLeft(p0: JCMediaChannelParticipant?) {
    }

    override fun onParticipantUpdate(p0: JCMediaChannelParticipant?) {
    }

    override fun onMediaChannelStateChange(p0: Int, p1: Int) {
    }

    override fun onParticipantJoin(p0: JCMediaChannelParticipant?) {
        channelCallback?.onParticipantJoin(p0)
    }

    override fun onMessageReceive(p0: String?, p1: String?, p2: String?) {
    }

    override fun onLeave(p0: Int, p1: String?) {
    }

    override fun onMediaChannelPropertyChange() {
    }

    override fun onJoin(p0: Boolean, p1: Int, p2: String?) {
        channelCallback?.onJoin(p0, p1, p2)
    }

    override fun onInviteSipUserResult(p0: Int, p1: Boolean, p2: Int) {
    }

    override fun onQuery(p0: Int, p1: Boolean, p2: Int, p3: JCMediaChannelQueryInfo?) {
    }
}