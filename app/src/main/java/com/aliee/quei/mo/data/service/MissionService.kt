package com.aliee.quei.mo.data.service

import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.bean.CoinsAmountBean
import io.reactivex.Observable
import retrofit2.http.POST

interface MissionService {
    @POST("activity/receive")
    fun dailyLogin() : Observable<BaseResponse<CoinsAmountBean>>
}