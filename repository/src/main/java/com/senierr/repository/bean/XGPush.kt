package com.senierr.repository.bean

/**
 * 信鸽推送
 *
 * @author zhouchunjie
 * @date 2018/9/3
 */
data class PushBean(
        val token_list: MutableList<String>,
        val message: PushMessage
) {
    val audience_type: String = "token"
    val platform: String = "android"
    val message_type: String = "message"
}

data class PushMessage(
        val title: String,
        val content: MutableList<String>,
        val custom_content: Map<String, String>
)

data class PushResponse(
        val seq: Long,
        val push_id: String,
        val ret_code: Int,
        val err_msg: String
)