package com.aliee.quei.mo.data.repository

import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.bean.AppUpdate
import com.aliee.quei.mo.data.bean.VersionInfoBean
import com.aliee.quei.mo.data.bean.dataConvert
import com.aliee.quei.mo.data.bean.toDataBean
import com.aliee.quei.mo.data.service.VersionService
import com.aliee.quei.mo.net.retrofit.RetrofitClient

class VersionRepository: BaseRepository() {
    private val service = RetrofitClient.createService(VersionService::class.java)

    suspend fun updateAppget(uid: Int, utemp: Int):UIDataBean<AppUpdate> {
        return try {
            service.updateAppgetK( uid, utemp).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }

    suspend fun updateAppop(opType: Int, uid: Int, utemp: Int):String? {
        return  service.updateAppopK(opType, uid, utemp).dataConvert()
    }

    suspend fun getVersionInfo(appid: String, version: String):UIDataBean<VersionInfoBean> {
        return try {
            service.getVersionInfoK(1, 1, appid, version).toDataBean()
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }
    }
}