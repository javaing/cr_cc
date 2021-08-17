package com.aliee.quei.mo.net


/**
 * @Author: YangYang
 * @Date: 2017/12/26
 * @Version: 1.0.0
 * @Description:
 */
class ApiConstants private constructor() {

    companion object {
        //请求地址
        //  var API_HOST = BuildConfig.API_HOST
        //  var API_HOST = "http://47.75.63.143/"
        //  var API_HOST = "http://47.52.26.64:8080/"
        //  var API_HOST = "http://api.ti1z6.com"
        var API_HOST = ""

        var fdsfsdfsdf = "0123456789abcdef"
        const val API_VERSION = "2/"
        const val API_PAY_VERSION = "3/"
        const val VIDEO_TYPESTRING = "20"

        //online
        const val OSS_PATH = "https://dswd202101.oss-cn-shenzhen.aliyuncs.com/api.json.txt"
        const val VOSS_PATH = "https://dswd202101v.oss-cn-shanghai.aliyuncs.com/api.json.txt"
        const val VOSS_PATH2 = "https://dswd202002v.oss-cn-shenzhen.aliyuncs.com/api.json.txt"
        //debug
        //  const val OSS_PATH = "https://cartoon2020d.oss-cn-hongkong.aliyuncs.com/tapi.json.txt"
//          const val OSS_PATH2 = "https://cartoon202007d.oss-cn-hongkong.aliyuncs.com/api.json.txt"
//        const val OSS_PATH3 = "https://cartoon202008d.oss-cn-hongkong.aliyuncs.com/api.json.txt"

        const val PAY_CALLBACK = "?redirect="
        var PAY_CALLBACK_PATH = ""
        var APP_DOWNLOAD_PATH = ""
        var CS_NUMBER = ""
        var CS_ROUTE = ""
        var APP_ID: String? = null
        var HOTFIX: Int = 0

        const val DEFAULT_VIDEO_API_PATH = "http://vapi.7911126.com/"
        var VIDEO_API_PATH = DEFAULT_VIDEO_API_PATH

    }
}