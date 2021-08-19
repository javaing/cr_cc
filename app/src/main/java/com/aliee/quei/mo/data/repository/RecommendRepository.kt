package com.aliee.quei.mo.data.repository

import android.util.Log
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.service.RecommendService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import java.util.*

class RecommendRepository : BaseRepository() {
    private val service = RetrofitClient.createService(RecommendService::class.java)

    /**
     * 通过推荐位ID获取推荐书籍
     */
    suspend fun getRecommend(id:String): UIDataBean<MutableList<RecommendBookBean>>? {
        Log.e("TAG", "RecommendRepository getRecommend ")
        return try {
            service.getRecommendK(id).data?.getByRid(id)
        } catch (e:Exception){
            UIDataBean(Status.Error)
        }

    }

    /**
     * 获取首页推荐位的数据
     */
    suspend fun getRecommendBatch(
            ids: String
    ): UIDataBean<MutableList<RecommendListBean>> {
        return try {
            val treemap = service.getRecommendK(ids).data as TreeMap<String, RecommendPositionList>
            val result = treemap.keys.map { key ->
                val rp = BeanConstants.RecommendPosition.getByRid(key) //
                val list: MutableList<RecommendBookBean>? = treemap[key]?.list
                list?.forEach {
                    it.rid = rp?.rid
                }
                RecommendListBean(key, rp?.title ?: "", list)
            }
            result as MutableList<RecommendListBean>
            UIDataBean(Status.Success, result)
        } catch (e: Exception) {
            UIDataBean(Status.Error)
        }

    }

    suspend fun getListByConversionRate(
            page: Int,
            pageSize: Int,
            sortField: String = "rate_order"
    ): UIDataBean<ListBean<RecommendBookBean>> {
        try {
            val it = service.getComicList(pageSize, sortField, page).data
            val result = ListBean<RecommendBookBean>()
            result.list = it?.map {
                RecommendBookBean(
                    it.author,
                    it.thumb,
                    it.id,
                    it.description,
                    it.status,
                    it.thumbX,
                    it.title,
                    it.typename
                )
            }
            return  UIDataBean(Status.Success, result)
        } catch (e:Exception) {
            return UIDataBean(Status.Error)
        }
    }

    suspend fun appUpdateOp(uid: Int, utemp: Int, opType: Int):UIDataBean<String> {
        return try {
            service.appUpdateOp(uid, utemp, opType).toDataBean()
        } catch (e:Exception){
            UIDataBean(Status.Error)
        }
    }


    suspend fun appDrainage(uid: Int, utemp: Int):UIDataBean<String> {
        return try {
            service.appDrainage(uid, utemp).toDataBean()
        } catch (e:Exception){
            UIDataBean(Status.Error)
        }
    }

    suspend fun getAdZone(status: Int):UIDataBean<List<AdZoneBean>> {
        return try {
            service.getadzone(status).toDataBean()
        } catch (e:Exception){
            UIDataBean(Status.Error)
        }
    }

}