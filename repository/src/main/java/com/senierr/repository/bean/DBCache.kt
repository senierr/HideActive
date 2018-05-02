package com.senierr.repository.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * 数据库缓存
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
@Entity(tableName = "DBCache")
data class DBCache(
        @PrimaryKey
        var cacheKey: String,
        var value: String? = null
)