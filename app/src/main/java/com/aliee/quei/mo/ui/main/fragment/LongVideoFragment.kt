package com.aliee.quei.mo.ui.main.fragment

import android.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.dueeeke.videocontroller.component.TitleView
import com.dueeeke.videoplayer.player.ProgressManager
import com.dueeeke.videoplayer.player.VideoView
import com.dueeeke.videoplayer.player.VideoViewManager
import com.google.gson.Gson
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.*
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.config.AdEnum
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.net.ApiConstants
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.main.adapter.MainVideoAdapter
import com.aliee.quei.mo.ui.main.vm.MainVideoModel
import com.aliee.quei.mo.ui.video.Utils
import com.aliee.quei.mo.ui.video.VideoShareActivity
import com.aliee.quei.mo.ui.video.controller.VideoController
import com.aliee.quei.mo.ui.video.view.*
import com.aliee.quei.mo.utils.StringUtils
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.videoPath
import com.aliee.quei.mo.utils.rxjava.RxBus
import io.reactivex.annotations.NonNull
import kotlinx.android.synthetic.main.attach_layout.*
import kotlinx.android.synthetic.main.fragment_fourth.*
import kotlinx.android.synthetic.main.fragment_longvideo.*
import kotlinx.android.synthetic.main.fragment_shop.*
import kotlinx.android.synthetic.main.fragment_shop.statuslayout
import kotlinx.android.synthetic.main.fragment_video_chlid.*
import kotlinx.android.synthetic.main.layout_common_list.*
import kotlinx.android.synthetic.main.layout_common_list.refreshLayout
import kotlinx.android.synthetic.main.recommend_attach_layout.*
import kotlinx.android.synthetic.main.video_auto_play_switch.*
import kotlinx.android.synthetic.main.video_auto_play_switch.sw_switch

@Route(path = Path.PATH_MAIN_LONG_VIDEO_FRAGMENT)
class LongVideoFragment : BaseFragment() {
    override fun scrollToTop() {
        try {
            recyclerView.smoothScrollToPosition(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private lateinit var tag: Tag
    private lateinit var mVideoView: IjkVideoView
    private lateinit var mController: VideoController
    private lateinit var mTitleView: TitleView
    private lateinit var mCompleteView: CompleteView
    private lateinit var mGestureView: GestureView
    private lateinit var mVodControlView: VodControlView
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private var mVideoInfo: VideoInfo? = null
    private var videoTips: VideoTips? = null

    private val VM = MainVideoModel()
    private val adapter = MainVideoAdapter()

    private var videos = mutableListOf<Video>()
    private var videoId: Int = 0

    /**
     * ?????????????????????
     */
    private var isAutoPlay = CommonDataProvider.instance.getAutoPlay()
    private var mCurPos = -1  //????????????????????????????????????????????????
    private var mCurrentVideoPos = -1   //??????????????????????????????
    private var mCurrentAutoPlayVideoPos = -1 //?????????????????????????????????
    private var mCurrentAddVideoPos = -1 //??????????????????????????????????????????
    private var isFirstPlay: Boolean = true  //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????false

    /**
     * ???????????????????????????????????????????????????????????????
     */
    private var mLastPos = mCurPos

    //???????????????????????????????????????
    private var confAutoDialogCount = 5
    private var showAutoDialogCount = 1

    //??????????????????????????????
    private var isShowAutoPlaySwitch = 1

    override fun getPageName(): String? = "????????????"


    companion object {
        fun newInstance(): LongVideoFragment {
            val args = Bundle()
            val fragment = LongVideoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_longvideo
    }

    override fun initView() {
        initVM()
        initRefresh()
        initRv()
        initVideoView()
        initEvent()

        attach_view.setOnClickListener {
            ARouterManager.goVideoSharedActivity(it.context)
        }
        recommend_attach_view.visibility = View.VISIBLE
        recommend_attach_view.setOnClickListener {
            ARouterManager.goVideoRecommendActivity(it.context)
        }

        search.setOnClickListener {
            ARouterManager.goSearchVideoActivity(context!!)
        }

        top.setOnClickListener {
            recyclerView.scrollToPosition(1)
        }
        Glide.with(this).asGif().load(R.mipmap.icon_tg).into(iv_attach)


        val autoAutoPlayConf = CommonDataProvider.instance.getAutoPlayCount()
        isShowAutoPlaySwitch = autoAutoPlayConf.enable
        confAutoDialogCount = autoAutoPlayConf.count

        Log.d("tag", "config:$autoAutoPlayConf")
        if (isShowAutoPlaySwitch == 1) {
            auto_play_switch.visibility = View.VISIBLE
            sw_switch.isChecked = isAutoPlay
            sw_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    CommonDataProvider.instance.saveAutoPlay(CommonDataProvider.AUTO_PLAY_OPEN)
                } else {
                    CommonDataProvider.instance.saveAutoPlay(CommonDataProvider.AUTO_PLAY_CLOSE)
                }
                isAutoPlay = isChecked
                RxBus.getInstance().post(EventAutoSwitch())
            }
        } else {
            auto_play_switch.visibility = View.GONE
        }


    }

    /**
     * ?????????????????????
     */
    private fun videoAd(videos: MutableList<Video>) {
        val adBean = AdConfig.getAd(AdEnum.VIDEO_CHILD_LIST.zid)
        if (adBean==null){
            activity!!.runOnUiThread {
                adapter.setData(tag, videos)
            }
            return
        }
        Log.d("tag", "video list:$adBean")
        adBean.also {
            AdConfig.getAdInfo(adBean, { adInfo ->
                activity!!.runOnUiThread {
                    val option = Gson().fromJson<Option>(adInfo.optionstr, Option::class.java)
                    adInfo.title = option.title
                    adInfo.desc = option.desc
                    adInfo.index = adBean.interval
                    val adVideo = Video()
                    adVideo.thumbImg = adInfo.imgurl
                    adVideo.name = adInfo.title
                    adVideo.content = adInfo.desc
                    adVideo.adCallbackUrl = adInfo.callbackurl
                    adVideo.adClickUrl = adInfo.clickurl
                    adVideo.type = MainVideoAdapter.VIEW_TYPE_VIDEO_AD
                    var index = 0
                    while (index < videos.size) {
                        if (index % (adInfo.index!! + 1) == 0) {
                            if (index != 0) {
                                videos.add(index, adVideo)
                            }
                        } else {
                            videos[index].type = MainVideoAdapter.VIEW_TYPE_VIDEO
                        }
                        index++
                    }
                    adapter.setData(tag, videos)
                    disLoading()
                }
            }, {
                activity!!.runOnUiThread {
                    adapter.setData(tag, videos)
                }
            })
        }
    }

    override fun initData() {
        tag = Tag(52, "")

        val thumb = CommonDataProvider.instance.getVideoDomain()
        if(thumb.isNotEmpty()) {
            Log.e("video", "cache domain thumb:$thumb")
            initThumbDomain(thumb)
        } else {
            VM.getVideoDomainType()
        }

        refreshVideoVipTip()
    }

    fun refreshVideoVipTip() {
        val userInfo: UserInfoBean? = CommonDataProvider.instance.getUserInfo()
       if(userInfo?.discountEndtime !=null) {
           if (userInfo.discountEndtime > System.currentTimeMillis()) {
               video_tips.visibility = View.VISIBLE
           } else {
               video_tips.visibility = View.GONE
           }
       }
    }

    private fun initRv() {
        mLinearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = mLinearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isFirstPlay) return
                    Log.d("tag", "isAutoPlay:$isAutoPlay")
                    if (isAutoPlay) {
                        autoPlayVideo(recyclerView)
                    }
                }
            }
        })

        recyclerView.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(@NonNull view: View) {}
            override fun onChildViewDetachedFromWindow(@NonNull view: View) {
                Log.d("tag", "view:${view.toString()}")
                viewStatus(view)
            }
        })
        adapter.onItemClick = { position, video ->
            mVideoView.setState(VideoView.STATE_IDLE)
            mCurrentVideoPos = position
            Log.d("tag", "??????????????????:$isAutoPlay")
            //???????????????
            //???????????????????????????????????????????????????????????????????????????????????????????????????

            //?????????????????????????????????????????????????????? ???????????????????????????????????????
            videoId = video.id!!
            if (isAutoPlay) {
                getVideoUrl(video.id)
            } else {
                if (isFirstPlay) {
                    showAutoPlayDialog(video.id)
                } else {
                    Log.d("tag", "showAutoDialogCount:$showAutoDialogCount")
                    //??????????????????????????????????????????10?????????
                    if (confAutoDialogCount == showAutoDialogCount) {
                        showAutoPlayDialog(video.id)
                        showAutoDialogCount = 1
                    } else {
                        //????????????
                        getVideoUrl(videoId)
                        showAutoDialogCount++
                    }
                }
            }
        }
        adapter.onAddVideoClick = { position, video ->
            mCurrentAddVideoPos = position
            VM.addMyVideo(video.id!!)
        }
        adapter.onItemShareClick = { position, video, thumbUrl ->
            VideoShareActivity.toThis(activity!!, video, thumbUrl)
        }
    }


    /**
     * ??????????????????????????????????????????????????????
     */
    fun viewStatus(view: View?) {
        val playerContainer = view?.findViewById<FrameLayout>(R.id.player_container)
        val v = playerContainer?.getChildAt(1)
        if (v != null && v === mVideoView && !mVideoView.isFullScreen) {
            releaseVideoView()
        }
    }

    /**
     * ????????????????????????
     */
    fun autoPlayVideo(view: RecyclerView) {
        view
        var count = view.childCount
        for (i in 0 until count) {
            val itemView = view.getChildAt(i) ?: continue
            if (itemView.tag != null) {
                val holder: MainVideoAdapter.VideoHolder = itemView.tag as MainVideoAdapter.VideoHolder
                val rect = Rect()
                holder.playerContainer.getLocalVisibleRect(rect)
                val height: Int = holder.playerContainer.height
                if (rect.top == 0 && rect.bottom == height) {
                    mCurrentAutoPlayVideoPos = holder.mPosition
                    Log.d("tag", "mCurrentAutoPlayVideoPos:${holder.mPosition}")
                    mCurrentVideoPos = holder.mPosition
                    val videoId = videos[mCurrentAutoPlayVideoPos].id!!
                    val videoName = videos[mCurrentAutoPlayVideoPos].name!!
                    Log.d("startPlay", "autoPlayVideo:videoId:${videoId},videosize:${videos.size} ${videos[mCurrentAutoPlayVideoPos].name}???mCurrentVideoPos:${mCurrentAutoPlayVideoPos}")
                    this@LongVideoFragment.videoId = videoId
                    getVideoUrl(videoId)
                    //  getVideoPreView(videoId, videoName)
                    break
                }
            }
        }
    }

    private fun initRefresh() {
        refreshLayout.setOnRefreshListener {
            getVideoList()
        }
        refreshLayout.setOnLoadMoreListener {
            VM.getLongVideoLoadMore()
        }
    }

    private fun initVideoView() {
        mVideoView = IjkVideoView(activity!!)
        mVideoView.setPlayerBackgroundColor(Color.TRANSPARENT)
        mVideoView.setOnStateChangeListener(object : VideoView.SimpleOnStateChangeListener() {

            var videoPlayTime = ""

            override fun onPlayStateChanged(playState: Int) {
                Log.d("tag,", "videoStatus:$playState")
                //??????VideoViewManager?????????????????????
                when (playState) {
                    VideoView.STATE_IDLE -> {
                        Utils.removeViewFormParent(mVideoView)
                        mLastPos = mCurPos
                        mCurPos = -1

                        Log.d("tag", "STATE_IDLE:mCurrentPosition:${mVideoView.currentPosition}")
                    }
                    VideoView.STATE_PLAYING -> {
                        //???????????????????????????
                        videoPlayTime = StringUtils.formatTime(mVideoView.duration.toInt())
                    }
                    VideoView.STATE_PLAYBACK_COMPLETED -> {
                        //????????????????????????????????????
                        VM.videoEndPlay(videoId, videoPlayTime, done = 1)
                    }
                }
            }

            override fun onPlayerStateChanged(playerState: Int) {
                super.onPlayerStateChanged(playerState)
                if (playerState == VideoView.PLAYER_FULL_SCREEN) {
                    mVideoView.setPlayerBackgroundColor(Color.BLACK)
                } else if (playerState == VideoView.PLAYER_NORMAL) {
                    mVideoView.setPlayerBackgroundColor(Color.TRANSPARENT)
                }
            }
        })

        mVideoView.setProgressManager(object : ProgressManager() {
            var videoCurrentPlayTime = ""
            override fun saveProgress(url: String?, progress: Long) {
                if (progress > 0) {
                    videoCurrentPlayTime = StringUtils.formatTime(progress.toInt())
                    VM.videoEndPlay(videoId, videoCurrentPlayTime, done = 0)
                }
            }

            override fun getSavedProgress(url: String?): Long {
                return 0
            }
        })

        mController = VideoController(activity!!)
        val mErrorView = ErrorView(activity)
        mController.addControlComponent(mErrorView)
        mCompleteView = CompleteView(activity!!)
        mGestureView = GestureView(activity!!)
        mVodControlView = VodControlView(activity!!)
        mController.addControlComponent(mCompleteView)
        mTitleView = TitleView(activity!!)
        mController.addControlComponent(mTitleView)
        mController.addControlComponent(mVodControlView)
        mController.addControlComponent(mGestureView)
        mController.setEnableOrientation(true)
        mController.setCanChangePosition(true)   //??????????????????????????????????????????
        mController.setGestureEnabled(true)  //???????????????????????????????????????????????????
        mVideoView.setVideoController(mController)
        mVideoView.commonOption()
        mVideoView.setEnableAccurateSeek(true)

        mCompleteView.registerView.click {
            ARouterManager.goRegister(activity!!)
        }

    }


    private fun getVideoUrl(videoId: Int) {
        VM.getVideoPath(videoId, "u_temp_user_0")
    }

    private fun getVideoPreView(videoId: Int, tname: String) {
        VM.getVideoPreView(videoId, tname)
    }

    /**
     * @param position   ?????????????????????
     * @param videoInfo  ????????????????????????
     * @param videoTips  ????????????????????????
     * @param isPlay    ??????????????????
     */
    private var isPreview: Boolean = true
    private fun startPlay(preViewVideoPath: String = "", position: Int, videoInfo: VideoInfo?, videoTips: VideoTips?, isPlay: Boolean) {
        if (mCurPos == position) return
        if (mCurPos != -1) {
            releaseVideoView()
        }
        val itemView: View = mLinearLayoutManager.findViewByPosition(position) ?: return
        val viewHolder = itemView.tag as MainVideoAdapter.VideoHolder
        mTitleView.setTitle(videoInfo?.name)
        mController.addControlComponent(viewHolder.prepareView, true)
        Utils.removeViewFormParent(mVideoView)
        viewHolder.playerContainer.addView(mVideoView, 1)
        VideoViewManager.instance().add(mVideoView, "list")
        Log.d("startPlay", "isPlay:$isPlay,videoInfo:${videoInfo?.video_path}")
        if (isPlay) {
            if (preViewVideoPath.isNotEmpty()) {
                isPreview = true
                mVideoView.setUrl(ApiConstants.VIDEO_API_PATH+ preViewVideoPath)
            } else {
                isPreview = false
                mVideoView.setUrl(videoInfo?.video_path?.videoPath())
                viewHolder.prepareView.setFreeCount(videoInfo!!.freetime)
            }
            viewHolder.prepareView.setPreview(isPreview)
            mCompleteView.setPreview(isPreview)
            mGestureView.setPreview(isPreview)
            mVodControlView.setPreview(isPreview)
            mVideoView.start()
            mLastPos = position
        } else {
            viewHolder.prepareView.setTipsText(videoTips?.bookbean?.bookbean.toString())
            viewHolder.prepareView.showTipsView(true)
            viewHolder.prepareView.hideFreeCountTips()
            mVideoView.onError()
        }
        mCurPos = position

        isFirstPlay = false

    }

    val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg?.what == 1) {
                val videoPreviewPath = msg.obj as String
                if (videoPreviewPath.isNotEmpty()) {
                    startPlay(videoPreviewPath, mCurrentVideoPos, null, null, true)
                }
            }
        }
    }

    private fun initVM() {
        //????????????
        VM.videoPreViewLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                    val msg = Message()
                    msg.what = 1
                    msg.obj = it.data!!.video_previewpath
                    mHandler.sendMessage(msg)
                    disLoading()
                }

                Status.Error -> {
                    if (it.code == 504) {

                    }
                }
            }
        })

        VM.videoDomainType.observeForever {
            when (it?.status) {
                Status.Complete -> {

                }
                Status.Success -> {
                    val videoThumbDomain = it.data!!.`20`[0].domain
                    initThumbDomain(videoThumbDomain)
                }
                Status.Error -> {

                }
            }
        }
        VM.videoPath.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val json = Gson().toJson(it.data!!)
                    val jsonObj = JSONObject.parseObject(json)
                    val code = jsonObj.getInteger("code")
                    val data = jsonObj.getString("data")
                    val msg = jsonObj.getString("msg")
                    Log.d("startPlay", "videoInfo:${json}")
                    if (code == 0) {
                        //????????????
                        Log.d("tag", "????????????")
                        mVideoInfo = Gson().fromJson(data, VideoInfo::class.java)
                        startPlay("", mCurrentVideoPos, mVideoInfo!!, null, true)
                        if (mVideoInfo!!.freetime.use != 0) {
                            RxBus.getInstance().post(EventToMineFreeTime())
                        }
                    } else if (code == -1) {
                        Log.d("tag", "????????????")
                        //????????????????????????????????????
                        videoTips = Gson().fromJson(data, VideoTips::class.java)
                        startPlay("", mCurrentVideoPos, null, videoTips, false)
                    }
                    // showLoadingView(false)
                    disLoading()
                }

                Status.Error -> {

                }
            }
        })

        VM.mainVideoLoadMoreLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (it.data!!.size == 0) {
                        refreshLayout.finishLoadMore()
                    }
                    val result = adapter.loadMore(it.data!!)
                    result
                }
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }
            }
        })
        VM.mainVideoLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Complete -> {
                    disLoading()
                }
                Status.Success -> {
                    refreshLayout.finishRefresh()
                    statuslayout.showContent()
                    if (it.data!!.size == 0) {
                        refreshLayout.finishLoadMore()
                    }
                    videos = it.data!!
                    adapter.setData(tag, it.data!!)

                }
                Status.Error -> {
                    /* statuslayout.showError {
                         doRetry()
                     }
                     val e = it.e
                     if (e is HttpException) {
                         MobclickAgent.onEvent(requireContext(), "error_shop")
                     }*/
                }

            }
        })

        VM.longVideoLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Complete -> {
                    disLoading()
                }
                Status.Success -> {
                    refreshLayout.finishRefresh()
                    statuslayout.showContent()
                    if (it.data!!.size == 0) {
                        refreshLayout.finishLoadMore()
                    }
                    videos = it.data!!
                    Log.d("tag", "msg-videos size- refresh-:${videos.size}")
                    //   adapter.setData(tag, it.data!!)
                    videoAd(videos)
                    /* mCurrentVideoPos = 0
                    getVideoPreView(videos[mCurrentVideoPos].id!!,videos[mCurrentVideoPos].name!!)*/
                }
                Status.Error -> {
                    /* statuslayout.showError {
                         doRetry()
                     }
                     val e = it.e
                     if (e is HttpException) {
                         MobclickAgent.onEvent(requireContext(), "error_shop")
                     }*/
                }

            }
        })

        VM.longVideoLoadMoreLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (it.data!!.size == 0) {
                        refreshLayout.finishLoadMore()
                    }
                    Log.d("tag", "msg-videos size- loadMore-:${it.data!!.size}")
                    videos.addAll(it.data!!)
                    val result = adapter.loadMore(it.data!!)
                    result
                }
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }
            }
        })

        VM.addMyVideo.observeForever {
            when (it?.status) {
                Status.Success -> {
                    if (it.data!!.code == 0) {
                        adapter.setCollectionStatus(mCurrentAddVideoPos)
                    }
                }
                Status.Error -> {

                }
            }
        }
    }

    private fun initThumbDomain(videoThumbDomain: String) {
        CommonDataProvider.instance.saveVideoThumbDomain(videoThumbDomain)
        adapter.setVideoThumbDomain(videoThumbDomain)
        getVideoList()
    }

    fun getVideoList() {
        //  VM.mainVideoList(this, "300")
        VM.getLongVideoList()
    }

    /**
     * ????????????????????????
     */
    private var builder: AlertDialog.Builder? = null
    private fun showAutoPlayDialog(videoId: Int) {
        builder = AlertDialog.Builder(activity)
                .setTitle("??????????????????")
                .setMessage("?????????????????????????????????????????????????????????????????????")
                .setNegativeButton("???") { _, _ ->
                    isAutoPlay = false
                    //??????????????????
                    CommonDataProvider.instance.saveAutoPlay(CommonDataProvider.AUTO_PLAY_CLOSE)
                    //????????????
                    getVideoUrl(videoId)
                    sw_switch.isChecked = isAutoPlay
                    RxBus.getInstance().post(EventAutoSwitch())
                }
                .setPositiveButton("???") { _, _ ->
                    isAutoPlay = true
                    //??????????????????
                    CommonDataProvider.instance.saveAutoPlay(CommonDataProvider.AUTO_PLAY_OPEN)
                    getVideoUrl(videoId)
                    sw_switch.isChecked = isAutoPlay
                    RxBus.getInstance().post(EventAutoSwitch())
                }
        builder!!.create().show()
    }

    /**
     * ??????onResume????????????super????????????????????????
     * ????????????????????????????????????onResume?????????
     */
    private fun resume() {
        if (mLastPos == -1) return
        if (mVideoInfo == null) return
        Log.d("tag", "startPlay:${mLastPos},mVideoInfo:${mVideoInfo.toString()}")
        // startPlay(mLastPos, mVideoInfo!!, null, true)
    }

    override fun onPause() {
        super.onPause()
        Log.d("tag", "onPause")
        pause()
    }

    /**
     * ??????onPause????????????super????????????????????????
     * ????????????????????????????????????onPause?????????
     */
    private fun pause() {
        releaseVideoView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pause()
    }

    private fun releaseVideoView() {
        mVideoView.release()
        if (mVideoView.isFullScreen) {
            mVideoView.stopFullScreen()
        }
        if (activity!!.requestedOrientation !== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        mCurPos = -1
    }


    override fun onResume() {
        super.onResume()
        Log.d("tag", "onResume:")
        resume()
    }

    override fun onDetach() {
        super.onDetach()
        VideoViewManager.instance().releaseByTag("list")
    }


    private fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
            when (it) {
                is EventAutoSwitch -> {
                    isAutoPlay = CommonDataProvider.instance.getAutoPlay()
                }
                is EventRechargeSuccess -> {
                    Log.d("tag", "????????????-------------")
                    refreshVideoVipTip()
                }
            }
        }, {
            it.printStackTrace()
        })
    }

}