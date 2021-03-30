package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.*

interface RecommendService {

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/recommend/lists")
    fun getRecommend(@Field("ids") ids: String): Observable<BaseResponse<TreeMap<String, RecommendPositionList>>>

    /**
     * @param sortField 排序字段订单转化率rate_order，rate_click收费章节
     * @param sort    排序方式，默认desc
     */
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/statiscartoon/minlist")
    fun getComicList(@Field("num") pageSize: Int, @Field("sortField") sortField: String, @Field("page") page: Int, @Field("sort") sort: Int = 1): Observable<BaseResponse<List<ComicBookBean>>>


    /**
     * 导流事件接口记录
     */
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/appupdateop")
    fun appUpdateOp(@Field("uid") uid: Int, @Field("utemp") utemp: Int, @Field("opType") opType: Int): Observable<BaseResponse<String>>

    /**
     * 导流事件是否弹出更新提示
     */
    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}cartoon/appdrainage")
    fun appDrainage(@Field("uid") uid: Int, @Field("utemp") utemp: Int): Observable<BaseResponse<String>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_VERSION}adzone/selectZoneIdByStatus?status=1")
    fun getadzone(@Field("status") status: Int): Observable<BaseResponse<List<AdZoneBean>>>

}