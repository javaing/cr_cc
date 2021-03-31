package com.aliee.quei.mo.ui.main.fragment

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.*
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.user.vm.UserInfoVModel
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.ToastUtil
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.invisible
import com.aliee.quei.mo.utils.extention.show
import com.aliee.quei.mo.utils.extention.toast
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.attach_layout.*
import kotlinx.android.synthetic.main.fragment_fourth.*
import kotlinx.android.synthetic.main.fragment_new_shop.*
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Administrator on 2018/4/18 0018.
 */
@Route(path = Path.PATH_MAIN_MINE_FRAGMENT)
class MineFragment : BaseFragment() {
    override fun getPageName() = "我的"

    var isBook: Boolean = true
    var userInfoVModel = UserInfoVModel()
    var launchVModel = LaunchVModel()

    private var userInfoBean: UserInfoBean? = null
    private lateinit var settings: SharedPreferences
    override fun getLayoutId() = R.layout.fragment_fourth
    override fun initView() {
        initVM()
        initEvent()
        initClick()
        initRefresh()

        attach_view.setBackgroundResource(R.mipmap.icon_service)
    }


    private fun initRefresh() {
        refreshLayout.setOnRefreshListener {
            userInfoVModel.getMemberInfo(this)
        }
        refreshLayout.isEnableLoadMore = false
    }

    private fun initClick() {
        headImg.click {
            Log.d("hasLogin", "hasLogin:${!CommonDataProvider.instance.hasLogin()}")
            if (!CommonDataProvider.instance.hasLogin()) {
                ARouterManager.goLoginActivity(activity!!)
            }
        }
        layoutOpenVip.click {
            ARouterManager.goRechargeActivity(activity!!, "", 0, isBook)
        }
//        imgSign.click {
//            userInfoVModel.dailySign(this)
//        }
        layoutSwitch.click {
            ARouterManager.goLoginActivity(activity!!)
        }
        layoutBillRecord.click {
            ARouterManager.goBillActivity(activity!!)
        }
        layoutRecord.click {
            ARouterManager.goMainActivity(
                    activity!!,
                    showPage = ARouterManager.TAB_MINE_COMIC,
                    showTab = ARouterManager.SUB_TAB_HISTORY
            )
        }
        layoutRecharge.click {
            ARouterManager.goRechargeActivity(activity!!, "", 0, isBook)
        }
        layoutBind.click {
            ARouterManager.goLoginActivity(activity!!)
        }
        layoutSwitch.click {
            ARouterManager.goLoginActivity(activity!!)
        }

        val packageName_weibo = "com.sina.weibo"
        val csNumber = SharedPreUtils.getInstance().getString("csNumber")
        val csRoute = SharedPreUtils.getInstance().getString("csRoute")
        layoutCustomService.click {
            if (csRoute.isNotEmpty()) {
                ARouterManager.goCustomServiceActivity(it.context)
            } else {
                if (csNumber.isNotEmpty()) {
                    copyText(tv_cstitle)
                    launchApp(packageName_weibo)
                } else {
                    ToastUtil.showToast(activity!!, "目前排查中,请改用客服邮箱", 1000)
                }
            }
        }
        attach_view.click {
            if (csRoute.isNotEmpty()) {
                ARouterManager.goCustomServiceActivity(it.context)
            } else {

                if (csNumber.isNotEmpty()) {
                    copyText(tv_cstitle)
                    launchApp(packageName_weibo)
                } else {
                    ToastUtil.showToast(activity!!, "目前排查中,请改用客服邮箱", 1000)
                }
            }
        }

        layoutRecommendId.click {
         //   ARouterManager.goRecommendActivity(it.context)
            ARouterManager.goVideoSharedActivity(it.context)
        }

        layoutMyBonus.click {
            ARouterManager.goRecommendBonusActivity(it.context)
        }

        layoutExchange.click {
            ARouterManager.goExchangeActivity(it.context)
        }

        layoutFind.click {
            ARouterManager.goRecoverUserActivity(it.context)
        }
        settings = activity!!.getSharedPreferences("DATA", 0)
        val apkPath = settings.getString("apkPath", "")
        layoutUpdate.click {
            actDownloadApk(apkPath)
        }


        var isVideoAutoPlay = CommonDataProvider.instance.getAutoPlay()
        sw_switch.isChecked = isVideoAutoPlay
        sw_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                CommonDataProvider.instance.saveAutoPlay(CommonDataProvider.AUTO_PLAY_OPEN)
            } else {
                CommonDataProvider.instance.saveAutoPlay(CommonDataProvider.AUTO_PLAY_CLOSE)
            }
            RxBus.getInstance().post(EventAutoSwitch())
        }
    }

    @SuppressLint("CheckResult")
    private fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
                .subscribe({
                    if (it is UserInfoBean) {
                        showUserInfo(it)
                    }
                }, {
                    it.printStackTrace()
                })

        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventUserInfoUpdated -> {
                    userInfoVModel.getMemberInfo(this)
                }
            }
        }, {
            it.printStackTrace()
        })

        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventAutoSwitch -> {
                    var isVideoAutoPlay = CommonDataProvider.instance.getAutoPlay()
                    sw_switch.isChecked = isVideoAutoPlay
                }
            }
        }, {
            it.printStackTrace()
        })

        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventToMineFrom -> {
                    isBook = it.isBook
                }
            }
        }, {
            it.printStackTrace()
        })

        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventToMineFreeTime -> {
                    userInfoVModel.getMemberInfo(this)
                }
                is EventModifyPwd -> {
                    userInfoVModel.loadUseInfo(this)
                }
            }
        }, {
            it.printStackTrace()
        })

    }

    private fun initVM() {
        userInfoVModel.getUserInfoLiveData.observeForever {
            when (it?.status) {
                Status.Complete -> {
                    refreshLayout.finishRefresh()
                    disLoading()
                }
                Status.Success -> {
                    userInfoBean = it.data
                    showUserInfo(it.data)
//                    Log.d("MineFragment", "UserInfo：" + it.data)
                }
                Status.Error -> {
                    val e = it.e
                    if (e is HttpException) {
                        MobclickAgent.onEvent(requireContext(), "error_usercenter")
                    }
                }

                Status.TokenError -> {
                    launchVModel.registerToken(this)
                }
            }
        }
        launchVModel.registerTokenLiveData.observe(this, androidx.lifecycle.Observer {
            when (it?.status) {
                Status.Success -> {
                    userInfoVModel.loadUseInfo(this)
                }
            }
        })
        userInfoVModel.getMemberInfoLiveData.observeForever {
            when (it?.status) {
                Status.Complete -> {
                    refreshLayout.finishRefresh()
                    disLoading()
                }
                Status.Success -> {
                    setFreeTime()
                }
                Status.Error -> {
                    val e = it.e
                    if (e is HttpException) {
                        MobclickAgent.onEvent(requireContext(), "error_usercenter")
                    }
                }
            }
        }
        settings = activity!!.getSharedPreferences("DATA", 0)
        var appVersion = settings.getString("appVersion", "")
        if (!appVersion.isNullOrEmpty()) {
            try {
                /* val appVersion_Ori = appVersion.substring(3, 8)
                   Log.d("appVersion","appVersion:$appVersion_Ori")
 //                Log.d("MineFragment", "AppVersion:" + appVersion_Ori)
                   if (versionCompare(appVersion_Ori)) {
                      layoutUpdate.visibility = View.VISIBLE
                 }*/
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                var pinfo: PackageInfo? = null
                pinfo = ReaderApplication.instance.getPackageManager().getPackageInfo("com.hehe.ykzh.mimic", 0)
                val cVersion = pinfo.versionName
                apkVersion.text = "版本:" + cVersion

                pinfo = ReaderApplication.instance.getPackageManager().getPackageInfo("com.hehe.sdwd.enoch", 0)
                val cVersion2 = pinfo.versionName
                apkVersion.text = "版本:" + cVersion2

                pinfo = ReaderApplication.instance.getPackageManager().getPackageInfo("com.aliee.quei.mo", 0)
                val cVersion3 = pinfo.versionName
                apkVersion.text = "版本:" + cVersion3

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
//        userInfoVModel.dailySignLiveData.observe(this, Observer {
//            when(it?.status){
//                Status.Success -> toast(R.string.daily_signin_success)
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
//            }
//        })
    }

    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    @SuppressLint("SetTextI18n")
    private fun showUserInfo(it: UserInfoBean?) {
        it ?: return
        refreshLayout.isEnableRefresh = true

        if (it.phone.isNullOrEmpty()) {
            userId.text = getString(R.string.user_id, it.id)
            userId.show()
            nickName.text = getString(R.string.guest)
        } else {
            userId.text = getString(R.string.user_id, it.uid)
            userId.show()
            nickName.text = it.phone
        }



        balance.text = it.bookBean.toString()
        if (it.vipEndtime > System.currentTimeMillis()) {
            vipEndTime.text = sdf.format(Date(it.vipEndtime))
        } else {
            vipEndTime.text = getString(R.string.not_vip)
        }
        if (it.discountEndtime>System.currentTimeMillis()){
            tv_vip_freetime.text = "视频VIP有效期至:${sdf1.format(Date(it.discountEndtime))}"
        }else{
            tv_vip_freetime.text = "视频VIP有效期至:无"
        }

        if (it.phone.isNullOrEmpty()) {
            layoutBind.visibility = View.VISIBLE
            bindStatus.text = getString(R.string.bind_phone_now)
            layoutBind.click {
                ARouterManager.goLoginActivity(activity!!)
            }
        } else {
            layoutBind.visibility = View.GONE
            bindStatus.text = getString(R.string.has_bind_phone)
            layoutBind.click { }
            layoutModify.visibility = View.VISIBLE
            layoutModify.click {
                ARouterManager.goReset(activity!!)
            }
            tv_password.text = it.password
            tv_password.visibility = View.GONE
            layoutMyBonus.visibility = View.VISIBLE

        }

        setFreeTime()

////        setCouponNum(it.coupon?:0)
//        when(it.sex){
//            BeanConstants.SEX_FEMALE -> imgSex.setImageResource(R.mipmap.ic_girl)
//            BeanConstants.SEX_MALE -> imgSex.setImageResource(R.mipmap.ic_boy)
//            else -> imgSex.setImageResource(R.mipmap.ic_sex_unknow_big)
//        }
        imgSex.invisible()


    }

    fun setFreeTime() {
        val freetime = CommonDataProvider.instance.getFreeTime()
        tv_freetime.text = "今日免费观影剩余${freetime}次"
    }

    private fun versionCompare(sVersion: String): Boolean {
        try {
            var pinfo: PackageInfo? = null
            pinfo = ReaderApplication.instance.getPackageManager().getPackageInfo("com.hehe.ykzh.mimic", 0)
            val cVersion = pinfo.versionName
            if (cVersion == sVersion) return false;
            val sVersionNumber = sVersion.replace(".", "")
                    .substring(0, 3)
                    .toInt()
            val cVersionNumber = cVersion.replace(".", "")
                    .substring(0, 3)
                    .toInt()
            return sVersionNumber > cVersionNumber
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        try {
            var pinfo: PackageInfo? = null
            pinfo = ReaderApplication.instance.getPackageManager().getPackageInfo("com.hehe.sdwd.enoch", 0)
            val cVersion = pinfo.versionName
            if (cVersion == sVersion) return false;
            val sVersionNumber = sVersion.replace(".", "")
                    .substring(0, 3)
                    .toInt()
            val cVersionNumber = cVersion.replace(".", "")
                    .substring(0, 3)
                    .toInt()
            return sVersionNumber > cVersionNumber
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        try {
            var pinfo: PackageInfo? = null
            pinfo = ReaderApplication.instance.getPackageManager().getPackageInfo("com.aliee.quei.mo", 0)
            val cVersion = pinfo.versionName
            if (cVersion == sVersion) return false;
            val sVersionNumber = sVersion.replace(".", "")
                    .substring(0, 3)
                    .toInt()
            val cVersionNumber = cVersion.replace(".", "")
                    .substring(0, 3)
                    .toInt()
            return sVersionNumber > cVersionNumber
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    private fun actDownloadApk(url: String?) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    fun copyText(view: View) {
        val csNumber = SharedPreUtils.getInstance().getString("csNumber")
        var myClipboard: ClipboardManager? = null
        var myClip: ClipData? = null
        myClipboard = activity!!.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager?;
        myClip = ClipData.newPlainText("text", csNumber);
        myClipboard?.setPrimaryClip(myClip);

        Toast.makeText(activity!!, "复制成功", Toast.LENGTH_SHORT).show();
    }


    private fun launchApp(packageName: String) {

//        val cmp = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
        // Get an instance of PackageManager
        val pm = activity!!.packageManager

        // Initialize a new Intent
        val intent: Intent? = pm.getLaunchIntentForPackage(packageName)
        intent?.action = Intent.ACTION_MAIN
        // Add category to intent
        intent?.addCategory(Intent.CATEGORY_LAUNCHER)
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // If intent is not null then launch the app
        if (intent != null) {
            activity!!.startActivity(intent)

        } else {
//            toast("Intent null.")
        }
    }


    override fun initData() {
        userInfoVModel.loadUseInfo(this)
        userInfoVModel.getMemberInfo(this)
        isFirst = true

    }

    override fun onDestroy() {
        userInfoVModel.getUserInfoLiveData.removeObservers(this)
        super.onDestroy()
    }

}