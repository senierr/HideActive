package com.senierr.repository.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.senierr.http.converter.Converter
import com.senierr.repository.bean.BmobArray
import com.senierr.repository.bean.BmobError
import okhttp3.Response
import java.io.IOException

/**
 * Bmob数据转换器
 *
 * @author zhouchunjie
 * @date 2018/8/30
 */
class BmobObjectConverter<T>(private val clazz: Class<T>) : Converter<T> {
    override fun convertResponse(p0: Response): T {
        val responseStr = p0.body()?.string()
        if (responseStr == null) {
            throw IOException("Response body is null!")
        } else {
            if (p0.code() >= 400) {
                throw Gson().fromJson(responseStr, BmobError::class.java)
            }
            return Gson().fromJson(responseStr, clazz)
        }
    }
}

class BmobArrayConverter<T> : Converter<BmobArray<T>> {
    override fun convertResponse(p0: Response): BmobArray<T> {
        val responseStr = p0.body()?.string()
        if (responseStr == null) {
            throw IOException("Response body is null!")
        } else {
            if (p0.code() >= 400) {
                throw Gson().fromJson(responseStr, BmobError::class.java)
            }
            return Gson().fromJson(responseStr, object : TypeToken<BmobArray<T>>(){}.type)
        }
    }
}