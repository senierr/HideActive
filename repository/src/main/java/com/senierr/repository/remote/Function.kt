package com.senierr.repository.remote

import com.senierr.http.internal.Result
import com.senierr.repository.bean.BmobArray
import io.reactivex.functions.Function
import java.io.IOException

/**
 * 数据转换
 *
 * @author zhouchunjie
 * @date 2018/8/30
 */

/** 解析对象 */
class ObjectFunction<T> : Function<Result<T>, T> {
    override fun apply(t: Result<T>): T {
        val body = t.body()
        if (body == null) {
            throw IOException("Response body is null!")
        } else {
            return body
        }
    }
}

/** 数量统计 */
class BmobCountFunction<T> : Function<Result<BmobArray<T>>, Int> {
    override fun apply(t: Result<BmobArray<T>>): Int {
        val body = t.body()
        if (body == null) {
            throw IOException("Response body is null!")
        } else {
            return body.count
        }
    }
}

/** 是否存在 */
class BmobExistFunction<T> : Function<Result<BmobArray<T>>, Boolean> {
    override fun apply(t: Result<BmobArray<T>>): Boolean {
        val body = t.body()
        if (body == null) {
            throw IOException("Response body is null!")
        } else {
            return body.count > 0
        }
    }
}

/** 解析列表 */
class BmobArrayFunction<T> : Function<Result<BmobArray<T>>, MutableList<T>> {
    override fun apply(t: Result<BmobArray<T>>): MutableList<T> {
        val result = t.body()?.results
        if (result == null) {
            throw IOException("Response body is null!")
        } else {
            return result
        }
    }
}

/** 解析列表 */
class BmobArrayFirstFunction<T> : Function<Result<BmobArray<T>>, T> {
    override fun apply(t: Result<BmobArray<T>>): T {
        val result = t.body()?.results?.first()
        if (result == null) {
            throw IOException("Response body is null!")
        } else {
            return result
        }
    }
}