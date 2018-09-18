package com.hideactive.domain

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.TextureView
import android.view.WindowManager
import android.widget.ImageView
import com.hideactive.R
import com.hideactive.util.ZegoHelper
import com.module.library.util.ToastUtil
import com.senierr.permission.CheckCallback
import com.senierr.permission.PermissionManager
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback
import com.zego.zegoliveroom.constants.ZegoConstants
import com.zego.zegoliveroom.constants.ZegoVideoViewMode
import com.zego.zegoliveroom.entity.ZegoStreamInfo
import kotlinx.android.synthetic.main.activity_agora.*

class ZegoActivity : AppCompatActivity() {

    private lateinit var channelId: String

    private var isLoginRoom = false
    private var hasLoginRoom = false

    companion object {
        fun startChat(context: Context, channelId: String, userId: String, userName: String) {
            ZegoHelper.setUser(userId, userName)
            val intent = Intent(context, ZegoActivity::class.java)
            intent.putExtra("channelId", channelId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agora)

        // 禁止手机休眠
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        channelId = intent.getStringExtra("channelId")

        PermissionManager.with(this)
                .permissions(Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO)
                .request(object : CheckCallback() {
                    override fun onAllGranted() {
                        initZego()
                    }
                })
    }

    override fun onDestroy() {
        leaveChannel()
        super.onDestroy()
    }

    private fun initZego() {
        val success = ZegoHelper.zegoLiveRoom.loginRoom(channelId, ZegoConstants.RoomRole.Audience,
                IZegoLoginCompletionCallback { i, arrayOfZegoStreamInfos ->
            if (i != 0) {
                ToastUtil.showLong(this, "Login room failed: $i")
            }
            startPublishStream()
            startRemotePreview(arrayOfZegoStreamInfos)
        })
        if (success) {
            isLoginRoom = true
            startPreview()
        }

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
     * 开启本地预览
     */
    private fun startPreview() {
        ZegoHelper.zegoLiveRoom.enableMic(true)
        ZegoHelper.zegoLiveRoom.enableCamera(true)
        ZegoHelper.zegoLiveRoom.enableSpeaker(true)

        val textureView = TextureView(this)
        fl_local.addView(textureView)

        ZegoHelper.zegoLiveRoom.setPreviewView(textureView)
        ZegoHelper.zegoLiveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFit)
        ZegoHelper.zegoLiveRoom.startPreview()
    }

    /**
     * 开启推流
     */
    private fun startPublishStream() {
        ZegoHelper.zegoLiveRoom.startPublishing(channelId, channelId, ZegoConstants.PublishFlag.JoinPublish)
    }

    /**
     * 开启远程预览
     */
    private fun startRemotePreview(streamList: Array<ZegoStreamInfo>?) {
        if (streamList != null && streamList.isNotEmpty()) {
            val textureView = TextureView(this)
            fl_remote.addView(textureView)

            ZegoHelper.zegoLiveRoom.startPlayingStream(streamList[0].streamID, textureView)
            ZegoHelper.zegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFit, streamList[0].streamID)
        }
    }

    /**
     * 本地用户声音静默
     */
    private fun localAudioMuted(muted: Boolean) {
        ZegoHelper.zegoLiveRoom.enableMic(muted)
    }

    /**
     * 切换摄像头
     */
    private fun switchCamera(isFront: Boolean) {
        ZegoHelper.zegoLiveRoom.setFrontCam(isFront)
    }

    /**
     * 离开频道
     */
    private fun leaveChannel() {
        ZegoHelper.zegoLiveRoom.stopPlayingStream(channelId)
        ZegoHelper.zegoLiveRoom.stopPublishing()
        ZegoHelper.zegoLiveRoom.stopPreview()
        if (isLoginRoom || hasLoginRoom) {
            ZegoHelper.zegoLiveRoom.logoutRoom()
        }
    }
}