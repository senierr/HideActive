package com.hideactive.domain

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.ImageView
import com.hideactive.R
import com.hideactive.util.JustalkHelper
import com.juphoon.cloud.JCMediaChannel
import com.juphoon.cloud.JCMediaChannelParticipant
import com.juphoon.cloud.JCMediaDevice
import com.senierr.permission.CheckCallback
import com.senierr.permission.PermissionManager
import kotlinx.android.synthetic.main.activity_agora.*



class JustTalkActivity : AppCompatActivity() {

    private lateinit var channelId: String
    private lateinit var userId: String

    companion object {
        fun startChat(context: Context, channelId: String, userId: String) {
            val intent = Intent(context, JustTalkActivity::class.java)
            intent.putExtra("channelId", channelId)
            intent.putExtra("userId", userId)
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
        userId = intent.getStringExtra("userId")

        PermissionManager.with(this)
                .permissions(Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO)
                .request(object : CheckCallback() {
                    override fun onAllGranted() {
                        initJustalk()
                    }
                })
    }

    override fun onDestroy() {
        leaveChannel()
        super.onDestroy()
    }

    private fun initJustalk() {
        // 加入频道
        JustalkHelper.mediaChannel.join(channelId, null)
        JustalkHelper.channelCallback = object : JustalkHelper.ChannelCallback{
            override fun onJoin(p0: Boolean, p1: Int, p2: String?) {
                startPreview()
                startRemotePreview()
            }

            override fun onParticipantJoin(p0: JCMediaChannelParticipant?) {
                startRemotePreview()
            }
        }

        btn_mute.setOnClickListener {
            val iv = it as ImageView
            iv.isSelected = !iv.isSelected
            localAudioMuted(iv.isSelected)
        }

        btn_switch_camera.setOnClickListener {
            switchCamera()
        }

        btn_end_call.setOnClickListener { finish() }
    }

    /**
     * 开启本地预览
     */
    private fun startPreview() {
        val localCanvas = JustalkHelper.mediaDevice.startCameraVideo(JCMediaDevice.RENDER_FULL_AUTO)
        fl_local.removeAllViews()
        localCanvas.videoView.setZOrderOnTop(true)
        fl_local.addView(localCanvas.videoView)
    }

    /**
     * 开启远程预览
     */
    private fun startRemotePreview() {
        val partps = JustalkHelper.mediaChannel.participants
        val item = partps.firstOrNull {
            it.userId == userId
        }
        item?.let {
            val remoteCanvas = JustalkHelper.mediaDevice.startVideo(it.renderId, JCMediaDevice.RENDER_FULL_AUTO)
            fl_remote.removeAllViews()
            fl_remote.addView(remoteCanvas.videoView)
            JustalkHelper.mediaChannel.requestVideo(it, JCMediaChannel.PICTURESIZE_LARGE)
        }
    }

    /**
     * 本地用户声音静默
     */
    private fun localAudioMuted(muted: Boolean) {
        JustalkHelper.mediaChannel.enableUploadAudioStream(muted)
    }

    /**
     * 切换摄像头
     */
    private fun switchCamera() {
        JustalkHelper.mediaDevice.switchCamera()
    }

    /**
     * 离开频道
     */
    private fun leaveChannel() {
        JustalkHelper.mediaChannel.leave()
        JustalkHelper.channelCallback = null
    }
}