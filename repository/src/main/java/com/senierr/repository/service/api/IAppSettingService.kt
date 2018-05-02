package com.senierr.repository.service.api

/**
 * 应用设置模块
 *
 * @author zhouchunjie
 * @date 2018/3/29
 */
interface IAppSettingService {

    /**
     * WIFI环境下自动更新
     */
    fun isAutoUpdateWallpaper(): Boolean

    /**
     * 设置WIFI环境下自动更新
     */
    fun setAutoUpdateWallpaper(isAuto: Boolean)

    /**
     * 自动删除90天前数据
     */
    fun isAutoClearCaches(): Boolean

    /**
     * 设置自动删除90天前数据
     */
    fun setAutoClearCaches(isAuto: Boolean)
}