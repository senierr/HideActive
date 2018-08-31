package com.hideactive.domain.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hideactive.R

/**
 * 我的页
 *
 * @author zhouchunjie
 * @date 2018/8/31
 */
class MineFragment : Fragment() {

    companion object {
        fun getInstance(): MineFragment {
            return MineFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }
}