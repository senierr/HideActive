package com.hideactive.domain.base

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.hideactive.domain.comm.ErrorHandler
import com.hideactive.domain.user.LoginActivity
import com.hideactive.ext.bindToLifecycle
import com.senierr.repository.Repository
import com.senierr.repository.service.api.IUserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Activity基类
 *
 * @author zhouchunjie
 * @date 2018/5/28
 */
open class BaseActivity : AppCompatActivity() {

    companion object {
        const val LOGIN_REDIRECT_REQUEST_CODE = 65535
        const val EXTRA_KEY_TARGET_REQUEST_CODE = "target_request_code"
        const val EXTRA_KEY_TARGET_COMPONENT = "target_component"
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    /**
     * 绑定至生命周期
     */
    fun bindToLifecycle(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    /**
     * 处理用户登录验证重定向
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_REDIRECT_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val targetComponent = data.getParcelableExtra<ComponentName>(EXTRA_KEY_TARGET_COMPONENT)
            val targetRequestCode = data.getIntExtra(EXTRA_KEY_TARGET_REQUEST_CODE, LOGIN_REDIRECT_REQUEST_CODE)
            // 重定向跳转
            targetComponent?.let {
                data.component = it
                if (targetRequestCode == LOGIN_REDIRECT_REQUEST_CODE) {
                    startActivity(data)
                } else {
                    startActivityForResult(data, targetRequestCode)
                }
            }
        }
    }

    /**
     * 验证用户登录状态，并startActivity
     */
    fun startActivityWithVerify(context: Context, intent: Intent) {
        Repository.getService<IUserService>().isLoggedIn()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) {
                        context.startActivity(intent)
                    } else {
                        // 重定向
                        intent.putExtra(EXTRA_KEY_TARGET_COMPONENT, intent.component)
                        intent.putExtra(EXTRA_KEY_TARGET_REQUEST_CODE, LOGIN_REDIRECT_REQUEST_CODE)
                        intent.setClass(context, LoginActivity::class.java)
                        startActivityForResult(intent, LOGIN_REDIRECT_REQUEST_CODE)
                    }
                }, {
                    ErrorHandler.showNetworkError(context, it)
                })
                .bindToLifecycle(this)
    }

    /**
     * 验证用户登录状态，并startActivityForResult
     */
    fun startActivityWithVertrifyForResult(context: Activity, intent: Intent, requestCode: Int) {
        if (requestCode == LOGIN_REDIRECT_REQUEST_CODE) {
            throw IllegalArgumentException("RequestCode must be not 65535!")
        }
        Repository.getService<IUserService>().isLoggedIn()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) {
                        context.startActivityForResult(intent, requestCode)
                    } else {
                        // 重定向
                        intent.putExtra(EXTRA_KEY_TARGET_COMPONENT, intent.component)
                        intent.putExtra(EXTRA_KEY_TARGET_REQUEST_CODE, requestCode)
                        intent.setClass(context, LoginActivity::class.java)
                        context.startActivityForResult(intent, LOGIN_REDIRECT_REQUEST_CODE)
                    }
                }, {
                    ErrorHandler.showNetworkError(context, it)
                })
                .bindToLifecycle(this)
    }
}