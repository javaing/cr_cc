package com.aliee.quei.mo.router

import android.content.Context
import androidx.fragment.app.Fragment
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.aliee.quei.mo.ui.comic.fragment.CategoryFragment
import com.aliee.quei.mo.ui.comic.fragment.RankFragment
import com.aliee.quei.mo.ui.main.fragment.*
import com.aliee.quei.mo.ui.search.fragment.SearchFragment
import com.aliee.quei.mo.ui.search.fragment.SearchResultFragment
import com.aliee.quei.mo.ui.search.fragment.SearchVideoFragment
import com.aliee.quei.mo.ui.search.fragment.SearchVideoResultFragment
import com.aliee.quei.mo.ui.video.adapter.VideoGuessLikeAdapter
import com.aliee.quei.mo.ui.web.fragment.BaseWebFragment
import com.aliee.quei.mo.ui.web.fragment.BaseWebFragment2
import com.aliee.quei.mo.ui.web.fragment.BaseWebFragment3


object ARouterManager {
    const val TAB_SHOP = 0
    const val TAB_MINE_COMIC = 1
    const val TAB_ME = 2
    const val SUB_TAB_HISTORY = 0
    const val SUB_TAB_SHELF = 1

    fun goReserveActivity(context: Context, url: String? = null, showPage: Int = 0, showTab: Int = 0) {
        ARouter.getInstance().build(Path.PATH_RESERVE_ACTIVITY)
                .navigation(context)
    }

    fun goContentActivity(context: Context, url: String? = null, showPage: Int = 0, showTab: Int = 0) {
        ARouter.getInstance().build(Path.PATH_CONTENT_ACTIVITY)
                .withInt("showPage", showPage)
                .withString("url", url)
                .withInt("showTab", showTab)
                .navigation(context)
    }

    fun goCHANNEL1Activity(context: Context, url: String? = null, showPage: Int = 0, showTab: Int = 0) {
        ARouter.getInstance().build(Path.PATH_CHANNEL1_ACTIVITY)
                .withInt("showPage", showPage)
                .withString("url", url)
                .withInt("showTab", showTab)
                .navigation(context)
    }

//    fun goMainActivity(context: Context, url: String? = null, showPage: Int = 0, showTab: Int = 0) {
//        ARouter.getInstance().build(Path.PATH_MAIN_ACTIVITY)
//                .withInt("showPage", showPage)
//                .withString("url", url)
//                .withInt("showTab", showTab)
//                .navigation(context)
//    }

    fun goLaunchActivity(context: Context, url: String? = null) {
        ARouter.getInstance().build(Path.PATH_LAUNCH_ACTIVITY)
                .withString("url", url)
                .navigation(context)
    }

    fun goLoginActivity(context: Context, phone: String = "", url: String = "") {
        ARouter.getInstance().build(Path.PATH_LOGIN)
                .withString("phone", phone)
                .withString("url", url)
                .navigation(context)
    }

    fun goRegister(context: Context, phone: String = "", url: String = "") {
        ARouter.getInstance().build(Path.PATH_REGISTER)
                .withString("phone", phone)
                .withString("url", url)
                .navigation(context)
    }

    fun goReset(context: Context) {
        ARouter.getInstance().build(Path.PATH_RESET)
                .navigation(context)
    }

    fun goSearch(context: Context, keyword: String? = "") {
        ARouter.getInstance().build(Path.PATH_SEARCH)
                .withString("keyword", keyword)
                .navigation(context)
    }

    fun goVideoSearch(context: Context, keyword: String? = "") {
        ARouter.getInstance().build(Path.PATH_VIDEO_SEARCH)
                .withString("keyword", keyword)
                .navigation(context)
    }

    /**
     * @param rand 是否随机跳转
     * @param rid 需要随机跳转时  传入书籍推荐位的id
     */
    fun goComicDetailActivity(context: Context, bookid: Int, rand: Boolean = true, rid: String = "17") {
        ARouter.getInstance().build(Path.PATH_COMIC_DETAIL)
                .withInt("bookid", bookid)
                .withBoolean("rand", rand)
                .withString("rid", rid)
                .navigation(context)
    }


    ///////////////////////////////////////////////////////////fragments/////////////////////////////////////////////////////////////////////////
    fun getMyComicFragment(context: Context, showTab: Int): MineComicFragment {
        return ARouter.getInstance().build(Path.PATH_MAIN_FIRST_FRAGMENT)
                .withInt("showTab", showTab)
                .navigation(context) as MineComicFragment
    }

    fun getSearchFragment(context: Context): SearchFragment {
        return ARouter.getInstance().build(Path.PATH_SEARCH_FRAGMENT)
                .navigation(context) as SearchFragment
    }

    fun getSearchResultFragment(context: Context): SearchResultFragment {
        return ARouter.getInstance().build(Path.PATH_SEARCH_RESULT_FRAGMENT)
                .navigation(context) as SearchResultFragment
    }

    fun getSearchVideoFragment(context: Context): SearchVideoFragment {
        return ARouter.getInstance().build(Path.PATH_VIDEO_SEARCH_VIDEO_FRAGMENT)
                .navigation(context) as SearchVideoFragment
    }

    fun getSearchVideoResultFragment(context: Context): SearchVideoResultFragment {
        return ARouter.getInstance().build(Path.PATH_VIDEO_SEARCH_VIDEO_RESULT_FRAGMENT)
                .navigation(context) as SearchVideoResultFragment
    }

    fun getShopFragment(context: Context): ShopFragment {
        return ARouter.getInstance().build(Path.PATH_MAIN_SHOP)
                .navigation(context) as ShopFragment
    }

    fun getVideoWebFragment(context: Context): BaseWebFragment3 {
        return ARouter.getInstance().build(Path.PATH_WEB_FRAGMENT3)
                .navigation(context) as BaseWebFragment3
    }

    fun getVideoFragment(context: Context): VideoFragment {
        return ARouter.getInstance().build(Path.PATH_MAIN_VIDEO_FRAGMENT)
                .navigation(context) as VideoFragment
    }

    fun getLongVideoFragment(context: Context): LongVideoFragment {
        return ARouter.getInstance().build(Path.PATH_MAIN_LONG_VIDEO_FRAGMENT)
                .navigation(context) as LongVideoFragment
    }

//    fun getNewShopFragment(context: Context, showTab: Int): NewShopFragment {
//        return ARouter.getInstance().build(Path.PATH_NEW_SHOP_FRAGMENT)
//                .withInt("showTab", showTab)
//                .navigation(context) as NewShopFragment
//    }

    fun goComicCatalogActivity(context: Context, id: Int?) {
        id ?: return
        ARouter.getInstance().build(Path.PATH_COMIC_CATALOG)
                .withInt("bookid", id)
                .navigation(context)
    }

    fun goComicCategoryActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_COMIC_CATEGORY)
                .navigation(context)
    }

    fun goReadActivity(
            context: Context,
            bookid: Int,
            chapterId: Int,
            chapterPosition: Int = 0,
            isHome: Boolean = false
    ) {
        ARouter.getInstance().build(Path.PATH_COMIC_READ)
                .withInt("bookid", bookid)
                .withInt("chapterId", chapterId)
                .withInt("chapterPosition", chapterPosition)
                .withBoolean("isHome", isHome)
                .navigation(context)
    }

    fun getCategoryFragment(context: Context, tid: Int, status: Int, sex: Int): CategoryFragment {
        return ARouter.getInstance().build(Path.PATH_COMIC_CATEGORY_FRAGMENT)
                .withInt("tid", tid)
                .withInt("status", status)
                .withInt("sex", sex)
                .navigation(context) as CategoryFragment
    }

    fun goRankActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_RANK_ACTIVITY)
                .navigation(context)
    }

    fun goRecommendActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_USER_RECOMMEND)
                .navigation(context)
    }

    fun goRecommendBonusActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_USER_RECOMMEND_BONUS)
                .navigation(context)
    }


    fun goBulletinActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_BULLETIN_ACTIVITY)
                .navigation(context)
    }

    fun goBulletinDetailActivity(context: Context, bulletinid: Int) {
        ARouter.getInstance().build(Path.PATH_BULLETIN_DETAIL_ACTIVITY)
                .withInt("bulletinid", bulletinid)
                .navigation(context)
    }

    fun goLimitFreeActivity(context: Context) {

    }

    fun goMoreActivity(context: Context, title: String, rid: String) {
        ARouter.getInstance().build(Path.PATH_COMIC_MORE)
                .withString("title", title)
                .withString("rid", rid)
                .navigation(context)
    }

    fun goWebActivity(context: Context, url: String, title: String, referer: String) {
        ARouter.getInstance().build(Path.PATH_WEB_ACTIVITY)
                .withString("url", url)
                .withString("showTitle", title)
                .withString("referer", referer)
                .navigation(context)
    }

    fun goWebActivity2(context: Context, url: String, title: String, referer: String) {
        ARouter.getInstance().build(Path.PATH_WEB_ACTIVITY2)
                .withString("url", url)
                .withString("showTitle", title)
                .withString("referer", referer)
                .navigation(context)
    }


    fun getWebFragment(
            context: Context,
            url: String,
            showTitle: Boolean,
            title: String = "",
            pageName: String = "",
            referer: String = ""
    ): Fragment {
        return ARouter.getInstance().build(Path.PATH_WEB_FRAGMENT)
                .withString("url", url)
                .withString("pageName", pageName)
                .withBoolean("showTitle", showTitle)
                .withString("title", title)
                .withString("referer", referer)
                .navigation(context) as BaseWebFragment
    }

    fun getWebFragment2(
            context: Context,
            url: String,
            showTitle: Boolean,
            title: String = "",
            pageName: String = "",
            referer: String = ""
    ): Fragment {
        return ARouter.getInstance().build(Path.PATH_WEB_FRAGMENT2)
                .withString("url", url)
                .withString("pageName", pageName)
                .withBoolean("showTitle", showTitle)
                .withString("title", title)
                .withString("referer", referer)
                .navigation(context) as BaseWebFragment2
    }

    fun goBillActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_USER_BILL)
                .navigation(context)
    }

    fun goH5PayResultActivity(context: Context, tradeNo: String, successUrl: String = "") {
        ARouter.getInstance().build(Path.PATH_H5_PAY_RESULT)
                .withString("tradeNo", tradeNo)
                .withString("successUrl", successUrl)
                .navigation(context)
    }

    /**
     * @param isBook  false ： 视频，  true：书城
     */
    fun goRechargeActivity(context: Context, successUrl: String = "", bookid: Int, isBook: Boolean = true) {
        Log.d("goRechargeActivity", "isBook:$isBook")
        ARouter.getInstance().build(Path.PATH_USER_RECHARGE)
                .withString("successUrl", successUrl)
                .withInt("bookid", if (isBook) -1 else bookid)
                .withBoolean("isBook", isBook)
                .navigation(context)
    }

    fun goReadFinishActivity(context: Context, bookName: String, bookStatus: Int) {
        ARouter.getInstance().build(Path.PATH_READ_FINISH)
                .withString("bookName", bookName)
                .withInt("bookStatus", bookStatus)
                .navigation(context)
    }

    fun getRankingFragment(context: Context, rid: String): RankFragment {
        return ARouter.getInstance().build(Path.PATH_RANK_FRAGMENT)
                .withString("rid", rid)
                .navigation(context) as RankFragment
    }

    fun goExchangeActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_USER_EXCHANGE)
                .navigation(context)
    }

    fun goRecoverUserActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_USER_RECOVER)
                .navigation(context)
    }

    fun goCustomServiceActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_USER_CUSTOMERSERVICE)
                .navigation(context)
    }

    fun goDailyLoginActivity(context: Context, coins: Int) {
        ARouter.getInstance().build(Path.PATH_DAILY_LOGIN)
                .withInt("coins", coins)
                .navigation(context)
    }


    fun goTicketActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_TICKET_LIST)
                .navigation(context)
    }

    fun getMineFragment(context: Context): MineFragment {
        return ARouter.getInstance().build(Path.PATH_MAIN_MINE_FRAGMENT)
                .navigation(context) as MineFragment
    }

    fun getWelfareFragment(context: Context): WelfareFragment {
        return ARouter.getInstance().build(Path.PATH_MAIN_WELFARE)
                .navigation(context) as WelfareFragment
    }

    fun goWelfareCoinListActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_WELFARE_COIN_LIST)
                .navigation(context)
    }

    fun goVideoInfoActivity(context: Context, result: String) {
        ARouter.getInstance().build(Path.PATH_VIDEO_PLAYER)
                .withString("videoInfoJson", result)
                .navigation(context)
    }

    fun goVideoRankingActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_VIDEO_RANKING)
                .navigation(context)
    }

    fun goVideoRecommendActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_VIDEO_RECOMMEND)
                .navigation(context)
    }

    fun goSearchVideoActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_VIDEO_SEARCH)
                .navigation(context)
    }

    fun goVideoSharedActivity(context: Context) {
        ARouter.getInstance().build(Path.PATH_VIDEO_SHARE)
                .navigation(context)
    }

}