package com.hideactive.base

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable

/**
 * Fragment基类
 *
 * @author zhouchunjie
 * @date 2018/2/26
 */
abstract class BaseFragment : Fragment() {

    val compositeDisposable = CompositeDisposable()

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}