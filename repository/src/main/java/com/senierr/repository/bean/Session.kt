package com.senierr.repository.bean

/**
 * 会话
 *
 * @author zhouchunjie
 * @date 2018/3/29
 */
data class Channel(
        var objectId: String,

        var line: String,       // 线路
        var owner: User,        // 创建者
        var invitee: User,      // 被邀请者

        var createdAt: String,  // 创建时间
        var updatedAt: String   // 更新时间
)