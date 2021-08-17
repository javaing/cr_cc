package com.aliee.quei.mo.ui.main.vm

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.bean.AdBean
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.Option
import com.aliee.quei.mo.data.bean.checkLink
import com.aliee.quei.mo.data.repository.AdRepository
import com.google.gson.Gson
import io.reactivex.Observable
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException


class AdVModel : BaseViewModel() {
    private val adRepository = AdRepository()

    val adListLiveData = MediatorLiveData<UIDataBean<MutableList<AdBean>>>()
    val adList1LiveData = MediatorLiveData<UIDataBean<MutableList<AdBean>>>()

    fun getAdList(groupId: Int) {
        viewModelScope.launch {
            val data = adRepository.getAdList(groupId)
            when(groupId) {
                2-> adListLiveData.value = data
                else -> adList1LiveData.value = data
            }
        }
    }

    fun getAdListK(successCall: (MutableList<AdBean>) -> Unit, failed: () -> Unit) {

        viewModelScope.launch {
            try {
                var group2 = mutableListOf<AdBean>()
                listOf(
                    launch {
                        val group1 = adRepository.getAdList(1).data
                        if(group1!=null) {
                            CommonDataProvider.instance.saveAdList(Gson().toJson(group1))
                            Log.d("tag", "获取group1的广告完成")
                        }
                           },
                    launch {
                        group2 = adRepository.getAdList(2).data?: mutableListOf()
                        if(group2.size>0)
                            CommonDataProvider.instance.saveFullscreenAdList(Gson().toJson(group2))
                        Log.d("tag", "获取group2的:$group2")
                    }
                ).joinAll()
                successCall.invoke(group2)
            } catch (e: Throwable) {
                val cache = CommonDataProvider.instance.getFullscreenAdList()
                if(cache!=null) {
                    Log.d("tag", "获取group2的广告cache")
                    successCall.invoke(cache)
                }
                failed.invoke()
            }
        }


    }

    /**
     *  开屏广告调取
     */
    fun multipleLaunchAd(adBeans: MutableList<AdBean>,
                         successCall: (MutableMap<Int, AdInfo?>) -> Unit,
                         failed: (String) -> Unit) {
        val adMap = mutableMapOf<Int, AdInfo?>()
        var outputInd = 0
        viewModelScope.launch {
            try {
                launch {
                    adBeans.forEach {
                        if (AdConfig.interceptorAd(it)) {
                            Log.d("tag", "multipleLaunchAdApi adId=${it.id}")
                            val adInfo = adRepository.getAdInfo(it.apiUrl).data
                            Log.d("tag", "multipleLaunchAdApi adId=${it.id}:$adInfo")
                            if (adInfo?.checkLink() == true) {
                                adInfo.sec =  it.sec
                                adMap[outputInd] = adInfo
                                outputInd++
                            }
                        }
                    }
                }.join()
                successCall.invoke(adMap)
            } catch (e: Exception) {
                e.printStackTrace()
                failed.invoke(e.localizedMessage)
            }
        }
    }

    /**
     * 漫画首页广告
     */
    @SuppressLint("CheckResult")
    fun multipleAdApi(lifecycleOwner: LifecycleOwner,
                      bannerAd: AdBean,
                      flow90Ad: AdBean,
                      flowQuAd: AdBean,
                      flowQiangAd: AdBean,
                      successCall: (MutableMap<String, AdInfo>) -> Unit,
                      failed: () -> Unit) {
        val adMap = mutableMapOf<String, AdInfo>()
        val preTasks = mutableListOf<Observable<AdInfo>>()
        viewModelScope.launch {
            try {
                listOf(
                    launch {
                        if (AdConfig.interceptorAd(bannerAd)) {
                            val it = adRepository.getAdInfo(bannerAd.apiUrl).data
                            Log.d("tag", "multipleAdApi bannerObs:$it")
                            if (it?.checkLink() == true) {
                                adMap["bannerAd"] = it
                            }
                        }
                    },
                    launch {
                        if (AdConfig.interceptorAd(flow90Ad)) {
                            val it = adRepository.getAdInfo(flow90Ad.apiUrl).data
                            Log.d("tag", "multipleAdApi flowObs90:$it")
                            if (it?.checkLink() == true) {
                                adMap["flowObs90"] = it
                            }
                        }
                    },
                    launch {
                        if (AdConfig.interceptorAd(flowQuAd)) {
                            val it = adRepository.getAdInfo(flowQuAd.apiUrl).data
                            Log.d("tag", "multipleAdApi flowObsQu:$it")
                            if (it?.checkLink() == true) {
                                val option3 = Gson().fromJson<Option>(it.optionstr, Option::class.java)
                                it.title = option3.title
                                it.desc = option3.desc
                                adMap["flowObsQu"] = it
                            }
                        }
                    },
                    launch {
                        if (AdConfig.interceptorAd(flowQiangAd)) {
                            val it = adRepository.getAdInfo(flowQiangAd.apiUrl).data
                            Log.d("tag", "multipleAdApi flowObsQiang:$it")
                            if (it?.checkLink() == true) {
                                val option4 = Gson().fromJson<Option>(it.optionstr, Option::class.java)
                                it.title = option4.title
                                it.desc = option4.desc
                                adMap["flowObsQiang"] = it
                            }
                        }
                    }
                ).joinAll()
                Log.d("tag", "multipleAdApi joinall:over")
                successCall.invoke(adMap)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("tag", "multipleAdApi joinall:failed")
                failed.invoke()
            }
        }

    }


    /**
     * 广告详情
     */
    fun getRotation(adBean: AdBean, successCall: (AdInfo) -> Unit, failed: () -> Unit) {
        Log.d("tag", "adInfo id:${adBean.zid},${adBean.apiUrl}")
        val request = Request.Builder().url(adBean.apiUrl).get().build()
        OkHttpClient()
                .newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        failed.invoke()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            val resp = response.body?.string()
                            Log.d("tag", "adInfo --> id${adBean.zid},resp :${resp}")
                            val adInfo = Gson().fromJson<AdInfo>(resp, AdInfo::class.java)
                            if (adInfo != null) {
                                successCall.invoke(adInfo)
                            }
                        } else {
                            failed.invoke()
                        }
                    }
                })
    }

    fun adPreview(url: String) {
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


}