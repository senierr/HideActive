package com.hideactive.logic.post

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.util.GridItemDecoration
import com.senierr.adapter.internal.MultiTypeAdapter
import com.senierr.adapter.internal.RVHolder
import com.senierr.adapter.internal.ViewHolderWrapper
import kotlinx.android.synthetic.main.activity_publish_post.*
import kotlinx.android.synthetic.main.item_image_add.*

/**
 * 发布帖子
 *
 * @author zhouchunjie
 * @date 2018/5/6
 */
class PublishPostActivity : BaseActivity() {

    private val multiTypeAdapter = MultiTypeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish_post)

        initView()
    }

    /**
     * 初始化界面
     */
    private fun initView() {
        tb_top.setTitle(R.string.login)
        tb_top.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance)
        setSupportActionBar(tb_top)
        tb_top.setNavigationIcon(R.drawable.ic_back_white)
        tb_top.setNavigationOnClickListener {
            finish()
        }

        multiTypeAdapter.bind(String::class.java, object : ViewHolderWrapper<String>() {
            override fun onCreateViewHolder(p0: ViewGroup): RVHolder {
                return RVHolder.create(p0, R.layout.item_image_add)
            }

            override fun onBindViewHolder(p0: RVHolder, p1: String) {
                Glide.with(iv_image)
                        .asBitmap()
                        .into(iv_image)
            }
        })
        rv_images.layoutManager = GridLayoutManager(this, 3)
        rv_images.setHasFixedSize(true)
        rv_images.addItemDecoration(GridItemDecoration(this, R.dimen.dp_4))
        rv_images.adapter = multiTypeAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_publish_post, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_publish) {
            val bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(R.layout.dialog_image_pick)
            bottomSheetDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }
}