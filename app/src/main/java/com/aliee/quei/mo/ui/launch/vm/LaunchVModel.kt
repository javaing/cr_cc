package com.aliee.quei.mo.ui.launch.vm

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import android.util.Log
import com.elvishew.xlog.BuildConfig
import com.google.gson.reflect.TypeToken
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.StatusResourceObserver
import com.aliee.quei.mo.base.response.UIDataBean
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.GsonProvider
import com.aliee.quei.mo.data.bean.DomainBean
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.data.local.TaskBean
import com.aliee.quei.mo.data.repository.*
import com.aliee.quei.mo.database.DatabaseProvider
import com.aliee.quei.mo.net.ApiConstants
import com.aliee.quei.mo.utils.AES
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.meituan.android.walle.WalleChannelReader
import com.aliee.quei.mo.data.bean.Tag
import com.aliee.quei.mo.data.bean.Tags
import com.google.gson.Gson
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import okhttp3.*
import java.io.IOException


/**
 * Created by Administrator on 2018/4/28 0028.
 */
class LaunchVModel : BaseViewModel() {

    private lateinit var repository: LaunchRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var welfareRepository: WelfareRepository
    private lateinit var rechargeRepository: RechargeRepository
    private lateinit var userInfoRepository: UserInfoRepository
    private lateinit var recommendRepository: RecommendRepository
    private lateinit var versionRepository: VersionRepository
    private lateinit var mainVideoRepository: MainVideoRepository

    val prepareLiveData = MediatorLiveData<UIDataBean<Any>>()
    val getUserInfoLiveData = MediatorLiveData<UIDataBean<UserInfoBean>>()
    val appupdateopLiveData = MediatorLiveData<UIDataBean<String>>()
    val mainVideoLiveData = MediatorLiveData<UIDataBean<MutableList<Tag>>>()
    val registerTokenLiveData = MediatorLiveData<UIDataBean<Any>>()

    private var backup_oss: List<String>? = null

    fun appUpdateOp(lifecycleOwner: LifecycleOwner, uid: Int, utemp: Int, opType: Int) {
        recommendRepository.appUpdateOp(lifecycleOwner, uid, utemp, opType).subscribe(StatusResourceObserver(appupdateopLiveData))
    }

    fun updateAppop(lifecycleOwner: LifecycleOwner, opType: Int, uid: Int, utemp: Int) {
        versionRepository.appop(lifecycleOwner, opType, uid, utemp)
                .subscribe()
    }


    fun loadUseInfo(lifecycleOwner: LifecycleOwner) {
        userInfoRepository = UserInfoRepository()
        userInfoRepository.getUserInfo(lifecycleOwner)
                .subscribe(StatusResourceObserver(getUserInfoLiveData))
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
                            val domainList = GsonProvider.gson.fromJson<List<DomainBean>>(response.body()!!.charStream(), type)
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
                                    backup_oss = domainList[0]?.backup_oss as List<String>
                                    SharedPreUtils.getInstance().putInt("backupNums", backup_oss!!.size)
                                    Log.d("LaunchVModel", "Backup_oss:" + backup_oss.toString())
                                    for (i in 0 until backup_oss!!.size) {
                                        SharedPreUtils.getInstance().putString("backup_" + i, backup_oss!!.get(i))
                                    }

                                }
                            }
                            if (ApiConstants.API_HOST.isNotEmpty() || ApiConstants.API_HOST.isNotBlank()) {
                                SharedPreUtils.getInstance().putString("apiDomain", ApiConstants.API_HOST)
                                SharedPreUtils.getInstance().putString("csRoute", ApiConstants.CS_ROUTE)
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

    fun reGet(successCall: () -> Unit, failed: () -> Unit, url: String) {

        val request = Request.Builder()
                .url(url)
                .get()
                .build()
        OkHttpClient()
                .newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        failed.invoke()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            val type = object : TypeToken<List<DomainBean>>() {}.type

                            val domainList =
                                    GsonProvider.gson.fromJson<List<DomainBean>>(response.body()!!.charStream(), type)
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
                                    backup_oss = domainList[0]?.backup_oss as List<String>
                                    SharedPreUtils.getInstance().putInt("backupNums", backup_oss!!.size)
                                    Log.d("LaunchVModel", "Backup_oss:" + backup_oss.toString())
                                    for (i in 0 until backup_oss!!.size) {
                                        SharedPreUtils.getInstance().putString("backup_" + i, backup_oss!!.get(i))
                                    }

                                }
                            }
                            if (ApiConstants.API_HOST.isNotEmpty() || ApiConstants.API_HOST.isNotBlank()) {
                                SharedPreUtils.getInstance().putString("apiDomain", ApiConstants.API_HOST)
                                SharedPreUtils.getInstance().putString("csRoute", ApiConstants.CS_ROUTE)
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

    @SuppressLint("CheckResult")
    fun doLaunch(lifecycleOwner: LifecycleOwner, success: () -> Unit) {
        repository = LaunchRepository()
        categoryRepository = CategoryRepository()
        welfareRepository = WelfareRepository()
        rechargeRepository = RechargeRepository()
        userInfoRepository = UserInfoRepository()
        recommendRepository = RecommendRepository()
        mainVideoRepository = MainVideoRepository()
        versionRepository = VersionRepository()


        preAll(lifecycleOwner) {
            prepareLiveData.value = UIDataBean(Status.Success)
            var openAppTimes = SharedPreUtils.getInstance().getInt("openAppTimes", 0)
            if (openAppTimes == 0) {
                SharedPreUtils.getInstance().putLong("installTime", System.currentTimeMillis());
            }
            openAppTimes++;
            SharedPreUtils.getInstance().putInt("openAppTimes", openAppTimes)

            val channelId = WalleChannelReader.getChannel(ReaderApplication.instance, "1") ?: "98"
            val version = BuildConfig.VERSION_NAME
            val hasUpload = SharedPreUtils.getInstance().getBoolean("hasUpload", false)
            if (!hasUpload) {
                repository.uploadChannelInfo(lifecycleOwner, channelId, version)
                        .subscribe({
                        }, {}, {})
            }
             userInfoRepository.getUserInfo(lifecycleOwner).subscribe({
                  CommonDataProvider.instance.saveUserInfo(it)
              }, {})

            categoryRepository.getCategory(lifecycleOwner)
                    .subscribe({
                        CommonDataProvider.instance.categoryConfig = it
                    }, {})

            /* welfareRepository.getAllTasks(lifecycleOwner)//activity/alldata
                     .subscribe({
                         val list = it.list
                         val realm = DatabaseProvider.getRealm()
                         realm.executeTransaction {
                             it.delete(TaskBean::class.java)
                             it.insertOrUpdate(list)
                         }
                         realm.close()
                     }, {
                         it.printStackTrace()
                     }, {})*/


            success?.invoke()


        }
    }


    fun registerToken(lifecycleOwner: LifecycleOwner) {
        repository = LaunchRepository()
        repository.registerToken(lifecycleOwner)
                .subscribe(StatusResourceObserver(registerTokenLiveData))
    }

    private var tokenRetryTime = 0

    @SuppressLint("CheckResult")
    fun retryRegisterToken(lifecycleOwner: LifecycleOwner) {
        repository = LaunchRepository()
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
        repository = LaunchRepository()
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
    fun retryUserInfo(lifecycleOwner: LifecycleOwner, successCall: (userInfo: UserInfoBean) -> Unit) {
        userInfoRepository = UserInfoRepository()
        userInfoRepository.getUserInfo(lifecycleOwner).subscribe({
            successCall.invoke(it)
        }, {
            it.printStackTrace()
            userInfoRetry++
            if (userInfoRetry < 5) {
                retryUserInfo(lifecycleOwner, successCall)
            }
        })
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
        preTasks.add(userInfoRepository.getUserInfo(lifecycleOwner))
        //preTasks.add(mainVideoRepository.getAutoPlay(lifecycleOwner))
        // preTasks.add(userInfoRepository.getMemberInfo(lifecycleOwner))
        // preTasks.add(repository.videoLogin(lifecycleOwner))
        /*    var token = CommonDataProvider.instance.getToken()
             if (token.isEmpty()) {
                 preTasks.add(userInfoRepository.getUserInfo(lifecycleOwner))
            } else {
              // preTasks.add(repository.registerToken(lifecycleOwner))
                preTasks.add(userInfoRepository.getUserInfo(lifecycleOwner))
            }*/
//        preTasks.add(rechargeRepository.getPayConfig(lifecycleOwner))
        Observable.merge(preTasks)
                .compose(SchedulersUtil.applySchedulers())
                .bindUntilEvent(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
                .subscribe({

                }, {
                    it.printStackTrace()
                    retryTime++;
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