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
        var username: String? = null,           // 账号
        var password: String? = null,           // 密码
        var nickname: String? = null,           // 昵称
        var portrait: String? = null,           // 头像
        var grander: Int = 0,                   // 性别 0：保密，1：男，2：女
        var sessionToken: String? = null        // 登录验证
)