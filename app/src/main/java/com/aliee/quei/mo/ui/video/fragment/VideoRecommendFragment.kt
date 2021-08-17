package com.aliee.quei.mo.ui.video.fragment

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.ui.main.vm.MainVideoModel
import com.aliee.quei.mo.ui.video.RecycleGridDivider
import com.aliee.quei.mo.ui.video.adapter.RecommendAdapter
import com.aliee.quei.mo.utils.extention.toast
import com.aliee.quei.mo.widget.ConfirmDialog
import kotlinx.android.synthetic.main.fragment_shop.statuslayout
import kotlinx.android.synthetic.main.layout_common_list.*

class VideoRecommendFragment : BaseFragment() {

    private var VM = MainVideoModel()
    private var adapter = RecommendAdapter()
    private var position: Int = 0
    private var videoId: Int = 0
    override fun getPageName(): String? {
        return "记录"
    }

    companion object {
        fun newInstance(position: Int): VideoRecommendFragment {
            val args = Bundle()
            args.putInt("position", position)
            val fragment = VideoRecommendFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_vidoe_rank
    }

    override fun initView() {
        initRv()
        initVM()
        initRefresh()
    }
    private fun initRefresh() {
        refreshLayout.setOnRefreshListener {
            initData()
        }
        refreshLayout.setOnLoadMoreListener {
            loadMore()
        }
    }
    override fun initData() {
        position = arguments!!.getInt("position")
        when (position) {
            0 -> {
                VM.getMyVideo()
            }
            1 -> {
                VM.videoRecommend()
            }
        }
    }

    fun loadMore(){
        when(position){
            0->{
                VM.getLoadMoreMyVideo()
            }
            1->{
                VM.videoRecommendLoadMore()
            }
        }
    }

    private fun initRv() {
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(RecycleGridDivider())
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        adapter.removeClick = {
            val bean = it
            activity?.let {
                val confirm = ConfirmDialog.newInstance(getString(R.string.dialog_notice), getString(R.string.dialog_confirm_del))
                confirm.confirmClick = {
                    videoId = bean.id!!
                    if (position == 0) {
                        VM.delMyVideo(bean.id)
                    } else {
                        VM.delVideoRecommend(bean.id)
                    }
                }
                confirm.show(childFragmentManager, confirm.javaClass.name)
            }

        }

        adapter.onItemClick = {
            val videoInfoJson = Gson().toJson(it)
            ARouterManager.goVideoInfoActivity(activity!!, videoInfoJson)
        }
    }

    private fun initVM() {
        VM.videoRecommendLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Complete -> {
                    disLoading()
                }
                Status.Success -> {
                    refreshLayout.finishRefresh()
                    statuslayout.showContent()
                    if (it.data!!.size == 0){
                        statuslayout.showEmpty()
                    }
                    adapter.setData(it.data!!)
                }
                Status.Empty -> {
                    statuslayout.showEmpty()
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
        VM.videoRecommendLoadMoreLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }
                Status.Success -> {
                    refreshLayout.finishRefresh()
                    statuslayout.showContent()
                    if (it.data!!.size == 0){
                        refreshLayout.finishLoadMore()
                    }
                    adapter.loadMore(it.data!!)
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

        VM.myVideoListLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Complete -> {
                    disLoading()
                }
                Status.Success -> {
                    refreshLayout.finishRefresh()
                    statuslayout.showContent()
                    val data = it.data!!
                    if (data.size == 0) {
                        statuslayout.showEmpty()
                    }
                    adapter.setData(it.data!!)
                }
                Status.Empty -> {
                    statuslayout.showEmpty()
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
        VM.myVideoListLoadMoreLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }
                Status.Success -> {
                    if (it.data!!.size == 0){
                        refreshLayout.finishLoadMore()
                    }
                    adapter.loadMore(it.data!!)
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
        VM.delVideoRecommendLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    toast("delVideoRecommendLiveData:$videoId")
                    adapter.removeItem(videoId)
                    if (adapter.itemCount == 0) {
                        statuslayout.showEmpty()
                    }
                }
                else -> {

                }
            }
        })

        VM.delMyVideoLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    toast("delMyVideoLiveData:$videoId")
                    adapter.removeItem(videoId)
                    if (adapter.itemCount == 0) {
                        statuslayout.showEmpty()
                    }
                }
                else -> {

                }
            }
        })
    }

    /**
     * 是否为编辑状态
     *
     * @param isEdit
     */
    private var isEdit: Boolean = false

    fun setEdit(isEdit: Boolean) {
        this.isEdit = isEdit
        adapter.setIsEdit(isEdit)
    }
}