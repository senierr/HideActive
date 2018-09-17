package com.senierr.repository.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * 用户
 *
 * @author zhouchunjie
 * @date 2018/3/29
 */
@Entity(tableName = "User")
data class User(
        @PrimaryKey
        var objectId: String,

        var account: String? = null,            // 账号
        var password: String? = null,           // 密码
        var nickname: String? = null,           // 昵称
        var portrait: String? = null,           // 头像
        var isOnline: Boolean = false,          // 是否在线

        var createdAt: String? = null,          // 创建时间
        var updatedAt: String? = null           // 更新时间
)