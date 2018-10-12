package com.hideactive.domain

import android.Manifest
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.ext.bindToLifecycle
import com.hideactive.util.ZegoAppHelper
import com.module.library.util.ToastUtil
import com.senierr.permission.CheckCallback
import com.senierr.permission.PermissionManager
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobError
import com.senierr.repository.service.api.IChannelService
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback
import com.zego.zegoliveroom.callback.IZegoRoomCallback
import com.zego.zegoliveroom.constants.ZegoConstants
import com.zego.zegoliveroom.constants.ZegoVideoViewMode
import com.zego.zegoliveroom.entity.ZegoStreamInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_zego.*
import java.util.concurrent.TimeUnit

class ZegoActivity : BaseActivity() {

    private var mPublishStreamId = ""
    private var mIsLoginRoom: Boolean = false   // 是否正在登录房间
    private var mHasLoginRoom: Boolean = false  // 是否已成功登录房间

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zego)
        // 禁止手机休眠
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        PermissionManager.with(this)
                .permissions(Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO)
                .request(object : CheckCallback() {
                    override fun onAllGranted() {
                        initView()
                        setupCallback()
                        loginRoom()
                        checkUser()
                    }
                })
    }

    override fun onDestroy() {
        logoutRoom()

        val sessionId = intent.getStringExtra("channelId")
        Repository.getService<IChannelService>().deleteChannel(sessionId)
                .subscribeOn(Schedulers.io())
                .subscribe({}, {})

        super.onDestroy()
    }

    private fun initView() {
        btn_mute.setOnClickListener {
            val iv = it as ImageView
            iv.isSelected = !iv.isSelected
            localAudioMuted(iv.isSelected)
        }

        btn_switch_camera.setOnClickListener {
            val iv = it as ImageView
            iv.isSelected = !iv.isSelected
            switchCamera(iv.isSelected)
        }

        btn_end_call.setOnClickListener { finish() }
    }

    /**
     * 本地用户声音静默
     */
    private fun localAudioMuted(muted: Boolean) {
        ZegoAppHelper.getLiveRoom().enableMic(muted)
    }

    /**
     * 切换摄像头
     */
    private fun switchCamera(isFront: Boolean) {
        ZegoAppHelper.getLiveRoom().setFrontCam(isFront)
    }

    private fun showRemotePreview(streamInfo: ZegoStreamInfo) {
        val liveRoom = ZegoAppHelper.getLiveRoom()
        val userId = intent.getStringExtra("userId")
        if (TextUtils.isEmpty(streamInfo.userID) || TextUtils.equals(streamInfo.userID, userId)) {
            // preview
            liveRoom.setPreviewView(fl_remote)
            liveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFill)
        } else {
            // play
            liveRoom.updatePlayView(streamInfo.streamID, fl_remote)
            liveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, streamInfo.streamID)
        }
    }

    private fun setupCallback() {
        val liveRoom = ZegoAppHelper.getLiveRoom()
        liveRoom.setZegoRoomCallback(ZegoRoomCallback())
    }

    private fun loginRoom() {
        val sessionId = intent.getStringExtra("channelId")
        val roomName = String.format("From_%s", "video")
        val success = ZegoAppHelper.getLiveRoom().loginRoom(sessionId, roomName, ZegoConstants.RoomRole.Audience, ZegoLgoinCompleteCallback())
        if (success) {
            mIsLoginRoom = true
            startPreview()
        }
    }

    private fun startPreview() {
        val liveRoom = ZegoAppHelper.getLiveRoom()

        liveRoom.enableMic(true)
        liveRoom.enableCamera(true)
        liveRoom.enableSpeaker(true)
        liveRoom.setPreviewView(fl_local)
        liveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFill)
        liveRoom.startPreview()
    }

    private fun startPublishStream() {
        val userId = intent.getStringExtra("userId")
        val streamId = String.format("s-%s", userId)
        val title = String.format("%s is comming", userId)

        val liveRoom = ZegoAppHelper.getLiveRoom()

        // 开始推流
        mPublishStreamId = streamId
        liveRoom.startPublishing(streamId, title, ZegoConstants.PublishFlag.JoinPublish)
    }

    private fun startPlayStreams(streamList: Array<ZegoStreamInfo>?) {
        var i = 0
        while (streamList != null && i < streamList.size) {
            val streamInfo = streamList[i]
            doPlayStream(streamInfo)
            i++
        }
    }

    private fun doPlayStream(streamInfo: ZegoStreamInfo) {
        val streamId = streamInfo.streamID
        val liveRoom = ZegoAppHelper.getLiveRoom()

        liveRoom.startPlayingStream(streamId, null)
        showRemotePreview(streamInfo)
        liveRoom.activateVedioPlayStream(streamId, true, ZegoConstants.VideoStreamLayer.VideoStreamLayer_Auto)
    }

    private fun stopPlayStreams(streamList: Array<ZegoStreamInfo>?) {
        var i = 0
        while (streamList != null && i < streamList.size) {
            val streamInfo = streamList[i]
            doStopPlayStream(streamInfo)
            i++
        }
    }

    private fun doStopPlayStream(streamInfo: ZegoStreamInfo) {
        val streamId = streamInfo.streamID ?: return

        val liveRoom = ZegoAppHelper.getLiveRoom()
        liveRoom.stopPlayingStream(streamId)
    }

    private fun logoutRoom() {
        val liveRoom = ZegoAppHelper.getLiveRoom()
        if (!TextUtils.isEmpty(mPublishStreamId)) {
            liveRoom.stopPublishing()
            liveRoom.stopPreview()
        }

        if (mIsLoginRoom || mHasLoginRoom) {
            liveRoom.logoutRoom()
        }

        liveRoom.setZegoLivePublisherCallback(null)
        liveRoom.setZegoLivePlayerCallback(null)
        liveRoom.setZegoRoomCallback(null)
    }

    private inner class ZegoLgoinCompleteCallback : IZegoLoginCompletionCallback {
        override fun onLoginCompletion(errorCode: Int, streamList: Array<ZegoStreamInfo>) {
            mIsLoginRoom = false
            if (isFinishing) return
            if (errorCode != 0) {
                return
            }

            mHasLoginRoom = true
            startPublishStream()
            startPlayStreams(streamList)
        }
    }

    private inner class ZegoRoomCallback : IZegoRoomCallback {
        override fun onKickOut(reason: Int, roomId: String) {}

        override fun onDisconnect(errorCode: Int, roomId: String) {}

        override fun onReconnect(errorCode: Int, roomId: String) {}

        override fun onTempBroken(errorCode: Int, roomId: String) {}

        override fun onStreamUpdated(type: Int, streamList: Array<ZegoStreamInfo>, roomId: String) {
            if (type == ZegoConstants.StreamUpdateType.Added) {
                startPlayStreams(streamList)
            } else if (type == ZegoConstants.StreamUpdateType.Deleted) {
                stopPlayStreams(streamList)
                ToastUtil.showShort(this@ZegoActivity, R.string.user_offline)
                finish()
            } else {
                Toast.makeText(this@ZegoActivity, "Unknown stream update type $type", Toast.LENGTH_LONG).show()
            }
        }

        override fun onStreamExtraInfoUpdated(streamList: Array<ZegoStreamInfo>, roomId: String) {}

        override fun onRecvCustomCommand(fromUserId: String, fromUserName: String, content: String, roomId: String) {}
    }

    private fun checkUser() {
        val sessionId = intent.getStringExtra("channelId")
        Observable.interval(0, 3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    return@flatMap Repository.getService<IChannelService>()
                            .get(sessionId)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {
                    if (it is BmobError) {
                        ToastUtil.showShort(this@ZegoActivity, R.string.user_reject)
                        finish()
                    }
                })
                .bindToLifecycle(this)
    }
}
