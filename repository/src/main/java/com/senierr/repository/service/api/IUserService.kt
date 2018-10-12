package com.senierr.repository.service.api

import com.senierr.repository.bean.BmobInsert
import com.senierr.repository.bean.BmobUpdate
import com.senierr.repository.bean.User
import io.reactivex.Observable

/**
 * 用户数据接口
 *
 * @author zhouchunjie
 * @date 2018/4/10
 */
interface IUserService {

    /**
     * 检查账号是否存在
     */
    fun checkAccountExist(account: String): Observable<Boolean>

    /**
     * 注册
     */
    fun register(account: String, password: String): Observable<BmobInsert>

    /**
     * 登录
     */
    fun login(account: String, password: String): Observable<User>

    /**
     * 登出
     */
    fun logout(userId: String): Observable<BmobUpdate>

    /**
     * 获取本地缓存用户
     *
     * 注：用户登录状态校验，实际项目会存储Token
     */
    fun getCacheUser(): Observable<User>

    /**
     * 获取远程用户信息
     */
    fun getRemoteUser(objectId: String): Observable<User>

    /**
     * 是否已登录
     */
    fun isLoggedIn(): Observable<Boolean>

    /**
     * 获取所有用户
     */
    fun getOtherUsers(userId: String): Observable<MutableList<User>>

    /**
     * 获取备注
     */
    fun getRemark(userId: String): String

    /**
     * 设置备注
     */
    fun setRemark(userId: String, remark: String)
}