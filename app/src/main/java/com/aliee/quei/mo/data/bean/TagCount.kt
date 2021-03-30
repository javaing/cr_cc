package com.aliee.quei.mo.data.bean

data class TagCount(val tagId: Int?, val tagName: String, var count: Int = 0){
    override fun toString(): String {
        return "TagCount(tagId=$tagId, tagName='$tagName', count=$count)"
    }
}