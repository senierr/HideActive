package com.hideactive.logic.post

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Selection
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hideactive.R
import com.hideactive.base.BaseActivity
import com.hideactive.ext.hideSoftInput
import com.hideactive.ext.isGone
import com.hideactive.util.EmotionUtil
import com.hideactive.util.GridItemDecoration
import com.senierr.adapter.internal.MultiTypeAdapter
import com.senierr.adapter.internal.RVHolder
import com.senierr.adapter.internal.ViewHolderWrapper
import com.senierr.repository.util.LogUtil
import kotlinx.android.synthetic.main.activity_publish_post.*
import kotlinx.android.synthetic.main.item_image.*

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
        // 输入框
        et_content.setOnFocusChangeListener { _, hasFocus ->
            // 输入框获取焦点，隐藏表情栏
            if (hasFocus) rv_emotions.isGone(true)
        }
        // 插入图片
        multiTypeAdapter.bind(String::class.java, object : ViewHolderWrapper<String>() {
            override fun onCreateViewHolder(p0: ViewGroup): RVHolder {
                return RVHolder.create(p0, R.layout.item_image)
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
        // 表情
        initEmotion()
        // 添加图片按钮

        // 添加表情按钮
        btn_add_emotion.setOnClickListener {
            rv_emotions.isGone(false)
            hideSoftInput()
        }
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

    /**
     * 初始化表情
     */
    private fun initEmotion() {
        val emotionAdapter = MultiTypeAdapter()
        val emotionWrapper = object : ViewHolderWrapper<EmotionUtil.Emotion>() {
            override fun onCreateViewHolder(p0: ViewGroup): RVHolder {
                return RVHolder.create(p0, R.layout.item_image)
            }

            override fun onBindViewHolder(p0: RVHolder, p1: EmotionUtil.Emotion) {
                val imageView = p0.getView<ImageView>(R.id.iv_image)
                Glide.with(imageView)
                        .asBitmap()
                        .apply(RequestOptions().placeholder(R.drawable.ic_emotion))
                        .load(p1.resId)
                        .into(imageView)
            }
        }
        emotionWrapper.onItemClickListener = object : ViewHolderWrapper.OnItemClickListener() {
            override fun onClick(viewHolder: RVHolder?, position: Int) {
                // 插入表情
                val startIndex = et_content.selectionStart
                val tag = EmotionUtil.emotions[position].tag
                et_content.setText(EmotionUtil.format(et_content, et_content.text.insert(startIndex, tag)))
                // 定位光标位置
                Selection.setSelection(et_content.text, startIndex + tag.length)
            }
        }
        emotionAdapter.bind(EmotionUtil.Emotion::class.java, emotionWrapper)
        rv_emotions.layoutManager = GridLayoutManager(this, 7)
        rv_emotions.setHasFixedSize(true)
        rv_emotions.addItemDecoration(GridItemDecoration(this, R.dimen.dp_16, true))
        emotionAdapter.dataList.addAll(EmotionUtil.emotions)
        rv_emotions.adapter = emotionAdapter
    }
}