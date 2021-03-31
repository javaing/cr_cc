package com.aliee.quei.mo.ui.main.activity


import android.app.ActionBar
import androidx.lifecycle.Observer
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.main.vm.BulletinVModel
import com.aliee.quei.mo.utils.ScreenUtils
import com.aliee.quei.mo.utils.extention.click

import kotlinx.android.synthetic.main.activity_bulletindetail.*
import kotlinx.android.synthetic.main.layout_title.*

@Route(path = Path.PATH_BULLETIN_DETAIL_ACTIVITY)
class BulletinDetailActivity : BaseActivity() {
    override fun getLayoutId() = R.layout.activity_bulletindetail

    private val VM = BulletinVModel()
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0


    @Autowired
    @JvmField
    var bulletinid: Int = 0

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData()
    }

    override fun initData() {

        val p = ScreenUtils.getScreenSize(this)
        screenWidth = p.x
        screenHeight = p.y

        Log.d("BulletinDetailActivity", "BulletinDetailID:" + bulletinid)
        VM.getBulletinDetail(this, bulletinid)
    }

    override fun initView() {
        initTitle()
        initVM()
//        initWebView()

    }

    private fun initTitle() {
        titleBack.click { onBackPressed() }
        titleText.text = getString(R.string.bulletin_detail)
    }


//               ARouterManager.goReadActivity(this@BulletinDetailActivity, 4498,  27, 0, true)


    private fun initVM() {
        VM.bulletinDetailLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> {

                }
                Status.Success -> {
                    val bean = it.data
                    Log.d("BulletinDetailActivity", "BulletinDetailData:" + bean)
                    val img = bean?.contentImagePath
                    val imgUrl = "${CommonDataProvider.instance.getImgDomain()}$img"
                    if (!img.isNullOrEmpty()) {
                        Glide.with(ImageView_bulletin)
                                .asDrawable()
                                .load(imgUrl)
                                .into(object : SimpleTarget<Drawable>() {
                                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {

                                        try {

                                            val height = resource.intrinsicHeight
                                            val width = resource.intrinsicWidth
                                            val r = screenWidth * 1.0f / width
                                            var animation = AlphaAnimation(1f, 0f)
                                            animation.duration = 100

                                            ImageView_bulletin.layoutParams.height = (height * r).toInt()
                                            ImageView_bulletin.layoutParams.width = ActionBar.LayoutParams.MATCH_PARENT
                                            ImageView_bulletin.setImageDrawable(resource)

                                        } catch (e: Exception) {

                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onLoadFailed(errorDrawable: Drawable?) {
                                        super.onLoadFailed(errorDrawable)

                                    }
                                })
                    }

                    val pageId = bean?.contentPageId

                    if (pageId == 1) {
                        //首頁
                        ImageView_bulletin.click {
                            ARouterManager.goContentActivity(this, showPage = ARouterManager.TAB_SHOP)
                            finish()
                        }
                    } else if (pageId == 2) {
                        //註冊頁
                        val tlength = CommonDataProvider.instance.getToken()
                        if (tlength.length > 11) {
                            ImageView_bulletin.click {
                                ARouterManager.goRegister(applicationContext)
                                finish()
                            }
                        }

                    } else if (pageId == 3) {
                        //閱讀器
                        val BookId: Int = bean!!.contentBookId
                        ImageView_bulletin.click {
                            ARouterManager.goReadActivity(this, BookId, 18, 0, true)
                        }
                    } else if (pageId == 4) {
                        //充值頁
                        ImageView_bulletin.click {
                            ARouterManager.goRechargeActivity(this,"",0)
                        }
                    }


                }
                Status.Empty -> {

                }
                Status.Error -> {

                }
                Status.NoNetwork -> {

                }
                Status.NoMore -> {

                }
            }
        })
    }


    override fun getPageName() = "公告页面"
}