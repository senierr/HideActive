package com.senierr.repository.bean

/**
 * Bmob对象
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
data class BmobError(
        var code: Int = UNKNOWN,
        var error: String? = null
) : Exception(error) {
    companion object {
        const val UNKNOWN = -1
        const val ACCOUNT_REPEAT = 0
        const val NICKNAME_REPEAT = 1
        const val UN_LOGGED_IN = 2
        // 服务器返回错误
        const val ACCOUNT_OR_PASSWORD_ERROR = 101
    }
}

data class BmobInsert(
        var objectId: String,
        var createdAt: String
)

data class BmobUpdate(
        var updatedAt: String
)

data class BmobDelete(
        var msg: String     // ok/fail
)

data class BmobArray<T>(
        var results: MutableList<T>? = null,
        var count: Int = 0
)