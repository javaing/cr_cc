package com.aliee.quei.mo.data.exception

/**
 * @Author: YangYang
 * @Date: 2018/1/10
 * @Version: 1.0.0
 * @Description:
 */
class RequestException(val code: Int = -1, val msg: String = "") : Exception() {
    companion object {
//        val ERROR_CODE_LOGIN = 1001
//        val ERROR_CODE_TOKEN_INVALID = 1004
//        val ERROR_CODE_TOKEN_EXPIRE = 1005
//        val ERROR_CODE_TOKEN_INVALID_2 = 1018
//
//        val ERROR_ACCOUNT_NOT_EXIST = 100010
//        val ERROR_ACCOUNT_MOBILE_EMPTY = 200112
//        val ERROR_BALANCE_INSUFFICIENT = 200100

        const val ERROR_REQUEST_SUCCESS_BUT_RETURN_NULL = -2

        const val ERROR_REQUEST_TOKEN_FAILED = 1015
    }
}