package com.senierr.repository.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.senierr.repository.bean.User

/**
 * 用户DB接口
 *
 * 注：接口返回对象可能为空
 *
 * @author zhouchunjie
 * @date 2018/3/29
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM User WHERE objectId = :objectId")
    fun get(objectId: String): User?

    @Query("SELECT * FROM User")
    fun getList(): MutableList<User>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(user: User)

    @Query("DELETE FROM User WHERE objectId = :objectId")
    fun deleteById(objectId: String)

    @Query("DELETE FROM User")
    fun deleteAll()
}