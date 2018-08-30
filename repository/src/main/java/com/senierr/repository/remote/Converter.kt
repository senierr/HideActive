package com.senierr.repository.remote

import com.google.gson.Gson
import com.senierr.http.converter.Converter
import com.senierr.repository.bean.BmobError
import okhttp3.Response
import java.io.IOException

/**
 * Bmob数据转换器
 *
 * @author zhouchunjie
 * @date 2018/8/30
 */
class BmobConverter : Converter<String> {

    override fun convertResponse(p0: Response): String {
        val responseStr = p0.body()?.string()
        if (responseStr == null) {
            throw IOException("Response body is null!")
        } else {
            if (p0.code() >= 400) {
                throw Gson().fromJson(responseStr, BmobError::class.java)
            }
            return responseStr
        }
    }
}