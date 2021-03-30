package com.aliee.quei.mo.component

import android.text.TextUtils
import android.util.Log
import com.google.gson.reflect.TypeToken
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.cache.CacheManager
import com.aliee.quei.mo.cache.DBCacheManager
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.utils.MD5
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.google.gson.Gson
import java.util.*

/**
 * @Author: YangYang
 * @Date: 2018/1/11
 * @Version: 1.0.0
 * @Description:
 */
class CommonDataProvider private constructor() {

    companion object {
        //cache key
        private val CACHE_KEY_USER_INFO = "cache.key.userinfo"
        private val CACHE_KEY_TOKEN = "cache.key.token"
        private val CACHE_KEY_REGISTER_TOKEN = "cache.key.register_token"
        private val CACHE_KEY_PUSH_TOKEN = "cache.key.push.token"
        private val CACHE_KEY_LAUNCH_AD_BEAN = "cache.key.launch.ad"
        private val CACHE_KEY_FIRST_PAGE_POPUP = "cache.key.firstpage.popup"
        private val CACHE_KEY_WEB_WHITE_LIST = "cache.key.web.white.list"
        private val CACHE_KEY_HAS_LOGIN = "cache.key.login"
        private val CACHE_KEY_HISTORY_ID = "cache.key.history.ids"
        private val CACHE_KEY_LAST_RECORD = "cache.key.history.last"
        private val CACHE_KEY_API_DOMAIN = "cache.key.api.domain"
        private val CACHE_KEY_IMG_DOMAIN = "cache.key.img.domain"
        private val CACHE_KEY_VIDEO_DOMAIN = "cache.key.video.domain"
        private val CACHE_KEY_VIDEO_THUMB_DOMAIN = "cache.key.video.thumb.domain"
        private val CACHE_KEY_VIDEO_TAGS = "cache.key.video.tags"
        private val CACHE_KEY_VIDEO_TAGS_SCORE = "cache.key.video.tags.score"
        private val CACHE_KEY_VIDEO_AUTO_PALY = "cache.key.video.auto.play"
        private val CACHE_KEY_VIDEO_USER_TYPE = "cache.key.video.user.type"
        private val CACHE_KEY_VIDEO_USER_TYPE_INT = "cache.key.video.user.type.int"
        private val CACHE_KEY_VIDEO_USER_FREE_TIME = "cache.key.video.user.freetime"
        private val CACHE_KEY_IS_SHARE_LINKID = "cache.key.is.share.linkid"
        private val CACHE_KEY_BULLETIN_SHOW = "cache.key.bulletin_show"
        private val CACHE_KEY_BULLETIN_SHOW_DAY = "cache.key.bulletin_show_day"
        private val CACHE_KEY_AUTO_PLAY_COUNT = "cache.key.auto_play_count"
        private val CACHE_KEY_AD_LIST = "cache.key.ad.list"

        val AUTO_PLAY_CLOSE = "close"
        val AUTO_PLAY_OPEN = "open"

        val instance: CommonDataProvider by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CommonDataProvider()
        }
    }

    object PageConfig {
        var mainTabs = getDefaultMainTab()
        private fun getDefaultMainTab(): List<MainTabBean> {
            return listOf<MainTabBean>(MainTabBean(1, ReaderApplication.instance.getString(R.string.tab_history), "", "", "", 1),
//                    MainTabBean(2,"推荐","","","",2),
                    MainTabBean(3, ReaderApplication.instance.getString(R.string.tab_home), "", "", "", 3),
                    MainTabBean(4, ReaderApplication.instance.getString(R.string.tab_mine), "", "", "", 4))
        }
    }

    private var token: String = ""

    private var pushToken: String = ""

    private val cacheManager: CacheManager = DBCacheManager()
    private var userInfo: UserInfoBean? = null

    fun getRegisterToken(): String {
        return cacheManager.getValueByKey(CACHE_KEY_REGISTER_TOKEN)
    }

    fun setRegisterToken(str: String) {
        cacheManager.saveCache(CACHE_KEY_REGISTER_TOKEN, str)
    }

    fun getToken(): String {
        if (token.isNotEmpty()) {
            return token
        }
        token = cacheManager.getValueByKey(CACHE_KEY_TOKEN)
        if (token.isNullOrEmpty()) {
            token = MD5.md5(UUID.randomUUID().toString())
            cacheManager.saveCache(CACHE_KEY_TOKEN, token)
        }
        return token
    }

    fun setToken(token: String) {
        this.token = token
        cacheManager.saveCache(CACHE_KEY_TOKEN, token)
    }

    /**
     * 推送的token
     */
    fun setPushToken(token: String?) {
        pushToken = token ?: ""
        cacheManager.saveCache(CACHE_KEY_PUSH_TOKEN, pushToken)
    }

    fun getUserInfo(): UserInfoBean? {
        if (userInfo == null) {
            val cache = cacheManager.getValueByKey(CACHE_KEY_USER_INFO)
            if (!cache.isBlank()) {
                userInfo = GsonProvider.gson.fromJson(cache, UserInfoBean::class.java)
            }
        }

        if (userInfo == null) {
            RxBus.getInstance().post(com.aliee.quei.mo.component.EventUserInfoUpdated())
        }
        return userInfo
    }

    fun saveUserInfo(bean: UserInfoBean?) {
        userInfo = bean
        Log.d("tag", "saveUserInfo:${bean.toString()}")
        if (bean == null) {
            cacheManager.saveCache(CACHE_KEY_USER_INFO, "")
        } else {
            cacheManager.saveCache(CACHE_KEY_USER_INFO, GsonProvider.gson.toJson(bean))
            RxBus.getInstance().post(bean)
        }
    }


    fun saveFreeTime(freeTime: String) {
        cacheManager.saveCache(CACHE_KEY_VIDEO_USER_FREE_TIME, freeTime)
    }

    fun getFreeTime(): String {
        return cacheManager.getValueByKey(CACHE_KEY_VIDEO_USER_FREE_TIME)
    }
    //是否点过赞


    fun clearUserData() {
        userInfo = null
        saveUserInfo(null)

        token = ""
    }


    /**
     * 用户的Vip是否有效
     */
    fun isUserVipValid(): Boolean {
        val userInfoBean = CommonDataProvider.instance.getUserInfo()
        userInfoBean ?: return false
        return userInfoBean.vipEndtime ?: 0 > System.currentTimeMillis()
    }

    fun isVip(): Int {
        val userInfoBean = CommonDataProvider.instance.getUserInfo()
        return userInfoBean!!.vip
    }

    private var launchAdBean: LaunchAdBean? = null

    fun getAdConfig(): LaunchAdBean? {
        if (launchAdBean == null) {
            val s = cacheManager.getValueByKey(CACHE_KEY_LAUNCH_AD_BEAN)
            if (!TextUtils.isEmpty(s)) {
                try {
                    launchAdBean = GsonProvider.gson.fromJson(s, LaunchAdBean::class.java)
                } catch (e: Exception) {

                }
            }
        }
        return launchAdBean
    }

    var fisrtPagePopup: PopupWindowBean? = null
    fun getFirstPagePopupConfig(): PopupWindowBean? {
        val s = cacheManager.getValueByKey(CACHE_KEY_FIRST_PAGE_POPUP)
        if (!TextUtils.isEmpty(s)) {
            try {
                fisrtPagePopup = GsonProvider.gson.fromJson(s, PopupWindowBean::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return fisrtPagePopup
    }

    private var mWhiteList: List<String>? = null

    fun getSchemeWhiteList(): List<String>? {
        val s = cacheManager.getValueByKey(CACHE_KEY_WEB_WHITE_LIST)
        var typeToken = object : TypeToken<List<String>>() {}.type
        if (mWhiteList != null) return mWhiteList
        try {
            mWhiteList = GsonProvider.gson.fromJson(s, typeToken)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mWhiteList
    }

    private var isLogin: Boolean? = null

    fun hasLogin(): Boolean {
        if (isLogin == null) {
            isLogin = SharedPreUtils.getInstance().getBoolean(CACHE_KEY_HAS_LOGIN, false)
        }
        return isLogin == true
    }

    fun setHasLogin(hasLogin: Boolean) {
        this.isLogin = hasLogin
        SharedPreUtils.getInstance().putBoolean(CACHE_KEY_HAS_LOGIN, hasLogin)
    }

    fun saveHistory(historyIds: String) {
        SharedPreUtils.getInstance().putString(CACHE_KEY_HISTORY_ID, historyIds)
    }

    fun getHistoryBookIds(): MutableList<String> {
        val ids = SharedPreUtils.getInstance().getString(CACHE_KEY_HISTORY_ID)
        if (ids.isNullOrEmpty()) return mutableListOf()
        return ids.split(",").toMutableList()
    }

    fun saveLastRead(bean: LocalRecordBean) {
        cacheManager.saveCache(CACHE_KEY_LAST_RECORD, GsonProvider.gson.toJson(bean))
    }

    fun getLastRead(): LocalRecordBean? {
        val s = cacheManager.getValueByKey(CACHE_KEY_LAST_RECORD)
        if (s.isNullOrEmpty()) return null
        try {
            return GsonProvider.gson.fromJson(s, LocalRecordBean::class.java)
        } catch (e: java.lang.Exception) {
            return null
        }
    }

    fun getApiDomain(): String {
        return cacheManager.getValueByKey(CACHE_KEY_API_DOMAIN)
    }

    fun getVideoDomain(): String {
        return cacheManager.getValueByKey(CACHE_KEY_VIDEO_DOMAIN)
    }

    fun getVideoThumbDomain(): String {
        return cacheManager.getValueByKey(CACHE_KEY_VIDEO_THUMB_DOMAIN)
    }

    fun saveApiDomain(domain: String) {
        cacheManager.saveCache(CACHE_KEY_API_DOMAIN, domain)
    }

    fun saveImgDomain(domains: String) {
        cacheManager.saveCache(CACHE_KEY_IMG_DOMAIN, domains)
    }

    fun saveVideoThumbDomain(domains: String) {
        cacheManager.saveCache(CACHE_KEY_VIDEO_THUMB_DOMAIN, domains)
    }

    fun saveVideoDomain(domains: String) {
        cacheManager.saveCache(CACHE_KEY_VIDEO_DOMAIN, domains)
    }

    fun saveVideoTags(tags: MutableList<Tag>) {
        val tagsJson = GsonProvider.gson.toJson(tags)
        cacheManager.saveCache(CACHE_KEY_VIDEO_TAGS, tagsJson)
    }

    fun getVideoTags(): MutableList<Tag> {
        var tagsJson = cacheManager.getValueByKey(CACHE_KEY_VIDEO_TAGS)
        if (tagsJson.isEmpty()) {
            val input = ReaderApplication.instance.assets.open("tags.json")
            tagsJson = input.bufferedReader().use { it.readText() }
        }
        val type = object : TypeToken<MutableList<Tag>>() {}.type
        return GsonProvider.gson.fromJson(tagsJson, type)
    }

    fun getTagScore(): MutableList<TagCount>? {
        val tagsJson = cacheManager.getValueByKey(CACHE_KEY_VIDEO_TAGS_SCORE)
        val type = object : TypeToken<MutableList<TagCount>>() {}.type
        if (tagsJson == "") {
            return null
        }
        return GsonProvider.gson.fromJson(tagsJson, type)
    }

    fun saveTagScore(tagCount: MutableList<TagCount>?) {
        val tagCountJson = GsonProvider.gson.toJson(tagCount)
        cacheManager.saveCache(CACHE_KEY_VIDEO_TAGS_SCORE, tagCountJson)
    }


    private var imgDomain = ""
    fun getImgDomain(): String {
        if (imgDomain.isNotEmpty()) return imgDomain
        val domains = cacheManager.getValueByKey(CACHE_KEY_IMG_DOMAIN)
        val domainArr = domains.split(",")
        imgDomain = domainArr[0]
        return imgDomain
    }


    var categoryConfig: List<CategoryBean>? = null
    var currentReading: ComicBookBean? = null


    fun saveAutoPlay(isAutoPlay: String) {
        cacheManager.saveCache(CACHE_KEY_VIDEO_AUTO_PALY, isAutoPlay)
    }

    fun getAutoPlay(): Boolean {
        val isAutoPlay = cacheManager.getValueByKey(CACHE_KEY_VIDEO_AUTO_PALY)
        return isAutoPlay == AUTO_PLAY_OPEN
    }

    fun saveUserTempType(isTemp: Int) {
        cacheManager.saveCache(CACHE_KEY_VIDEO_USER_TYPE, isTemp.toString())
    }

    fun getUserTempType(): Boolean {
        val isTemp = cacheManager.getValueByKey(CACHE_KEY_VIDEO_USER_TYPE)
        return isTemp == "1"
    }

    fun getTempUser(): Int {
        val isTemp = cacheManager.getValueByKey(CACHE_KEY_VIDEO_USER_TYPE)
        return if (isTemp == "") {
            0
        } else {
            isTemp.toInt()
        }
    }

    fun getShareLinkId(): String {
        return cacheManager.getValueByKey(CACHE_KEY_IS_SHARE_LINKID)
    }

    fun cacheShareLinkId(linkid: String = "1") {
        cacheManager.saveCache(CACHE_KEY_IS_SHARE_LINKID, linkid)
    }

    fun saveBulletinDialogShow(isShow: String) {
        cacheManager.saveCache(CACHE_KEY_BULLETIN_SHOW, isShow)
    }

    fun getBulletinDialogShow(): Boolean {
        val url = cacheManager.getValueByKey(CACHE_KEY_BULLETIN_SHOW)
        return url == "true"
    }

    fun saveShowDay(day: String) {
        cacheManager.saveCache(CACHE_KEY_BULLETIN_SHOW_DAY, day)
    }

    fun getSaveShowDay(): String {
        val day = cacheManager.getValueByKey(CACHE_KEY_BULLETIN_SHOW_DAY)
        return if (day == "") "0" else day
    }

    fun saveAutoPlayCount(str: String) {
        cacheManager.saveCache(CACHE_KEY_AUTO_PLAY_COUNT, str)
    }

    fun getAutoPlayCount(): AutoPlayConf {
        val str = cacheManager.getValueByKey(CACHE_KEY_AUTO_PLAY_COUNT)
        if (str.isEmpty()) {
            val conf = AutoPlayConf()
            conf.count = 4
            conf.enable = 0
            return conf
        }
        return Gson().fromJson(str, AutoPlayConf::class.java)
    }

    fun saveAdList(json: String) {
        cacheManager.saveCache(CACHE_KEY_AD_LIST, json)
    }

    fun getAdList(): MutableList<AdBean>? {
        val json = cacheManager.getValueByKey(CACHE_KEY_AD_LIST)
        return if (json.isEmpty()) {
            null
        } else {
            val type = object : TypeToken<MutableList<AdBean>>() {}.type
            GsonProvider.gson.fromJson(json, type)
        }
    }
}