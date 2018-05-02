package com.senierr.repository.service.impl

import com.senierr.repository.Repository
import com.senierr.repository.service.api.IAppSettingService

/**
 * @author zhouchunjie
 * @date 2018/4/8
 */
class AppSettingService : IAppSettingService {

    override fun isAutoUpdateWallpaper(): Boolean {
        return Repository.sp
                .getBoolean("", true)
    }

    override fun setAutoUpdateWallpaper(isAuto: Boolean) {
        Repository.sp
                .edit()
                .putBoolean("", isAuto)
                .apply()
    }

    override fun isAutoClearCaches(): Boolean {
        return Repository.sp
                .getBoolean("", true)
    }

    override fun setAutoClearCaches(isAuto: Boolean) {
        Repository.sp
                .edit()
                .putBoolean("", isAuto)
                .apply()
    }
}