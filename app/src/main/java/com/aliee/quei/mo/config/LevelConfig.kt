package com.aliee.quei.mo.config

object LevelConfig{
    fun getTitleByLevel(level : Int) : String{
        if (level <= 5)return "坚韧黑铁"
        if (level <= 10)return "倔强青铜"
        if (level <= 15)return "不屈白银"
        if (level <= 20)return "荣耀黄金"
        if (level <= 25)return "尊贵铂金"
        if (level <= 30)return "璀璨钻石"
        if (level <= 35)return "至尊星耀"
        if (level <= 40)return "最强王者"
        if (level <= 45)return "荣耀王者"
        return "智慧王者"
    }
}