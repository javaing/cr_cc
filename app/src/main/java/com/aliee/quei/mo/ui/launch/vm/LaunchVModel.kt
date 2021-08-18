package com.aliee.quei.mo.ui.launch.vm

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.GsonProvider
import com.aliee.quei.mo.data.Channel
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.data.repository.*
import com.aliee.quei.mo.data.service.*
import com.aliee.quei.mo.net.ApiConstants
import com.aliee.quei.mo.utils.AES
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.SharedPreUtils.Key_ApiDomain
import com.aliee.quei.mo.utils.SharedPreUtils.Key_CSRoute
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.elvishew.xlog.BuildConfig
import com.google.gson.reflect.TypeToken
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException


/**
 * Created by Administrator on 2018/4/28 0028.
 */
class LaunchVModel : BaseViewModel() {
    private var versionRepository= VersionRepository()
    private var repository= LaunchRepository()
    private var recommendRepository= RecommendRepository()
    private var categoryRepository= CategoryRepository()
    private var userInfoRepository= UserInfoRepository()

    val prepareLiveData = MediatorLiveData<UIDataBean<Any>>()
    val getUserInfoLiveData = MediatorLiveData<UIDataBean<UserInfoBean>>()
    val appupdateopLiveData = MediatorLiveData<UIDataBean<String>>()
    val registerTokenLiveData = MediatorLiveData<UIDataBean<Any>>()


    private var backup_oss: List<String>? = null


    fun appUpdateOp(uid: Int, utemp: Int, opType: Int) {
        viewModelScope.launch {
            appupdateopLiveData.value = recommendRepository.appUpdateOp(uid, utemp, opType)
        }
    }

    fun updateAppop(opType: Int, uid: Int, utemp: Int) {
        viewModelScope.launch {
                val data = versionRepository.updateAppop(opType, uid, utemp)
                Log.e("tag", "LaunchVModel updateAppop $data")
        }
    }




    /**
     * 获取可用的API域名
     * @param successCall 获取域名成功的回调
     * @param failed 获取域名失败的回调
     */
    fun getApiDomain(successCall: () -> Unit, failed: () -> Unit) {
        //测试环境 使用测试域名
        /* if (true) {
              ApiConstants.API_HOST = "http://47.52.26.64:8080/"
            // ApiConstants.API_HOST = "http://api.ti1z6.com"
             SharedPreUtils.getInstance().putString("apiDomain", ApiConstants.API_HOST)
             successCall.invoke()
             return
         }*/
        //在正式环境下，先获取API域名
        val request = Request.Builder()
                .url(ApiConstants.OSS_PATH)
                .get()
                .build()
        OkHttpClient()
                .newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("tag","00000000000000 onFailure：${e.message}")
                        failed.invoke()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            val type = object : TypeToken<List<DomainBean>>() {}.type
                            val domainList = GsonProvider.gson.fromJson<List<DomainBean>>(response.body?.charStream(), type)
                            if (domainList.isEmpty()) {
                                failed.invoke()
                                return
                            }

                            for (domainBean in domainList) {
                                if (domainBean.enable == 1) {
                                    //   val api = String(mAes.decrypt(domainBean.domain.toByteArray(), ApiConstants.fdsfsdfsdf.toByteArray()))
                                    ApiConstants.API_HOST = domainBean.domain
                                    ApiConstants.CS_ROUTE = domainBean.cs_route
                                    ApiConstants.HOTFIX = domainBean.hotfix
                                    backup_oss = domainList[0].backup_oss as List<String>
                                    SharedPreUtils.getInstance().putInt("backupNums", backup_oss!!.size)
                                    Log.d("LaunchVModel", "Backup_oss:" + backup_oss.toString())
                                    for (i in backup_oss!!.indices) {
                                        SharedPreUtils.getInstance().putString("backup_$i",backup_oss!![i])
                                    }

                                }
                            }
                            if (ApiConstants.API_HOST.isNotEmpty() || ApiConstants.API_HOST.isNotBlank()) {
                                SharedPreUtils.getInstance().putString(Key_ApiDomain, ApiConstants.API_HOST)
                                SharedPreUtils.getInstance().putString(Key_CSRoute, ApiConstants.CS_ROUTE)
                                SharedPreUtils.getInstance().putInt("hotfix", ApiConstants.HOTFIX)
                                successCall.invoke()
                            } else {
                                failed.invoke()
                            }
                            return
                        }
                        failed.invoke()
                    }
                })
    }

    /**
     * 获取VIDEO可用的API域名
     * @param successCall 获取域名成功的回调
     * @param failed 获取域名失败的回调
     */
    fun getVApiDomain(successCall: () -> Unit, failed: () -> Unit) {
        //在正式环境下，先获取API域名
        val request = Request.Builder()
                .url(ApiConstants.VOSS_PATH)
                .get()
                .build()
        OkHttpClient()
                .newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("tag","00000000000000 onFailure：${e.message}")
                        failed.invoke()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            val type = object : TypeToken<List<DomainBean>>() {}.type
                            val domainList: List<DomainBean>?
                            try {
                                domainList = GsonProvider.gson.fromJson<List<DomainBean>>(response.body?.charStream(), type)
                                if (domainList.isEmpty()) {
                                    failed.invoke()
                                    return
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                failed.invoke()
                                return
                            }

                            for (domainBean in domainList) {
                                if (domainBean.enable == 1) {
                                    Log.d("tag", "VIDEO域名获取成功")
                                    ApiConstants.VIDEO_API_PATH = domainBean.domain
                                }
                            }
                            successCall.invoke()
                            return
                        }
                        failed.invoke()
                    }
                })
    }

    fun reGetV(successCall: () -> Unit, failed: () -> Unit, url: String) {

        val request = Request.Builder()
                .url(url)
                .get()
                .build()
        OkHttpClient()
                .newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("tag", "域名reGetV onFailure失敗")
                        failed.invoke()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            val type = object : TypeToken<List<DomainBean>>() {}.type

                            try {
                                val domainList =
                                        GsonProvider.gson.fromJson<List<DomainBean>>(response.body?.charStream(), type)
                                if (domainList.isEmpty()) {
                                    failed.invoke()
                                    return
                                }

                                for (domainBean in domainList) {
                                    if (domainBean.enable == 1) {
                                        ApiConstants.VIDEO_API_PATH = domainBean.domain
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                failed.invoke()
                                return
                            }
                            successCall.invoke()
                            return
                        }
                        Log.d("tag", "域名reGetV onResponse失敗")
                        failed.invoke()
                    }
                })

    }

    fun reGet(successCall: () -> Unit, failed: () -> Unit, url: String) {

        val request = Request.Builder()
                .url(url)
                .get()
                .build()
        OkHttpClient()
                .newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("tag", "域名reGet onFailure失敗")
                        failed.invoke()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            val type = object : TypeToken<List<DomainBean>>() {}.type

                            val domainList =
                                    GsonProvider.gson.fromJson<List<DomainBean>>(response.body?.charStream(), type)
                            if (domainList.isEmpty()) {
                                failed.invoke()
                                return
                            }

                            val mAes = AES()
                            for (domainBean in domainList) {
                                if (domainBean.enable == 1) {
                                    //   val api = String(mAes.decrypt(domainBean.domain.toByteArray(), ApiConstants.fdsfsdfsdf.toByteArray()))
                                    ApiConstants.API_HOST = domainBean.domain
                                    ApiConstants.CS_ROUTE = domainBean.cs_route
                                    ApiConstants.HOTFIX = domainBean.hotfix
                                    backup_oss = domainList[0].backup_oss as List<String>
                                    SharedPreUtils.getInstance().putInt("backupNums", backup_oss!!.size)
                                    Log.d("LaunchVModel", "Backup_oss:" + backup_oss.toString())
                                    for (i in 0 until backup_oss!!.size) {
                                        SharedPreUtils.getInstance().putString("backup_" + i, backup_oss!!.get(i))
                                    }

                                }
                            }
                            if (ApiConstants.API_HOST.isNotEmpty() || ApiConstants.API_HOST.isNotBlank()) {
                                SharedPreUtils.getInstance().putString(Key_ApiDomain, ApiConstants.API_HOST)
                                SharedPreUtils.getInstance().putString(Key_CSRoute, ApiConstants.CS_ROUTE)
                                SharedPreUtils.getInstance().putInt("hotfix", ApiConstants.HOTFIX)
                                successCall.invoke()
                            } else {
                                failed.invoke()
                            }
                            return
                        }
                        Log.d("tag", "域名reGet onResponse失敗")
                        failed.invoke()
                    }
                })

    }


    @SuppressLint("CheckResult")
    fun doLaunch(lifecycleOwner: LifecycleOwner, success: () -> Unit) {

        preAll(lifecycleOwner) {
            prepareLiveData.value = UIDataBean(Status.Success)
            var openAppTimes = SharedPreUtils.getInstance().getInt("openAppTimes", 0)
            if (openAppTimes == 0) {
                SharedPreUtils.getInstance().putLong("installTime", System.currentTimeMillis())
            }
            openAppTimes++
            SharedPreUtils.getInstance().putInt("openAppTimes", openAppTimes)

            val channelId = Channel.channelName
            val version = BuildConfig.VERSION_NAME
            val hasUpload = SharedPreUtils.getInstance().getBoolean("hasUpload", false)
            if (!hasUpload) {
                repository.uploadChannelInfo(lifecycleOwner, channelId, version)
                        .subscribe({
                        }, {}, {})
            }

            viewModelScope.launch {
                CommonDataProvider.instance.saveUserInfo( userInfoRepository.getUserInfo() )
                CommonDataProvider.instance.categoryConfig = categoryRepository.getCategory().data
            }

            success.invoke()
        }
    }


    fun registerToken(lifecycleOwner: LifecycleOwner) {
        //repository = LaunchRepository()
        repository.registerToken(lifecycleOwner)
                .subscribe(StatusResourceObserver(registerTokenLiveData))
    }

    private var tokenRetryTime = 0

    @SuppressLint("CheckResult")
    fun retryRegisterToken(lifecycleOwner: LifecycleOwner) {
        //repository = LaunchRepository()
        repository.registerToken(lifecycleOwner).subscribe({}, {
            it.printStackTrace()
            tokenRetryTime++
            if (tokenRetryTime < 5) {
                retryRegisterToken(lifecycleOwner)
            }
        })
    }

    private var imgRetryTime = 0

    @SuppressLint("CheckResult")
    fun retryImgDomain(lifecycleOwner: LifecycleOwner, successCall: () -> Unit) {
        //repository = LaunchRepository()
        repository.getImgDomain(lifecycleOwner).subscribe({ successCall.invoke() }, {
            it.printStackTrace()
            imgRetryTime++
            if (imgRetryTime < 5) {
                retryImgDomain(lifecycleOwner, successCall)
            }
        })
    }

    private var userInfoRetry = 0

    @SuppressLint("CheckResult")
    fun retryUserInfo(successCall: (userInfo: UserInfoBean) -> Unit) {
        viewModelScope.launch {
            val userBean = userInfoRepository.getUserInfo()
            if(userBean==null) {
                userInfoRetry++
                if (userInfoRetry < 5) {
                    retryUserInfo(successCall)
                }
                return@launch
            }
            successCall.invoke(userBean)
        }
    }

    /**
     * 前置接口，在所有接口之前调用
     */
    private var retryTime = 0

    @SuppressLint("CheckResult")
    private fun preAll(lifecycleOwner: LifecycleOwner, doAfter: () -> Unit) {
        val preTasks = mutableListOf<Observable<*>>()
        preTasks.add(repository.registerToken(lifecycleOwner))
        preTasks.add(repository.getImgDomain(lifecycleOwner))

        Observable.merge(preTasks)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .subscribe({

                }, {
                    it.printStackTrace()
                    retryTime++
                    if (retryTime < 10) {
                        preAll(lifecycleOwner, doAfter)
                    } else {
                        doAfter.invoke()
                    }
                }, {
                    doAfter.invoke()
                })
    }
}