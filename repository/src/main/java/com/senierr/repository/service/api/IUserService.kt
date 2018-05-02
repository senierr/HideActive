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
     * 注册
     *
     * @param username 账号
     * @param password 密码
     * @return 用户ID: objectId
     */
    fun register(username: String, password: String): Single<String>

    /**
     * 登录
     *
     * @param username 账号
     * @param password 密码
     * @return 用户
     */
    fun login(username: String, password: String): Single<User>
}