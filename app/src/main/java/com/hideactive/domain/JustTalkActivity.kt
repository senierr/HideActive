package com.hideactive.domain

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.ext.bindToLifecycle
import com.hideactive.util.JustalkHelper
import com.juphoon.cloud.JCMediaChannel
import com.juphoon.cloud.JCMediaChannelParticipant
import com.juphoon.cloud.JCMediaDevice
import com.module.library.util.ToastUtil
import com.senierr.permission.CheckCallback
import com.senierr.permission.PermissionManager
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobError
import com.senierr.repository.service.api.IChannelService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_agora.*
import java.util.concurrent.TimeUnit


class JustTalkActivity : BaseActivity() {

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
                        checkUser()
                    }
                })
    }

    override fun onDestroy() {
        leaveChannel()

        Repository.getService<IChannelService>().deleteChannel(channelId)
                .subscribeOn(Schedulers.io())
                .subscribe({}, {})

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

            override fun onParticipantLeft(p0: JCMediaChannelParticipant?) {
                ToastUtil.showShort(this@JustTalkActivity, R.string.user_offline)
                finish()
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

    private fun checkUser() {
        Observable.interval(0, 3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    return@flatMap Repository.getService<IChannelService>()
                            .get(channelId)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {
                    if (it is BmobError) {
                        ToastUtil.showShort(this@JustTalkActivity, R.string.user_reject)
                        finish()
                    }
                })
                .bindToLifecycle(this)
    }
}