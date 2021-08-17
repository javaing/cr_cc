package com.aliee.quei.mo.component

/**
 * Created by Administrator on 2018/4/20 0020.
 */
class EventLoginSuccess

class EventLogout

class EventUserInfoUpdated

class EventShelfChanged

class EventCancelCleanShelf

class EventLaunchConfigsSuccess

class EventRechargeSuccess

class EventRefreshHome

class EventReadHistoryUpdated(val bookId: Int? = null)

class EventReadToHome

class EventHideBottomBar

class EventShowBottomBar

class EventReturnComic

class EventToMine

class EventToRecharge

class EventToBulletin

class EventAutoSwitch

//判断从漫画进入还是视频进入个人中心
class EventToMineFrom(val isBook: Boolean = true)

class EventToMineFreeTime

class EventToReLogin

class EventModifyPwd

class EventShowBulletin

class EventRechargeUser

/**
 * 周阅读时长更新
 */
data class EventWeekReadTimeUpdated(val minute: Int)