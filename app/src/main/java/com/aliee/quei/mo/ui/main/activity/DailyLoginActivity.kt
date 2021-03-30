package com.aliee.quei.mo.ui.main.activity

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.support.v7.widget.GridLayoutManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bilibili.boxing_impl.view.SpacesItemDecoration
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.EventShowBulletin
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.comic.adapter.ChpaterEndRecommendAdapter
import com.aliee.quei.mo.ui.main.vm.MainVModel
import com.aliee.quei.mo.utils.RxUtils
import com.aliee.quei.mo.utils.ScreenUtils
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.gone
import com.aliee.quei.mo.utils.extention.show
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_daily_login.*
import java.util.concurrent.TimeUnit

@Route(path = Path.PATH_DAILY_LOGIN)
class DailyLoginActivity : BaseActivity() {
    @Autowired
    @JvmField var coins : Int = 0

    private var adapter = ChpaterEndRecommendAdapter()

    private val VM = MainVModel()
    override fun getLayoutId(): Int {
        return R.layout.activity_daily_login
    }

    override fun initData() {
        VM.getRecommend(this)
      //  showCoinIn()
      //  showCoinIn()
    }

    @SuppressLint("CheckResult")
    private fun showCoinIn() {
        layoutDailyReward.startAnimation(mTopInAnim)
        layoutDailyReward.show()

//        Observable.timer(3,TimeUnit.SECONDS)
//            .compose(SchedulersUtil.applySchedulers())
//            .bindUntilEvent(this,Lifecycle.Event.ON_DESTROY)
//            .subscribe {
//                close.callOnClick()
//            }
    }

    override fun initView() {
        initWidget()
        initVM()
        initAnim()
    }

    private fun initAnim() {
        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in)
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
        mTopInAnim.duration = 300
        mTopOutAnim.duration = 300
    }

    private fun initVM() {
        VM.recommendLiveData.observe(this, Observer {
            when(it?.status) {
                Status.Success -> {
                    val list = it.data ?:return@Observer
                    adapter.setData(list)
                }
            }
        })



    }

    private fun initWidget() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this,3)
        frameLayout.click { finish() }
        close.click {
            layoutDailyReward.startAnimation(mTopOutAnim)
            layoutDailyReward.gone()
            finish()
        }
        ivClose.click {
            RxBus.getInstance().post(EventShowBulletin())
            finish()
            }
        layoutDailyReward.click {  }
        recyclerView.addItemDecoration(SpacesItemDecoration(ScreenUtils.dpToPx(2),2))

        tvCoinsGive.text = getString(R.string.daily_first_login_reward,coins)
        tvCoinsGive2.text = getString(R.string.daily_first_login_reward,coins)
    }


    private lateinit var mTopInAnim: Animation
    private lateinit var mTopOutAnim: Animation
    @SuppressLint("CheckResult")
    private fun showDailyReward() {

        mTopInAnim.duration = 300
        mTopOutAnim.duration = 300
        layoutDailyReward.startAnimation(mTopInAnim)
        layoutDailyReward.show()
        tvCoinsGive.text = getString(R.string.daily_first_login_reward,coins)
        close.click {
            layoutDailyReward.startAnimation(mTopOutAnim)
            layoutDailyReward.gone()
        }
        Observable.timer(5, TimeUnit.SECONDS)
            .compose(SchedulersUtil.applySchedulers())
            .bindUntilEvent(this, Lifecycle.Event.ON_DESTROY)
            .subscribe {
                close.callOnClick()
            }
    }
    override fun getPageName() = "每日首次登陆奖励"
}