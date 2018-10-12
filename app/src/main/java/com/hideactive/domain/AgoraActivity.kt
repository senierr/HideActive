package com.hideactive.domain

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.ext.bindToLifecycle
import com.module.library.util.ToastUtil
import com.senierr.permission.CheckCallback
import com.senierr.permission.PermissionManager
import com.senierr.repository.Repository
import com.senierr.repository.bean.BmobError
import com.senierr.repository.service.api.IChannelService
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_agora.*
import java.util.concurrent.TimeUnit

class AgoraActivity : BaseActivity() {

    private var rtcEngine: RtcEngine? = null

    private lateinit var channelId: String

    companion object {
        fun startChat(context: Context, channelId: String) {
            val intent = Intent(context, AgoraActivity::class.java)
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
                        initAgora(channelId)
                        checkUser()
                    }
                })
    }

    override fun onDestroy() {
        leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null

        Repository.getService<IChannelService>().deleteChannel(channelId)
                .subscribeOn(Schedulers.io())
                .subscribe({}, {})

        super.onDestroy()
    }

    private fun initAgora(channelName: String) {
        rtcEngine = RtcEngine.create(this, "e4274e53f2de41ef95ba847248839dfa", object : IRtcEngineEventHandler() {
            override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
                runOnUiThread { setupRemoteVideo(uid) }
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                runOnUiThread { onUserOffline() }
            }

            override fun onUserMuteAudio(uid: Int, muted: Boolean) {
                runOnUiThread { onRemoteUserVideoMuted(uid, muted) }
            }
        })
        rtcEngine?.enableVideo()
        rtcEngine?.setVideoProfile(Constants.VIDEO_PROFILE_360P, false)
        val localSurface = RtcEngine.CreateRendererView(baseContext)
        localSurface.setZOrderMediaOverlay(true)
        fl_local.addView(localSurface)
        rtcEngine?.setupLocalVideo(VideoCanvas(localSurface, VideoCanvas.RENDER_MODE_ADAPTIVE, 0))
        rtcEngine?.joinChannel(null, channelName, "Extra Optional Data", 0)

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
     * 启动远程视频画面
     */
    private fun setupRemoteVideo(uid: Int) {
        if (fl_remote.childCount >= 1) {
            return
        }
        val remoteSurface = RtcEngine.CreateRendererView(baseContext)
        fl_remote.addView(remoteSurface)
        rtcEngine?.setupRemoteVideo(VideoCanvas(remoteSurface, VideoCanvas.RENDER_MODE_ADAPTIVE, uid))
        remoteSurface.tag = uid
    }

    /**
     * 远程用户下线
     */
    private fun onUserOffline() {
        fl_remote.removeAllViews()
        ToastUtil.showShort(this, R.string.user_offline)
        finish()
    }

    /**
     * 远程用户视频显示
     */
    private fun onRemoteUserVideoMuted(uid: Int, muted: Boolean) {
        val surfaceView = fl_remote.getChildAt(0) as SurfaceView
        val tag = surfaceView.tag
        if (tag != null && tag as Int == uid) {
            surfaceView.visibility = if (muted) View.GONE else View.VISIBLE
        }
    }

    /**
     * 本地用户声音静默
     */
    private fun localAudioMuted(muted: Boolean) {
        rtcEngine?.muteLocalAudioStream(muted)
    }

    /**
     * 本地用户视频静默
     */
    private fun localVideoMuted(muted: Boolean) {
        rtcEngine?.muteLocalVideoStream(muted)
        val surfaceView = fl_local.getChildAt(0) as SurfaceView
        surfaceView.setZOrderMediaOverlay(!muted)
        surfaceView.visibility = if (muted) View.GONE else View.VISIBLE
    }

    private fun switchCamera() {
        rtcEngine?.switchCamera()
    }

    /**
     * 离开频道
     */
    private fun leaveChannel() {
        rtcEngine?.leaveChannel()
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
                        ToastUtil.showShort(this@AgoraActivity, R.string.user_reject)
                        finish()
                    }
                })
                .bindToLifecycle(this)
    }
}