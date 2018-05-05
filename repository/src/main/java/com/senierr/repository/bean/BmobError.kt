package com.senierr.repository.bean

/**
 * Bmob请求错误
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
data class BmobError(
        var code: Int = UNKNOWN,
        var error: String? = null
) : Throwable(error) {

    companion object {
        const val UNKNOWN = -1
        const val ACCOUNT_REPEAT = 0
        const val NICKNAME_REPEAT = 1
        const val ACCOUNT_OR_PASSWORD_ERROR = 101
    }
}