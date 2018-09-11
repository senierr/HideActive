package com.hideactive.domain

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.senierr.adapter.internal.MultiTypeAdapter
import com.senierr.adapter.internal.RVHolder
import com.senierr.adapter.internal.ViewHolderWrapper
import com.senierr.repository.bean.User
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 首页
 *
 * @author zhouchunjie
 * @date 2018/8/31
 */
class MainActivity : BaseActivity() {

    private val multiTypeAdapter = MultiTypeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        tb_top.setTitle(R.string.app_name)
        tb_top.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance)
        setSupportActionBar(tb_top)

        val userWrapper = object : ViewHolderWrapper<User>() {
            override fun onCreateViewHolder(p0: ViewGroup): RVHolder {
                return RVHolder.create(p0, R.layout.item_user)
            }

            override fun onBindViewHolder(p0: RVHolder, p1: User) {
                val ivIcon = p0.getView<ImageView>(R.id.iv_icon)
                val tvNickname = p0.getView<TextView>(R.id.iv_icon)

                Glide.with(ivIcon.context)
                        .load(p1.portrait)
                        .into(ivIcon)
                tvNickname.text = if (TextUtils.isEmpty(p1.nickname)) p1.account else p1.nickname
            }
        }
        userWrapper.onItemClickListener = object : ViewHolderWrapper.OnItemClickListener() {
            override fun onClick(viewHolder: RVHolder?, position: Int) {
                // todo
            }
        }
        multiTypeAdapter.bind(User::class.java, userWrapper)

        rv_users.layoutManager = LinearLayoutManager(this)
        rv_users.adapter = multiTypeAdapter
    }
}