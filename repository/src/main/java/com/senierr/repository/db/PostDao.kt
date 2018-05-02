package com.senierr.repository.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.senierr.repository.bean.Post

/**
 * 帖子DB接口
 *
 * @author zhouchunjie
 * @date 2018/3/29
 */
@Dao
interface PostDao {

    @Query("SELECT * FROM Post WHERE objectId = :objectId")
    fun get(objectId: String): Post

    @Query("SELECT * FROM Post")
    fun getList(): MutableList<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(list: MutableList<Post>)

    @Query("DELETE FROM Post WHERE objectId = :objectId")
    fun deleteById(objectId: String)

    @Query("DELETE FROM Post")
    fun deleteAll()
}