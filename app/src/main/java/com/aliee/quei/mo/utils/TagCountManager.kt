package com.aliee.quei.mo.utils

import android.util.Log
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.bean.Tag
import com.aliee.quei.mo.data.bean.TagCount
import com.aliee.quei.mo.data.bean.Tags
import org.apache.commons.lang3.RandomUtils

object TagCountManager {

    var tagCounts: MutableList<TagCount>? = null
    var moreTagCount = 1
    var onlyTagCount = 3

    /**
     * 初始化
     */
    fun init(tags: MutableList<Tag>) {
        tagCounts = CommonDataProvider.instance.getTagScore()
        if (tagCounts.isNullOrEmpty()) {
            tagCounts = mutableListOf()
            tags.forEach {
                val tagCount = TagCount(it.id, it.tag)
                tagCounts?.add(tagCount)
            }
        }
    }

    /**
     * 保存点击过的tag 分数
     */
    fun saveOnlyTagCount(tag: Tag) {
        tagCounts?.forEach {
            if (it.tagId == tag.id) {
                it.count += onlyTagCount  //tag  点击 加3
            }
        }
        CommonDataProvider.instance.saveTagScore(tagCounts)
    }

    /**
     * 保存视频观看时，多tag分数
     */
    fun saveMoreTagCount(tags: List<Tags>?) {
        if (tags?.size == 0) {
            return
        }
        tags?.forEach { tag ->
            tagCounts?.forEach { tagCount ->
                if (tagCount.tagId == tag.id) {
                    tagCount.count += moreTagCount  //视频点击  点击 加3
                }
            }
        }
        CommonDataProvider.instance.saveTagScore(tagCounts)
    }

    /**
     * 取最高分的tag
     */
    fun getTagCount(): TagCount? {
        val tagCounts = CommonDataProvider.instance.getTagScore() ?: return null
        tagCounts.sortByDescending { it.count }
        val tags = tagCounts.subList(0,5)
        return tags[RandomUtils.nextInt(0,tags.size)]
    }
}