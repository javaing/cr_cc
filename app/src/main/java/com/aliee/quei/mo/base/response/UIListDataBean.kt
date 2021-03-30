package com.aliee.quei.mo.base.response

/**
 * @Author: YangYang
 * @Date: 2017/12/28
 * @Version: 1.0.0
 * @Description:List类型的bean
 */
class UIListDataBean<T>(var status: Status, var data: MutableList<T>, var e: Throwable?,var total : Int = 0) {

    constructor(status: Status, data: MutableList<T>) : this(status, data, null)
    constructor(status: Status, data: MutableList<T>,total: Int) : this(status, data, null,total)

    /**
     * 判断请求是否成功
     */
    fun isSuccess(): Boolean {
        return status == Status.Success
    }
}