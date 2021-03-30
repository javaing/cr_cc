package com.aliee.quei.mo.utils

import android.view.View
import android.widget.LinearLayout
import com.aliee.quei.mo.R
import kotlin.random.Random



object ComicUtils{
    val tags = mutableListOf<Int>(
        R.layout.tag_comic_cd,
        R.layout.tag_comic_fc,
        R.layout.tag_comic_jp,
        R.layout.tag_comic_lh,
        R.layout.tag_comic_vip,
        R.layout.tag_comic_xa,
        R.layout.tag_comic_yy
    )

    fun addSomeTags(viewGroup: LinearLayout) {
        try {
            val childCount = viewGroup.childCount
            if (childCount > 0)return
            val context = viewGroup.context
            tags.shuffle()
            val tagSize = Random.nextInt(3) + 2
            tags.subList(0,tagSize)
                .forEach {
                    View.inflate(context,it,viewGroup)
                }
        } catch (e : java.lang.Exception) {
            e.printStackTrace()
        }

    }

    fun getCommentStar(id : Int) : String {
        return try {
            "9.${id % 100}"
        } catch (e : Exception){
            "9.5"
        }
    }

    fun getReadNum(id : Int) : Int {
        val r = System.currentTimeMillis() / 1000 / 100 - 15100000 + id * 2
        return if (r.toInt() > 200000) r.toInt() else 400000
    }



}