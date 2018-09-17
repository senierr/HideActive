package com.senierr.repository.util

import java.text.SimpleDateFormat
import java.util.*

/**********************************************************
 * @文件作者：zhouchunjie
 * @创建时间：2018/9/17 9:32
 * @文件描述：
 * @修改历史：2018/9/17 创建初始版本
 **********************************************************/
object DateUtil {

    fun format(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(time)
    }
}