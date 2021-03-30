package com.aliee.quei.mo.data.bean

class AppUpdate {
    val version: String? = null // (返回下載APK的版本)
    val descr: String? = null // (提示更新顯示的文字)
    val descImgUrl: String? = null// (提示更新顯示的圖檔,格式為同公告的html圖檔, 空字串時, 不顯示圖片)
    val url: String? = null //(APK下載的URL)
    var isForce: Int? = 0// (1強更0不強更)
    var button: String? = null
}