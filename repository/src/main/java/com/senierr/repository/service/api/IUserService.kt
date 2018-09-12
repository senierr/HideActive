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
     * 检查账号是否重复
     */
    fun checkAccountIfRepeat(account: String): Observable<Boolean>

    /**
     * 检查昵称是否重复
     */
    fun checkNicknameIfRepeat(nickname: String): Observable<Boolean>

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
     * 更新用户头像
     */
    fun updateUserPortrait(objectId: String, portrait: String): Observable<BmobUpdate>

    /**
     * 更新用户昵称
     */
    fun updateUserNickname(objectId: String, nickname: String): Observable<BmobUpdate>

    /**
     * 获取本地用户信息
     */
    fun getLocalUser(): Observable<User>

    /**
     * 获取远程用户信息
     */
    fun getRemoteUser(objectId: String): Observable<User>

    /**
     * 是否已登录
     */
    fun isLoggedIn(): Observable<Boolean>

    /**
     * 获取好友
     */
    fun getFriends(userId: String): Observable<MutableList<User>>
}