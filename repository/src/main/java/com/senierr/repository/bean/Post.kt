package com.senierr.repository.bean

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * 帖子
 *
 * 用户发送内容的载体
 *
 * @author zhouchunjie
 * @date 2018/3/29
 */
@Entity(tableName = "Post")
data class Post(
        @PrimaryKey
        var objectId: String,
        var content: String? = null,    // 文本内容
        var images: String? = null,     // 图片内容（多张数组:['','']）
        @Embedded
        var author: User? = null,       // 作者
        var likeNum: Int = 0,           // 点赞数
        var commentNum: Int = 0,        // 评论数
        var createdAt: String? = null,  // 创建时间
        var updatedAt: String? = null   // 更新时间
)