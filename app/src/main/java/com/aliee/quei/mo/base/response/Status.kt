package com.aliee.quei.mo.base.response

/**
 * @Author: YangYang
 * @Date: 2017/12/28
 * @Version: 1.0.0
 * @Description:数据请求的状态
 */
enum class Status {
    Before,//第一次请求
    Start,//请求开始
    Complete,//请求结束
    NoNetwork,//无网络
    Success,//请求成功
    Error,//请求出错
    Empty,//请求列表为空
    NoMore,//请求列表没有更多数据了
    TokenError//token错误
}