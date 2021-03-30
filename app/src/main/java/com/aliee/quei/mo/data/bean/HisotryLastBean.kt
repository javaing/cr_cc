package com.aliee.quei.mo.data.bean

/**
 *作者:sunfuyi
 *时间:2019-08-08
 *描述:
 */

data class HistoryLastBean(
    var book: Book,
    var bookBean: Int,
    var bookid: Int,
    var id: Int,
    var image: List<String>,
    var images: String,
    var isOffline: Int,
    var maxsort: String,
    var minsort: String,
    var name: String,
    var nextid: Int,
    var previd: Int,
    var sort: Int,
    var thumb: String
)

data class Book(
    var author: String,
    var bookIndex: Int,
    var createTime: Int,
    var description: String,
    var id: Int,
    var isOffline: Int,
    var isSearch: Int,
    var lang: Int,
    var sexy: Int,
    var status: Int,
    var tag: String,
    var thumb: String,
    var thumbX: String,
    var tid: Int,
    var title: String,
    var typename: String,
    var updateTime: Int
)