package com.aliee.quei.mo.base.response

/**
 * @Author: YangYang
 * @Date: 2017/12/28
 * @Version: 1.0.0
 * @Description:
 */
class UIDataBean<T>(var status: Status, var data: T?, var code: Int?, var e: Throwable?) {

    //只有请求状态的UIData构造
    constructor(status: Status) : this(status, null, -1, null)

    //只有请求状态和数据的UIData构造
    constructor(status: Status, data: T?) : this(status, data, -1, null)

    //请求成功且数据正确返回的UIData构造
    constructor(data: T?) : this(Status.Success, data, -1, null)

    //请求失败的UIData构造，包含网络异常，一般错误，接口返回异常等
    constructor(e: Throwable?) : this(Status.Error, null, -1, e)

    //请求成功后需要拿当前返回的code
    constructor(data: T?, code: Int?) : this(Status.Success, data, code, null)

    /**
     * 判断请求是否成功
     */
    fun isSuccess(): Boolean {
        return status == Status.Success
    }
}