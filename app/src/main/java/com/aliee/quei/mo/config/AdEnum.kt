package com.aliee.quei.mo.config

enum class AdEnum(val zid: Int) {   //此处zid 为接口返回的id，偷懒了
    OPEN_SCREEN(43),  //开屏广告   43 57 59
    HOME_DIALOG(56),//弹窗广告
    BANNER(57), //漫画首页banner
    COMIC_INFO_FLOW_90(68), //漫画信息流 90
    COMIC_INFO_FLOW_QU(72), //漫画信息流区块
    COMIC_INFO_FLOW_QIANG(73), //漫画信息流墙
    BOTTOM_BAR(70), //下方浮动
    COMIC_READ_HEAD(74), //漫画阅读页顶部
    COMIC_READ_CENTER(75), //漫画阅读页中间
    COMIC_READ_BOTTOM(76), //漫画阅读页底部
    COMIC_SEARCH(77), //搜索上方
    COMIC_RANK(78), //排行榜
    VIDEO_CHILD_LIST(79), //短视频首页
    VIDEO_SEARCH(80), //视频搜索
    VIDEO_INFO(81), //视频详情·
    COMIC_READ_DIALOG(92), //漫画内页弹窗广告
    COMIC_GUESS_LIKE_RANK(93), //漫画猜你喜欢
    VIDEO_GUESS_LIKE_RANK(94) //视频详情页猜你喜欢

}