package com.aliee.quei.mo.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aliee.quei.mo.data.bean.Tag
import com.aliee.quei.mo.ui.main.fragment.VideoChildFragment

open class VideoTabFragmentAdapter(fm: FragmentActivity, tags: MutableList<Tag>) : FragmentStateAdapter(fm) {

    private var tags: MutableList<Tag> = tags

    override fun createFragment(position: Int): Fragment {
        return VideoChildFragment.newInstance(tags[position])
    }

    override fun  getItemCount(): Int {
        if (tags.size > 0) {
            return tags.size
        }
        return 0
    }

}