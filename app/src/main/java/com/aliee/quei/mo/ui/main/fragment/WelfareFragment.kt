package com.aliee.quei.mo.ui.main.fragment

import android.app.AlertDialog
import android.app.Dialog
import androidx.lifecycle.Observer
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.widget.TextViewCompat
import androidx.appcompat.widget.*
import android.util.TypedValue
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.*
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.config.LevelConfig
import com.aliee.quei.mo.data.bean.CheckInStatsBean
import com.aliee.quei.mo.data.bean.TicketBean
import com.aliee.quei.mo.data.bean.UserInfoBean
import com.aliee.quei.mo.data.bean.UserLevelBean
import com.aliee.quei.mo.data.local.LoginRecordBean
import com.aliee.quei.mo.data.local.ReadStatBean
import com.aliee.quei.mo.data.local.TaskBean
import com.aliee.quei.mo.database.DatabaseProvider
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.main.adapter.SignDayAdapter
import com.aliee.quei.mo.ui.main.adapter.TaskAdapter
import com.aliee.quei.mo.ui.main.vm.WelfareVModel
import com.aliee.quei.mo.utils.ScreenUtils
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.extention.*
import kotlinx.android.synthetic.main.fragment_welfare.*
import kotlinx.android.synthetic.main.layout_title.*
import kotlinx.android.synthetic.main.layout_welfare_checkin.*
import kotlinx.android.synthetic.main.layout_welfare_daily.*
import kotlinx.android.synthetic.main.layout_welfare_income.*
import kotlinx.android.synthetic.main.layout_welfare_level.*
import kotlinx.android.synthetic.main.layout_welfare_newbie.*
import kotlinx.android.synthetic.main.layout_welfare_ticket.*
import kotlinx.android.synthetic.main.layout_welfare_week_duration2.*
import kotlinx.android.synthetic.main.statusbar_holder.*
import java.text.SimpleDateFormat
import java.util.*

@Route(path = Path.PATH_MAIN_WELFARE)
class WelfareFragment : BaseFragment() {

    private val VM = WelfareVModel()
    private val launchVModel = LaunchVModel()
    private val signAdapter = SignDayAdapter()
    private val realm = DatabaseProvider.getRealm()
    private val newbieTaskAdapter = TaskAdapter(realm)
    private val dailyTaskAdapter = TaskAdapter(realm)
    private val weekTaskAdapter = TaskAdapter(realm)
    private var dialog: Dialog? = null
    override fun getLayoutId(): Int {
        return R.layout.fragment_welfare
    }

    private fun showDialog() {
        if (dialog == null) {
            dialog = AlertDialog.Builder(requireContext())
                    .setTitle("提示")
                    .setMessage("明天再来，可以开启赚金币模式")
                    .setPositiveButton("好的") { d, w ->
                        d.dismiss()
                    }.create()
        }
        dialog?.show()
    }

    override fun initView() {
        initTitle()
        initSignRecyclerView()
        initWidget()
        initClick()
        initVM()
        initNewbieTasksRecyclerView()
        initDailyTasksRecyclerView()
        initWeekTaskRecyclerView()
    }

    private fun initWeekTaskRecyclerView() {
        rvWeek.adapter = weekTaskAdapter
        rvWeek.layoutManager = LinearLayoutManager(activity)
        rvWeek.isNestedScrollingEnabled = false
        rvWeek.addItemDecoration(DividerItemDecoration(activity, OrientationHelper.VERTICAL))
        weekTaskAdapter.claimClick = {
            if (isBanned) {
                showDialog()
            } else {
                VM.claimReward(this, it)
            }
        }
    }

    private fun initDailyTasksRecyclerView() {
        rvDaily.adapter = dailyTaskAdapter
        rvDaily.layoutManager = LinearLayoutManager(activity)
        rvDaily.addItemDecoration(DividerItemDecoration(activity, OrientationHelper.VERTICAL))
        rvDaily.isNestedScrollingEnabled = false
        dailyTaskAdapter.claimClick = {
            if (isBanned) {
                showDialog()
            } else {
                VM.claimReward(this, it)
            }
        }
    }

    private fun initNewbieTasksRecyclerView() {
        rvNewbie.adapter = newbieTaskAdapter
        rvNewbie.layoutManager = LinearLayoutManager(activity)
        rvNewbie.addItemDecoration(DividerItemDecoration(activity, OrientationHelper.VERTICAL))
        rvNewbie.isNestedScrollingEnabled = false
        newbieTaskAdapter.claimClick = {
            if (isBanned) {
                showDialog()
            } else {
                VM.claimReward(this, it)
            }
        }
    }

    private fun initVM() {
        VM.totalIncomeLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
//                    showBalance()
                    tvIncome.text = it.data?.count.toString()
                }
            }
        })
        VM.userInfoLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    showBalance(it.data)
                }
                Status.TokenError -> {
                    launchVModel.registerToken(this)
                }
            }
        })
        launchVModel.registerTokenLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    VM.getUserInfo()
                }
            }
        })
        VM.ticketLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    showTickets(it.data?.list)
                }
            }
        })
        VM.levelLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val data = it.data ?: return@Observer
                    showUserLevel(data)
                }
            }
        })
        VM.checkInLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    VM.getCheckInStats(this)
                    toast("签到成功")
                    VM.getUserInfo()
                    VM.getIncome(this)
                }
            }
        })
        VM.checkStatsLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    signAdapter.setData(it.data)
                    showSignInfo(it.data)
                }
            }
        })
        VM.claimLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
//                    val bean = it
//                    weekTaskAdapter.notifyDataSetChanged()
                    val bean = it.data
                    toast("领取成功 +${bean?.reward}金币")
                    weekTaskAdapter.updateBean(bean)
                    dailyTaskAdapter.updateBean(bean)
                    newbieTaskAdapter.updateBean(bean)
                    VM.getUserInfo()
                    VM.getIncome(this)
                }
                Status.Start -> showLoading()
                Status.Complete -> disLoading()
            }
        })
    }

    private var isBanned = false
    private fun showBalance(data: UserInfoBean?) {
        data ?: return
        tvBalance.text = data.bookBean.toString()

//        val calandar data.registerTime * 1000
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val registerDate = sdf.format(Date(data.registerTime))
        val nowDate = sdf.format(Date())
        isBanned = registerDate == nowDate
    }

    private fun showUserLevel(data: UserLevelBean) {
        tvLevel.text = "Lv.${data.level}"
        tvNextLevel.text = "Lv.${data.level + 1}"
        tvTitle.text = LevelConfig.getTitleByLevel(data.level)

        tvChapterLeft.text = "还需看书${data.diff}章"
        pbChapter.max = data.next_total
        pbChapter.progress = data.next_total - data.diff

        if (data.level < 1) return
        var level = data.level
        if (level < 1) level = 1
        if (level > 50) level = 50
        val id = resources.getIdentifier("ic_level_${level}", "mipmap", ReaderApplication.instance.packageName)
        ivLevel.setImageResource(id)
        var star = level % 5
        if (star == 0) star = 5
        layoutStar.removeAllViews()
        for (i in 0 until star) {
            val ivStar = ImageView(context)
            ivStar.setImageResource(R.mipmap.ic_medal_star)
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.leftMargin = ScreenUtils.dpToPx(5)
            layoutStar.addView(ivStar, params)
        }
    }

    private fun showSignInfo(data: List<CheckInStatsBean>?) {
        data ?: return
        var myDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
        if (myDayOfWeek == 0) myDayOfWeek = 7
        val hasSign = data.find {
            it.weekday == myDayOfWeek
        } != null
        if (hasSign) {
            btnSign.disable()
            btnSign.setBackgroundResource(R.drawable.bg_btn_sign_welfare_disable)
            tvSign.text = "今日已签"
            waveView.stop()
        } else {

        }
        var amountWeek = 0
        data.forEach {
            amountWeek += it.book_bean
        }

        var tempDay = myDayOfWeek
        var numContinuous = 0
        for (i in data.size - 1 downTo 0) {
            val bean = data[i]
            if (tempDay == bean.weekday) {
                numContinuous++
            } else {
                if (tempDay < myDayOfWeek - 1) {
                    break
                }
            }
            tempDay--
        }
        tvDaysContinuous.setText(numContinuous.toString(), true)
        tvSignCoinTotal.setText(amountWeek.toString(), true)
    }

    private fun showTickets(list: List<TicketBean>?) {
        if (list == null || list.isEmpty()) {
            layoutTickets.gone()
            return
        }
        layoutTickets.show()
        layoutTickets.click {
            if (isBanned) {
                showDialog()
                return@click
            }
            ARouterManager.goTicketActivity(it.context)
        }
        val bean = list[0]
        tvTicketAmount.text = "+${bean.complete_reward}金币"
        tvTicketDescr.text = "阅读${bean.chapter_free}章可领取"
        tvTicketTitle.text = "《${bean.title}》"
        tvTicketFreeChapter.text = " 可免费解锁至第${bean.chapter_free}章"
        ivTicketCover.loadNovelCover(bean.thumb_x)
    }

    private fun initTitle() {
        systemBarHolder.setBackgroundColor(getResColor(R.color.color_welfare))
        titleBar.setBackgroundColor(getResColor(R.color.color_welfare))
        titleBack.gone()
        titleText.text = "今日福利"
    }

    private fun initClick() {
        ticketMore.click {
            if (isBanned) {
                showDialog()
                return@click
            }
            ARouterManager.goTicketActivity(it.context)
        }
        btnSign.click {
            if (isBanned) {
                showDialog()
                return@click
            }
            VM.checkIn(this)
        }
        btnDetails.click {
            if (isBanned) {
                showDialog()
                return@click
            }
            ARouterManager.goWelfareCoinListActivity(it.context)
        }
//        mask.click {
//            toast("明日再来可以开启赚金币模式")
//        }
    }

    private fun initWidget() {
        waveView.start()
        waveView.setInitialRadius(ScreenUtils.dpToPx(50).toFloat())
        waveView.setStyle(Paint.Style.FILL)
        waveView.setSpeed(300)
        waveView.setColor(Color.parseColor("#FFAFB2"))
    }

    private fun initSignRecyclerView() {
        rvSign.adapter = signAdapter
        rvSign.layoutManager = GridLayoutManager(activity, 7)
        rvSign.isNestedScrollingEnabled = false
        rvSign.addItemDecoration(object : RecyclerView.ItemDecoration() {
            val spacing = ScreenUtils.dpToPx(10)
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                super.getItemOffsets(outRect, itemPosition, parent)
                val column = itemPosition % 7
                outRect.left = spacing - column * spacing / 7 // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / 7 // (column + 1) * ((1f / spanCount) * spacing)
            }
        })
    }

    override fun initData() {
        VM.getTickets(this)
        VM.getCheckInStats(this)
        VM.getUserLevel(this)
        VM.getUserInfo()
        VM.getIncome(this)
        loadWeek()
        loadDaily()
        loadNewbie()
        isFirst = true
    }

    private fun loadNewbie() {
        val loginDays = (realm.where(LoginRecordBean::class.java)
                .findAll() ?: mutableListOf<LoginRecordBean>()).size
        if (loginDays >= 7) {
            layoutNewbie.gone()
            return
        }

        val list = realm.where(TaskBean::class.java)
                .equalTo("period", 2.toInt())
                .findAll() ?: return

        newbieTaskAdapter.setData(realm.copyFromRealm(list))

        val hasRecharge = SharedPreUtils.getInstance().getBoolean("hasRecharge", false)
        if (!hasRecharge) {
            layoutFirstRecharge.show()
        } else {
            layoutFirstRecharge.gone()
        }
        layoutFirstRecharge.click {
            if (isBanned) {
                showDialog()
                return@click
            }
            ARouterManager.goRechargeActivity(it.context, "", 0)
        }
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tvFirstRecharge, 10, 17, 1, TypedValue.COMPLEX_UNIT_SP)
    }

    private fun loadDaily() {
        val list = realm.where(TaskBean::class.java)
                .equalTo("period", 0.toInt())
                .findAll() ?: return
        dailyTaskAdapter.setData(realm.copyFromRealm(list))
    }

    private fun loadWeek() {
        val list = realm.where(TaskBean::class.java)
                .equalTo("period", 1.toInt())
                .findAll() ?: return
        weekTaskAdapter.setData(realm.copyFromRealm(list))
        val calendar = Calendar.getInstance()

        var duration = 0
        realm.where(ReadStatBean::class.java)
                .equalTo("year", calendar.get(Calendar.YEAR).toInt())
                .equalTo("weekOfYear", calendar.get(Calendar.WEEK_OF_YEAR))
                .findAll()?.forEach {
                    duration += it.duration
                }
        tvWeekTitle.text = "本周阅读${duration}分钟"
    }

    override fun onDestroyView() {
        waveView.stop()
        super.onDestroyView()
    }

    private var paused = false
    override fun onPause() {
        super.onPause()
        paused = true
    }

    override fun onResume() {
        super.onResume()
        if (paused) initData()
    }

    override fun getPageName() = "福利"
}