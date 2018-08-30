package com.senierr.repository.service.api

import com.senierr.repository.bean.User
import io.reactivex.Single

/**
 * 用户数据接口
 *
 * @author zhouchunjie
 * @date 2018/4/10
 */
interface IUserService {

    /**
     * 检查账号是否重复
     *
     * @param account 账号
     */
    fun checkAccountIfRepeat(account: String): Single<Boolean>

    /**
     * 检查昵称是否重复
     *
     * @param nickname 昵称
     */
    fun checkNicknameIfRepeat(nickname: String): Single<Boolean>

    /**
     * 注册
     *
     * @param account 账号
     * @param password 密码
     * @return 用户ID: objectId
     */
    fun register(account: String, password: String): Single<String>

    /**
     * 登录
     *
     * @param account 账号
     * @param password 密码
     * @return 用户
     */
    fun login(account: String, password: String): Single<User>

    /**
     * 更新用户信息
     *
     * @param user 昵称
     */
    fun updateUserInfo(user: User): Single<Boolean>
}