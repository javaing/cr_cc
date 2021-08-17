package com.aliee.quei.mo.ui.video

import androidx.recyclerview.widget.GridLayoutManager
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject

import com.dueeeke.videocontroller.component.TitleView
import com.dueeeke.videoplayer.player.VideoView
import com.google.gson.Gson
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.EventToMineFreeTime
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.config.AdEnum
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.main.vm.MainVideoModel
import com.aliee.quei.mo.ui.video.adapter.VideoGuessLikeAdapter
import com.aliee.quei.mo.ui.video.view.GestureView
import com.aliee.quei.mo.ui.video.view.PrepareView
import com.aliee.quei.mo.ui.video.controller.VideoController
import com.aliee.quei.mo.ui.video.view.CompleteView
import com.aliee.quei.mo.ui.video.view.VodControlView
import com.aliee.quei.mo.ui.video.vm.VideoInfoModel
import com.aliee.quei.mo.utils.StringUtils
import com.aliee.quei.mo.utils.TagCountManager
import com.aliee.quei.mo.utils.extention.*
import com.aliee.quei.mo.utils.rxjava.RxBus

import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_viceo_info.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.layout_title.*


/**
 *
 * 免费观看已用完，金币剩余50
 * 如还要继续观看视频请充值喔
 * 去充值
 *
 */

@Route(path = Path.PATH_VIDEO_PLAYER)
class VideoDetailActivity : BaseActivity() {
    @Autowired
    @JvmField
    var videoInfoJson: String? = ""

    private var videoInfoVModel = VideoInfoModel()
    private val VM = MainVideoModel()

    private lateinit var videoController: VideoController
    private lateinit var prepareView: PrepareView

    private var mVideoInfo: VideoInfo? = null
    private lateinit var videoBean: VideoBean
    private lateinit var videoTips: VideoTips

    private lateinit var adapter: VideoGuessLikeAdapter

    private lateinit var thumb: ImageView

    private var videoId = 0

    private var imageDomain: String? = null
    override fun getPageName(): String? {
        return this.getPageName()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_viceo_info
    }


    override fun initView() {
        titleBack.setOnClickListener { finish() }
        titleText.text = "视频详情"

        btn_collection.setOnClickListener {
            addMyVideo()
        }

        iv_share.setOnClickListener {
            val video = Gson().fromJson(videoInfoJson,Video::class.java)
            VideoShareActivity.toThis(this, video, CommonDataProvider.instance.getVideoThumbDomain())
        }
        showLoading()
        initRv()
        initVM()
    }

    override fun initData() {
        videoBean = Gson().fromJson(videoInfoJson, VideoBean::class.java)
        //Log.e("video", "videoBean:$videoBean")
        showLoadingView(true)
        imageDomain = CommonDataProvider.instance.getVideoDomain()
        if(!imageDomain.equals("")) {
            Log.e("video", "cache domain:$imageDomain")
        } else {
            videoInfoVModel.getVideoDomainType()
        }

        videoInfoVModel.getVideoPath(videoBean.id, "u_temp_user_0")
        setVideoInfo()
        initVideoView()
    }

    private fun initRv() {
        adapter = VideoGuessLikeAdapter()
        rv.layoutManager = GridLayoutManager(this, 2)
        rv.adapter = adapter
        rv.addItemDecoration(RecycleGridDivider())
        rv.isNestedScrollingEnabled = false

        adapter.setOnItemClickListener(object : VideoGuessLikeAdapter.OnItemClickListener {
            override fun onItemClick(video: VideoBean) {
                if (video.thumbImg.contains("http")){
                    AdConfig.adClick(this@VideoDetailActivity, video.adClickUrl!!)
                }else{
                    val videoInfoJson = Gson().toJson(video)
                    ARouterManager.goVideoInfoActivity(this@VideoDetailActivity, videoInfoJson)
                    finish()
                }
            }
        })
    }

    private fun initVideoView() {
        player.setPlayerBackgroundColor(resources.getColor(R.color.transparent))
        player.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT)
        videoController = VideoController(this)
        videoController.setEnableOrientation(true)
        prepareView = PrepareView(this)   //添加准备画面的view
        thumb = prepareView.findViewById<ImageView>(R.id.thumb) //封面图

        prepareView.recharge.setOnClickListener { ARouterManager.goRechargeActivity(this, bookid = videoBean.id, isBook = false) }
        val titleView = TitleView(this) //标题栏
        titleView.setTitle(videoBean.name)
        val vodController = VodControlView(this)    //设置底部控制栏
        val gestureControlView = GestureView(this)  //滑动控制视图
        videoController.addControlComponent(titleView)
        videoController.addControlComponent(vodController)
        videoController.addControlComponent(gestureControlView)
        videoController.addControlComponent(CompleteView(this))
        videoController.addControlComponent(prepareView)
        //   videoController.addControlComponent(tipsControlView)
        videoController.setEnableOrientation(true)
        videoController.setEnableInNormal(true)   //竖屏也开启手势操作，默认关闭
        videoController.setGestureEnabled(true)  //滑动调节亮度，音量，进度，默认开启
        player.setVideoController(videoController)
        player.commonOption()

        player.setOnStateChangeListener(object : VideoView.OnStateChangeListener {
            var videoPlayTime = ""
            override fun onPlayStateChanged(playState: Int) {
                //监听VideoViewManager释放，重置状态
                when (playState) {
                    VideoView.STATE_PLAYING -> {
                        videoPlayTime = StringUtils.formatTime(player.duration.toInt())
                        Log.d("tag", "STATE_PLAYING:videoTime:$videoPlayTime")
                    }
                    VideoView.STATE_PLAYBACK_COMPLETED -> {
                        Log.d("tag", "STATE_PLAYBACK_COMPLETED:videoTime:${player.duration}")
                        //记录当前视频完成播放次数
                        VM.videoEndPlay(videoId, videoPlayTime, done = 1)
                    }
                }
            }

            override fun onPlayerStateChanged(playerState: Int) {
                //处理全屏下图片背景
                if (playerState == VideoView.PLAYER_FULL_SCREEN) {
                    //  fullscreen_bg.visibility = View.VISIBLE
                    //  fullscreen_bg.loadBlurCover(replaceImageUrl(imageDomain!!))

                    // fullscreen_bg.setImageResource(R.mipmap.img_launch3)
                    player.setPlayerBackgroundColor(resources.getColor(R.color.all_black))
                } else {
                    player.setPlayerBackgroundColor(resources.getColor(R.color.transparent))
                    // fullscreen_bg.visibility = View.GONE
                }
            }
        })
    }

    private fun setPlayerData(videoPath: String) {
        Log.e("tag", "videoPath="+videoPath.videoPath())
        player.setUrl(videoPath.videoPath())
        // player.setUrl("http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4")
        player.start()
    }


    private fun setVideoInfo() {
        videoBean.apply {
            tv_video_dis.text = Html.fromHtml(name).trim()
            tv_collection_num.text = favcounts
            tv_watch_num.text = playcounts
            //  tv_video_price.text = price.toString()

            if (isFav == 1) {
                btn_collection.isEnabled = false
                btn_collection.setBackgroundResource(R.drawable.bg_join_my_video_cli)
            } else {
                btn_collection.isEnabled = true
                btn_collection.setBackgroundResource(R.drawable.bg_join_my_video)
            }
        }

        if (videoBean.tags.isEmpty()) {
            tag_layout.visibility = View.GONE
        } else {
            tag_layout.visibility = View.VISIBLE
            tag_layout.adapter = object : TagAdapter<Tags?>(videoBean.tags) {
                override fun getView(parent: FlowLayout, position: Int, t: Tags?): View? {
                    val textView = parent.context?.inflate(R.layout.video_tag_layout, parent, false) as TextView
                    textView.text = t?.name
                    return textView
                }
            }
            TagCountManager.saveMoreTagCount(videoBean.tags)
        }
        val tagId = TagCountManager.getTagCount()?.tagId
        videoInfoVModel.loadGuessLike(tagId)
        videoAd()
    }

    private fun showLoadingView(b: Boolean) {
        if (b) {
            loading_view.visibility = View.VISIBLE
        } else {
            loading_view.visibility = View.GONE
        }
    }

    private fun addMyVideo() {
        videoInfoVModel.addMyVideo(videoBean.id)
    }
    private fun videoAd() {
        val adBean = AdConfig.getAd(AdEnum.VIDEO_INFO.zid)
        adBean?.also { adBean ->
            AdConfig.getAdInfo(adBean, { adInfo->
               runOnUiThread {
                   if (AdConfig.isClosed(adBean.close)) {
                       video_ad_iv_close.show()
                       video_ad_iv_close.click {
                           video_ad.gone()
                       }
                   } else {
                       video_ad_iv_close.gone()
                   }
                    video_ad.show()
                    iv_video_ad.loadHtmlImg(adInfo.imgurl)
                    AdConfig.adPreview(adInfo.callbackurl)
                    iv_video_ad.click {
                        AdConfig.adClick(iv_video_ad.context,adInfo.clickurl)
                    }
                }
            }, {

            })
        }
    }

    fun guessLikeAd(list : MutableList<VideoBean>) {
        Log.e("guess", "ad list size:"+list.size)
        val adBean: AdBean?
        try {
            adBean = AdConfig.getAd(AdEnum.VIDEO_GUESS_LIKE_RANK.zid)
            Log.e("guess", "ad: bean :"+adBean.toString())
            if (adBean==null){
                runOnUiThread {adapter.setItem(list)}
                return
            }
        } catch (e: Exception) {
            runOnUiThread {adapter.setItem(list)}
            return
        }
        adBean.also { adBean ->
            AdConfig.getAdInfo(adBean, { adInfo->
                val option = Gson().fromJson<Option>(adInfo.optionstr, Option::class.java)
                adInfo.title = option.title
                adInfo.desc = option.desc
                runOnUiThread {
                    Log.d("tag","视频详情猜你喜欢：interval:${adBean.interval},物料：$adInfo")
                    val videoBean = VideoBean(adInfo.title!!,"",-321,-1,adInfo.desc!!,"",-1, mutableListOf(),"",adInfo.imgurl,
                        "",1,"")
                    videoBean.adCallbackUrl = adInfo.callbackurl
                    videoBean.adClickUrl = adInfo.clickurl
                    list.add(0,videoBean)
                    adapter.setItem(list)
                }
            }, {
                runOnUiThread {adapter.setItem(list)}
            })
        }
    }


    private fun initVM() {
        videoInfoVModel.guessLikes.observeForever {
            when (it?.status) {
                Status.Success -> {
                    guessLikeAd(it.data!!)
                }
                Status.Error -> {

                }
            }
        }
        videoInfoVModel.videoPath.observeForever {
            when (it?.status) {
                Status.Success -> {
                    val json = Gson().toJson(it.data!!)
                    val jsonObj = JSONObject.parseObject(json)
                    val code = jsonObj.getInteger("code")
                    val data = jsonObj.getString("data")
                    val msg = jsonObj.getString("msg")

                    if (code == 0) {
                        //正常播放
                        mVideoInfo = Gson().fromJson(data, VideoInfo::class.java)
                        prepareView.hideTipsView()
                        prepareView.setFreeCount(mVideoInfo!!.freetime)
                        videoId = mVideoInfo!!.id
                        setPlayerData(mVideoInfo!!.video_path)
                        if (mVideoInfo!!.freetime.use != 0) {
                            RxBus.getInstance().post(EventToMineFreeTime())
                        }
                    } else if (code == -1) {
                        //金币不足，免费次数不足时
                        if(data!=null) {
                            videoTips = Gson().fromJson(data, VideoTips::class.java)
                            prepareView.showTipsView(true)
                            prepareView.setTipsText(videoTips.bookbean.bookbean.toString())
                        }
                        player.onError()
                    }
                    showLoadingView(false)
                    disLoading()
                }

                Status.Error -> {

                }
            }
        }

        videoInfoVModel.videoDomainType.observeForever {
            when (it?.status) {
                Status.Complete -> {

                }
                Status.Success -> {
                    imageDomain = it.data!!.`20`[0].domain
                    Log.d("tag", "imageDomain:$imageDomain")
                    //获取视频
                    if(imageDomain!=null) {
                        initVideoDomain(imageDomain!!)
                    } else {
                        toast("获取视频網域失敗，請關閉app再試")
                    }

                }
                Status.Error -> {

                }
            }
        }

        videoInfoVModel.addMyVideo.observeForever {
            when (it?.status) {
                Status.Success -> {
                    if (it.data!!.code == 0) {
                        btn_collection.isEnabled = false
                        btn_collection.text = "已加入收藏"
                        btn_collection.setBackgroundResource(R.drawable.bg_join_my_video_cli)
                    }
                }
                Status.Error -> {

                }
            }
        }
    }

    private fun initVideoDomain(domain: String) {
        adapter.setImageDomain(domain)
        parseImageData(domain)
    }


    //图片处理
    private fun parseImageData(domain: String) {
        var imageUrl = replaceImageUrl(domain)
        thumb.loadImageScale(imageUrl)
        iv_blue_thumb.loadBlurCover(imageUrl.videoUrl())
        iv_blue_thumb.scaleType = ImageView.ScaleType.FIT_XY

    }

    private fun replaceImageUrl(domain: String): String {
        return "${domain}${videoBean.thumbImg}".replace(".jpeg", ".html")
    }


    override fun onResume() {
        super.onResume()
        if (player != null) {
            player.resume()
        }
        //充值完成后回到界面刷新
        videoInfoVModel.getVideoPath(videoBean.id, "u_temp_user_0")
    }


    override fun onPause() {
        super.onPause()
        if (player != null) {
            player.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (player != null) {
            player.release()
        }
    }

    override fun onBackPressed() {
        if (player == null || !player.onBackPressed()) {
            super.onBackPressed()
        }
    }


}