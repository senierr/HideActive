package com.senierr.repository.bean

/**
 * 会话房间
 *
 * @author zhouchunjie
 * @date 2018/3/29
 */
data class Session(
        var objectId: String,

        var channel: String? = null,    // 频道

        var createdAt: String? = null,  // 创建时间
        var updatedAt: String? = null   // 更新时间
)