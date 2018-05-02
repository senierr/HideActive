package com.senierr.repository.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.senierr.repository.bean.DBCache
import com.senierr.repository.bean.User

/**
 * 数据库入口
 *
 * @author zhouchunjie
 * @date 2018/3/13
 */
@Database(entities = [DBCache::class, User::class],
        version = AppDatabase.DB_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DB_VERSION = 1    // 数据库版本
    }

    abstract fun getDBCacheDao(): DBCacheDao
    abstract fun getUserDao(): UserDao
}