package com.aliee.quei.mo.ui.video.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.lifecycle.Observer
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.alibaba.fastjson.JSONObject
import com.dueeeke.videocontroller.component.TitleView
import com.dueeeke.videoplayer.player.VideoView
import com.dueeeke.videoplayer.player.VideoViewManager
import com.google.gson.Gson
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.EventToMineFreeTime
import com.aliee.quei.mo.data.bean.Video
import com.aliee.quei.mo.data.bean.VideoInfo
import com.aliee.quei.mo.data.bean.VideoTips
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.ui.main.vm.MainVideoModel
import com.aliee.quei.mo.ui.video.Utils
import com.aliee.quei.mo.ui.video.VideoShareActivity
import com.aliee.quei.mo.ui.video.adapter.VideoListAdapter
import com.aliee.quei.mo.ui.video.controller.VideoController
import com.aliee.quei.mo.ui.video.view.*
import com.aliee.quei.mo.utils.StringUtils
import com.aliee.quei.mo.utils.TagCountManager
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.videoPath
import com.aliee.quei.mo.utils.rxjava.RxBus
import io.reactivex.annotations.NonNull
import kotlinx.android.synthetic.main.fragment_shop.*
import kotlinx.android.synthetic.main.layout_common_list.*

class VideoRankFragment : BaseFragment() {

    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private lateinit var mVideoView:IjkVideoView
    private lateinit var mController: VideoController
    private lateinit var mTitleView: TitleView
    private var mVideoInfo: VideoInfo? = null
    private var videoTips: VideoTips? = null
    private var VM = MainVideoModel()
    private var adapter = VideoListAdapter()
    private var videos = mutableListOf<Video>()
    private var videoId: Int = 0

    private var position: Int = 0
    private var tag: Int = 0

    /**
     * ?????????????????????
     */
    private var isAutoPlay = CommonDataProvider.instance.getAutoPlay()
    private var mCurPos = -1  //????????????????????????????????????????????????
    private var mCurrentVideoPos = -1   //??????????????????????????????
    private var mCurrentAddVideoPos = -1 //??????????????????????????????????????????
    private var isFirstPlay: Boolean = true  //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????false


    /**
     * ???????????????????????????????????????????????????????????????
     */
    private var mLastPos = mCurPos
    override fun getPageName(): String? {
        return "????????????"
    }

    companion object {
        fun newInstance(position: Int): VideoRankFragment {
            val args = Bundle()
            args.putInt("position", position)
            val fragment = VideoRankFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        position = arguments!!.getInt("position")
        //??????????????????????????? tag = 50??? ?????? 25
        //todo
        tag = if (position == 0) 50 else 25
        Log.d("tag", "position:${position}")
        return R.layout.fragment_vidoe_rank
    }

    override fun initView() {
        initRv()
        initVM()
        initRefresh()
        initVideoView()
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    fun viewStatus(view: View?) {
        val playerContainer = view?.findViewById<FrameLayout>(R.id.player_container)
        val v = playerContainer?.getChildAt(0)
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
                val holder: VideoListAdapter.VideoHolder = itemView.tag as VideoListAdapter.VideoHolder
                val rect = Rect()
                holder.playerContainer.getLocalVisibleRect(rect)
                val height: Int = holder.playerContainer.height
                if (rect.top == 0 && rect.bottom == height) {
                    mCurrentVideoPos = holder.mPosition
                    var videoId = videos[mCurrentVideoPos].id!!
                    Log.d("startPlay", "autoPlayVideo:${videos[mCurrentVideoPos].name}???mCurrentVideoPos:${mCurrentVideoPos}")
                    getVideoUrl(videoId)
                    this@VideoRankFragment.videoId = videoId
                    break
                }
            }
        }
    }

    private fun initRefresh() {
        refreshLayout.setOnRefreshListener {
            VM.videoRankingList(tag = tag)
        }
        refreshLayout.setOnLoadMoreListener {
            VM.videoRankingLoadMoreList(tag = tag)
        }
    }


    private fun initRv() {
        mLinearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = mLinearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

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
                Log.d("tag", "view:$view")
                viewStatus(view)
            }
        })
        adapter.onItemClick = { position, video ->
            mCurrentVideoPos = position
            Log.d("tag", "??????????????????:$isAutoPlay")
            //???????????????
            //???????????????????????????????????????????????????????????????????????????????????????????????????
            //?????????????????????????????????????????????????????? ???????????????????????????????????????
            /* if (mCurPos != -1) {
                 val itemView: View = mLinearLayoutManager.findViewByPosition(position)
                 val viewHolder = itemView.tag as MainVideoAdapter.VideoHolder
                 viewHolder?.prepareView?.hideTipsView()
             }*/

            videoId = video.id!!
            if (isAutoPlay) {
                getVideoUrl(video.id)
            } else {
                showAutoPlayDialog(video.id)
            }

            //??????tag??????
            TagCountManager.saveMoreTagCount(video.tags)
        }
        adapter.onAddVideoClick = { position, video ->
            mCurrentAddVideoPos = position
            VM.addMyVideo(video.id!!)
        }
        adapter.onItemShareClick = { position, video, thumbUrl ->
            VideoShareActivity.toThis(activity!!, video, thumbUrl)
        }
    }

    private fun initVideoView() {
        mVideoView = IjkVideoView(activity!!)
        mVideoView.setPlayerBackgroundColor(Color.TRANSPARENT)

        mVideoView.setOnStateChangeListener(object : VideoView.SimpleOnStateChangeListener() {
            var videoPlayTime = ""
            override fun onPlayStateChanged(playState: Int) {
                //??????VideoViewManager?????????????????????
                when (playState) {
                    VideoView.STATE_IDLE -> {
                        Utils.removeViewFormParent(mVideoView)
                        mLastPos = mCurPos
                        mCurPos = -1
                    }
                    VideoView.STATE_PLAYING -> {
                        videoPlayTime = StringUtils.formatTime(mVideoView.duration.toInt())
                        Log.d("tag", "STATE_PLAYING:videoTime:$videoPlayTime")
                    }
                    VideoView.STATE_PLAYBACK_COMPLETED -> {
                        Log.d("tag", "STATE_PLAYBACK_COMPLETED:videoTime:${mVideoView.duration}")
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
        mController = VideoController(activity!!)
        val mErrorView = ErrorView(activity)
        mController.addControlComponent(mErrorView)
        val mCompleteView = CompleteView(activity!!)
        mController.addControlComponent(mCompleteView)
        mTitleView = TitleView(activity!!)
        mController.addControlComponent(mTitleView)
        mController.addControlComponent(VodControlView(activity!!))
        mController.addControlComponent(GestureView(activity!!))
        mController.setEnableOrientation(true)
        mController.setCanChangePosition(true)   //??????????????????????????????????????????
        mController.setGestureEnabled(true)  //???????????????????????????????????????????????????
        mVideoView.setVideoController(mController)
        mVideoView.commonOption()
        mCompleteView.registerView.click {
            ARouterManager.goRegister(activity!!)
        }
    }

    /**
     * @param position   ?????????????????????
     * @param videoInfo  ????????????????????????
     * @param videoTips  ????????????????????????
     * @param isPlay    ??????????????????
     */
    private fun startPlay(position: Int, videoInfo: VideoInfo?, videoTips: VideoTips?, isPlay: Boolean) {
        if (mCurPos == position) return
        if (mCurPos != -1) {
            releaseVideoView()
        }
        val itemView: View = mLinearLayoutManager.findViewByPosition(position) ?: return
        val viewHolder = itemView.tag as VideoListAdapter.VideoHolder
        mTitleView.setTitle(videoInfo?.name)
        mController.addControlComponent(viewHolder.prepareView, true)
        Utils.removeViewFormParent(mVideoView)
        viewHolder.playerContainer.addView(mVideoView, 1)
        VideoViewManager.instance().add(mVideoView, "list")
        if (isPlay) {
            mVideoView.setUrl(videoInfo?.video_path?.videoPath())
            mVideoView.start()

        } else {
            viewHolder.prepareView.setTipsText(videoTips?.bookbean?.bookbean.toString())
            viewHolder.prepareView.showTipsView(true)
            mVideoView.onError()
        }
        mCurPos = position
        mLastPos = position
        isFirstPlay = false
    }

    private fun getVideoUrl(videoId: Int) {
        VM.getVideoPath(videoId, "u_temp_user_0")
    }

    override fun initData() {
        VM.videoRankingList(tag = tag)
        VM.analyticsVideoTag(tag)
    }

    fun initVM() {
        VM.videoRankingLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> {
                    refreshLayout.setNoMoreData(false)
                }
                Status.Success -> {
                    videos = it.data!!
                    if (videos.size == 0) {
                        statuslayout.showEmpty()
                    } else {
                        adapter.setData(it.data!!)
                        statuslayout.showContent()
                    }
                }
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }
                Status.NoMore -> {
                    refreshLayout.setNoMoreData(true)
                    statuslayout.showContent()
                }
            }
        })
        VM.videoRankingLoadMoreLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> {
                    refreshLayout.setNoMoreData(false)
                }
                Status.Success -> {
                    adapter.loadMore(it.data!!)
                    videos = adapter.getData()
                    statuslayout.showContent()
                }
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }
                Status.NoMore -> {
                    refreshLayout.finishLoadMoreWithNoMoreData()
                }
            }
        })

        VM.videoPath.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val json = Gson().toJson(it.data!!)
                    val jsonObj = JSONObject.parseObject(json)
                    val code = jsonObj.getInteger("code")
                    val data = jsonObj.getString("data")
                    val msg = jsonObj.getString("msg")

                    if (code == 0) {
                        //????????????
                        Log.d("tag", "????????????")
                        mVideoInfo = Gson().fromJson(data, VideoInfo::class.java)
                        if (mVideoInfo!!.freetime.use != 0) {
                            RxBus.getInstance().post(EventToMineFreeTime())
                        }
                        startPlay(mCurrentVideoPos, mVideoInfo!!, null, true)

                    } else if (code == -1) {
                        Log.d("tag", "????????????")
                        //????????????????????????????????????
                        videoTips = Gson().fromJson(data, VideoTips::class.java)
                        startPlay(mCurrentVideoPos, null, videoTips, false)
                    }
                    // showLoadingView(false)
                    disLoading()
                }

                Status.Error -> {

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

    /**
     * ????????????????????????
     */
    private var builder: AlertDialog.Builder? = null
    private fun showAutoPlayDialog(videoId: Int) {
        builder = AlertDialog.Builder(activity)
                .setTitle("??????????????????")
                .setMessage("????????????????????????????????????????????????????????????????????????")
                .setNegativeButton("???") { _, _ ->
                    isAutoPlay = false
                    //??????????????????
                    CommonDataProvider.instance.saveAutoPlay(CommonDataProvider.AUTO_PLAY_CLOSE)
                    //????????????
                    getVideoUrl(videoId)
                }
                .setPositiveButton("???") { _, _ ->
                    isAutoPlay = true
                    //??????????????????
                    CommonDataProvider.instance.saveAutoPlay(CommonDataProvider.AUTO_PLAY_OPEN)
                    getVideoUrl(videoId)
                }
        builder!!.create().show()
    }

    /**
     * ??????onResume????????????super????????????????????????
     * ????????????????????????????????????onResume?????????
     */
    private fun resume() {
        if (mLastPos == -1) return

        startPlay(mLastPos, mVideoInfo!!, null, true)
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

    @SuppressLint("SourceLockedOrientationActivity")
    private fun releaseVideoView() {
        Log.d("tag", "releaseVideoView:")
        mVideoView.release()
        if (mVideoView.isFullScreen) {
            mVideoView.stopFullScreen()
        }
        if (activity!!.requestedOrientation !== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        mCurPos = -1
    }

}