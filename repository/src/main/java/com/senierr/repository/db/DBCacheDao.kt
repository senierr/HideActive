package com.senierr.repository.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.senierr.repository.bean.DBCache

/**
 * 帖子DB接口
 *
 * @author zhouchunjie
 * @date 2018/3/29
 */
@Dao
interface DBCacheDao {

    @Query("SELECT * FROM DBCache WHERE cacheKey = :cacheKey")
    fun get(cacheKey: String): DBCache

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(dbCache: DBCache)

    @Query("DELETE FROM DBCache WHERE cacheKey = :cacheKey")
    fun deleteByKey(cacheKey: String)

    @Query("DELETE FROM DBCache")
    fun deleteAll()
}