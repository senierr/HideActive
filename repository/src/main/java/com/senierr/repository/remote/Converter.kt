package com.senierr.repository.remote

import com.google.gson.Gson
import com.senierr.http.converter.Converter
import com.senierr.repository.bean.BmobArray
import com.senierr.repository.bean.BmobError
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 数据转换器
 *
 * @author zhouchunjie
 * @date 2018/8/30
 */
class ObjectConverter<T>(private val clazz: Class<T>) : Converter<T> {
    override fun convertResponse(p0: Response): T {
        val responseStr = p0.body()?.string()
        if (responseStr == null) {
            throw IOException("Response body is null!")
        } else {
            return Gson().fromJson(responseStr, clazz)
        }
    }
}

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

class BmobArrayConverter<T>(private val clazz: Class<T>) : Converter<BmobArray<T>> {
    override fun convertResponse(p0: Response): BmobArray<T> {
        val responseStr = p0.body()?.string()
        if (responseStr == null) {
            throw IOException("Response body is null!")
        } else {
            if (p0.code() >= 400) {
                throw Gson().fromJson(responseStr, BmobError::class.java)
            }
            val type = ParameterizedTypeImpl(BmobArray::class.java, arrayOf(clazz))
            return Gson().fromJson(responseStr, type)
        }
    }
}

class ParameterizedTypeImpl(
        private val raw: Class<*>,
        private val args: Array<Type> = emptyArray()
) : ParameterizedType {
    override fun getActualTypeArguments(): Array<Type> {
        return args
    }
    override fun getRawType(): Type {
        return raw
    }
    override fun getOwnerType(): Type? {
        return null
    }
}