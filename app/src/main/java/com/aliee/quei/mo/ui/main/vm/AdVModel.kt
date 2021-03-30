package com.aliee.quei.mo.ui.main.vm

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import android.util.Log
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.bean.AdBean
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.Option
import com.aliee.quei.mo.data.repository.AdRepository
import com.aliee.quei.mo.data.service.AdService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.google.gson.Gson
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import okhttp3.*
import java.io.IOException


class AdVModel : BaseViewModel() {
    private val adRepository = AdRepository()

    val adListLiveData = MediatorLiveData<UIDataBean<MutableList<AdBean>>>()
    val adList1LiveData = MediatorLiveData<UIDataBean<MutableList<AdBean>>>()
    val adInfoLiveData = MediatorLiveData<UIDataBean<Any>>()

    fun getAdList(lifecycleOwner: LifecycleOwner, groupId: Int) {
        adRepository.getAdList(lifecycleOwner, groupId)
                .subscribe(StatusResourceObserver(if (groupId == 2) adListLiveData else adList1LiveData))
    }

    @SuppressLint("CheckResult")
    fun getAdList(lifecycleOwner: LifecycleOwner, successCall: (MutableList<AdBean>) -> Unit, failed: () -> Unit) {
        val preTasks = mutableListOf<Observable<MutableList<AdBean>>>()
        val adMap = mutableMapOf<String, MutableList<AdBean>>()
        val group1 = adRepository.getAdList(lifecycleOwner, 1)
        val group2 = adRepository.getAdList(lifecycleOwner, 2)

        preTasks.add(group1)
        preTasks.add(group2)

        group1.subscribe({
            Log.d("tag", "multipleAdApi group1:${it.toString()}")
            adMap["group1"] = it
            CommonDataProvider.instance.saveAdList(Gson().toJson(it))
        }, {}, {})
        group2.subscribe({
            Log.d("tag", "multipleAdApi group2:${it.toString()}")
            adMap["group2"] = it
        }, {}, {})

         Observable.concat(preTasks)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .subscribe({}, {
                    Log.d("tag", "获取group1的广告列表失败")
                    CommonDataProvider.instance.saveAdList(Gson().toJson(adMap["group1"]))

                    failed.invoke()
                }, {
                   Log.d("tag", "获取group1的广告列表完成")
                    val group1AdList = adMap["group1"]
                    CommonDataProvider.instance.saveAdList(Gson().toJson(group1AdList))
                    val group2AdList = adMap["group2"]!!
                    successCall.invoke(group2AdList)
                })
    }


    /**
     *  开屏广告调取
     */
    @SuppressLint("CheckResult")
    fun multipleLaunchAd(lifecycleOwner: LifecycleOwner, adBeans: MutableList<AdBean>,
                         successCall: (MutableMap<Int, AdInfo?>) -> Unit,
                         failed: () -> Unit) {
        val adMap = mutableMapOf<Int, AdInfo?>()
        val secsMap = mutableMapOf<Int, Int>()
        val preTasks = mutableListOf<Observable<AdInfo>>()
        var ind = 0
        adBeans.forEach {
            if (AdConfig.interceptorAd(it)) {
                ind++
                secsMap[ind] = it.sec
                preTasks.add(adRepository.getAdInfo(lifecycleOwner, it.apiUrl))
            }
        }
        var index = 0
        Observable.concat(preTasks)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .subscribe({
                    index++
                    val adInfo = it
                    if (adInfo.callbackurl != null && adInfo.clickurl != null && adInfo.imgurl != null) {
                        Log.d("tag", "multipleLaunchAdApi concat:next:${it.toString()}")
                        adInfo?.sec = secsMap[index]
                        adMap[index] = adInfo
                    }
                }, {
                    Log.d("tag", "multipleLaunchAdApi concat:failed：${it.localizedMessage}")
                    failed.invoke()
                }, {
                    Log.d("tag", "multipleLaunchAdApi concat:over:${adMap.toString()}")
                    successCall.invoke(adMap)
                })
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
        if (AdConfig.interceptorAd(bannerAd)) {
            val bannerObs = adRepository.getAdInfo(lifecycleOwner, bannerAd.apiUrl)
            preTasks.add(bannerObs)
            bannerObs.subscribe({
                Log.d("tag", "multipleAdApi bannerObs:${it.toString()}")
                if (it.clickurl != null && it.callbackurl != null && it.imgurl != null) {
                    adMap["bannerAd"] = it
                }
            }, {}, {})
        }
        if (AdConfig.interceptorAd(flow90Ad)) {
            val flowObs90 = adRepository.getAdInfo(lifecycleOwner, flow90Ad.apiUrl)
            preTasks.add(flowObs90)
            flowObs90.subscribe({
                Log.d("tag", "multipleAdApi flowObs90:${it.toString()}")
                if (it.clickurl != null && it.callbackurl != null && it.imgurl != null) {
                    adMap["flowObs90"] = it
                }
            }, {}, {})
        }
        if (AdConfig.interceptorAd(flowQuAd)) {
            val flowObsQu = adRepository.getAdInfo(lifecycleOwner, flowQuAd.apiUrl)
            preTasks.add(flowObsQu)
            flowObsQu.subscribe({
                Log.d("tag", "multipleAdApi flowObsQu:${it.toString()}")
                if (it.clickurl != null && it.callbackurl != null && it.imgurl != null) {
                    val option3 = Gson().fromJson<Option>(it.optionstr, Option::class.java)
                    it.title = option3.title
                    it.desc = option3.desc
                    adMap["flowObsQu"] = it
                }
            }, {}, {})
        }
        if (AdConfig.interceptorAd(flowQiangAd)) {
            val flowObsQiang = adRepository.getAdInfo(lifecycleOwner, flowQiangAd.apiUrl)
            preTasks.add(flowObsQiang)
            flowObsQiang.subscribe({
                Log.d("tag", "multipleAdApi flowObsQiang:${it.toString()}")
                if (it.clickurl != null && it.callbackurl != null && it.imgurl != null) {
                    val option4 = Gson().fromJson<Option>(it.optionstr, Option::class.java)
                    it.title = option4.title
                    it.desc = option4.desc
                    adMap["flowObsQiang"] = it
                }
            }, {}, {})
        }
        Observable.concat(preTasks)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .subscribe({}, {
                    Log.d("tag", "multipleAdApi concat:failed")
                    failed.invoke()
                }, {
                    Log.d("tag", "multipleAdApi concat:over")
                    successCall.invoke(adMap)
                })

        /* Observable.zip(bannerObs, flowObs90, flowObsQu, flowObsQiang, Function4<AdInfo, AdInfo, AdInfo, AdInfo, MutableMap<String, AdInfo>> { t1, t2, t3, t4 ->
             val map = mutableMapOf<String, AdInfo>()
             Log.d("tag", "ad qu t3:${t3.toString()}")
             map["bannerAd"] = t1
             map["flowObs90"] = t2
             val option3 = Gson().fromJson<Option>(t3.optionstr, Option::class.java)
             t3.title = option3.title
             t3.desc = option3.desc
             map["flowObsQu"] = t3
             val option4 = Gson().fromJson<Option>(t4.optionstr, Option::class.java)
             t4.title = option4.title
             t4.desc = option4.desc
             map["flowObsQiang"] = t4
             map
         }).compose(SchedulersUtil.applySchedulers())
                 .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                 .subscribe({
                     Log.d("tag", "adInfo multipleAdApi throwable:ok")
                     successCall.invoke(it)
                 }, {
                     failed.invoke()
                     Log.d("tag", "adInfo multipleAdApi throwable:${it.localizedMessage}")
                 }, {})*/

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
                            val resp = response.body()!!.string()
                            Log.d("tag", "adInfo --> id${adBean.zid},resp :${resp}")
                            val adInfo = Gson().fromJson<AdInfo>(resp, AdInfo::class.java)
                            if (adInfo != null) {
                                successCall.invoke(adInfo)
                            }
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