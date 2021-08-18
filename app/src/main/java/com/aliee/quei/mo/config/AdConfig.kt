package com.aliee.quei.mo.config

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.bean.AdBean
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.Option
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

object AdConfig {

    //banner 广告默认id，
    const val BANNER_DEF_ID: Int = -100
    const val GROUP2_CACHE_EXPIRATION = 1000*60*10L ////超過10分鐘清空cache

    fun getAd(zid: Int): AdBean? {
        val adList: MutableList<AdBean>?
        try {
            adList = CommonDataProvider.instance.getAdList()
            Log.e("tag", "getAD($zid)")
            if (adList == null || adList.size == 0) {
                return null
            }
            return adList.first {
                it.id == zid
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 广告点击
     */
    fun adClick(context: Context, url: String) {
        if (url.isEmpty()) {
            return
        }
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    /**
     * 获取广告物料
     */
    fun getAdInfo(adBean: AdBean, successCall: (AdInfo) -> Unit, failed: () -> Unit) {
        //Log.d("tag", "getAdInfo:$adBean")
        //广告拦截
        if (!interceptorAd(adBean)) {
            failed.invoke()
            return
        }
        val request = Request.Builder().url(adBean.apiUrl).get().build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                failed.invoke()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val resp = response.body?.string()
                    val adInfo = Gson().fromJson<AdInfo>(resp, AdInfo::class.java)
                    //val option = Gson().fromJson<Option>(adInfo.optionstr, Option::class.java)
                    //Log.d("tag", "adInfo --> id${adBean.zid},resp :${resp}，adInfo:$adInfo")
                    if (adInfo != null) {
                        if (adInfo.imgurl != null && adInfo.callbackurl != null && adInfo.clickurl != null) {
                            successCall.invoke(adInfo)
                        } else {
                            failed.invoke()
                        }
                    }
                } else {
                    failed.invoke()
                }
            }
        })
    }

    /**
     * 广告预览
     */
    fun adPreview(url: String) {
        if (url.isEmpty()) {
            return
        }
        val request = Request.Builder().url(url).get().build()
        OkHttpClient()
                .newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            Log.d("tag", "广告曝光成功")
                        }
                    }
                })
    }

    /**
     * 广告是否开启
     */
    fun isAdShow(adBean: AdBean): Boolean {
        return adBean.status != 0
    }

    /**
     * 广告是否可关闭
     */
    fun isClosed(close: Int): Boolean {
        return close != 0
    }


    /**
     * 拦截请求
     */
    fun interceptorAd(adBean: AdBean): Boolean {
        Log.e("tag", "interceptorAd adinfo :$adBean")
        val userInfo = CommonDataProvider.instance.getUserInfo()
        Log.e("tag", " userInfo :${userInfo.toString()}")
        val isTempUser = CommonDataProvider.instance.getTempUser()
        Log.d("tag", "interceptorAd: itTempUser:$isTempUser")
        val isVip = userInfo?.isVip ?: 0
        Log.d("tag", "interceptorAd: isVip:$isVip")
        val isRecharge = userInfo?.isRecharge ?: 0
        Log.d("tag", "interceptorAd: isRecharge:$isRecharge")
        /*  return if (adBean.status == 0) {
              false
          } else if ((adBean.user == 1 && isTempUser ==0) || (adBean.user == 2 && isTempUser==1)) {
              Log.d("interceptorAd", "ad id:${adBean.zid} - user false")
              false
          } else if ((adBean.vip == 1 && isVip==0) || (adBean.vip == 2 && isVip==1)) {
              Log.d("interceptorAd", "ad id:${adBean.zid} - vip false")
              false
          } else if ((adBean.recharge == 1 && isRecharge == 0) || (adBean.recharge == 2 && isRecharge == 1)) {
              Log.d("interceptorAd", "ad id:${adBean.zid} - recharge false")
              false
          } else
              true*/
        return if (adBean.status == 0) {
            false
        } else if ((adBean.user == 1 && isTempUser == 0) || adBean.user == 2 && isTempUser == 1) {
            Log.d("interceptorAd", "ad id:${adBean.zid} - user false")
            false
        } else if ((adBean.vip == 1 && isVip == 1) || (adBean.vip == 2 && isVip == 0)) {
            Log.d("interceptorAd", "ad id:${adBean.zid} - vip false")
            false
        } else if ((adBean.recharge == 1 && isRecharge == 1 || adBean.recharge == 2 && isRecharge == 0)) {
            Log.d("interceptorAd", "ad id:${adBean.zid} - recharge false")
            false
        } else {
            Log.d("interceptorAd", "ad id:${adBean.zid} - true")
            true
        }
    }
}