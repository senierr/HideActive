package com.senierr.repository.remote

import com.google.gson.JsonParser
import com.senierr.http.internal.Result
import io.reactivex.functions.Function

/**
 * 数据转换
 *
 * @author zhouchunjie
 * @date 2018/8/30
 */

/** 数量筛选 */
class CountFunction : Function<Result<String>, Int> {

    override fun apply(t: Result<String>): Int {
        val str = t.body()
        return if (str == null) 0 else {
            JsonParser().parse(str)
                    .asJsonObject
                    .get("count")
                    .asInt
        }
    }
}

/** 是否有结果 */
class IsHaveFunction : Function<Result<String>, Boolean> {

    override fun apply(t: Result<String>): Boolean {
        val str = t.body()
        return if (str == null) false else {
            JsonParser().parse(str)
                    .asJsonObject
                    .get("count")
                    .asInt > 0
        }
    }
}
