package com.senierr.repository.bean

/**
 * Bmob请求错误
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
data class BmobError(
        var code: Int = -1,
        var error: String? = null
) : Throwable(error)