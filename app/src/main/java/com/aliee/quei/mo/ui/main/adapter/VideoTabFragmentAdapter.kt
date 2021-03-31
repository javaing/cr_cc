package com.aliee.quei.mo.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.ViewGroup
import com.aliee.quei.mo.data.bean.Tag
import com.aliee.quei.mo.ui.main.fragment.VideoChildFragment

class VideoTabFragmentAdapter(fm: FragmentManager, tags: MutableList<Tag>) : FragmentPagerAdapter(fm) {
    private var fm: FragmentManager = fm

    private var tags: MutableList<Tag> = tags

    override fun getPageTitle(position: Int): CharSequence? {
        return tags[position].tag
    }

    override fun getItem(position: Int): Fragment {
        return VideoChildFragment.newInstance(tags[position])
    }

    override fun getCount(): Int {
        if (tags.size > 0) {
            return tags.size
        }
        return 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        fm.beginTransaction().show(fragment).commit()
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        val fragment = any as Fragment
        fm.beginTransaction().hide(fragment).commit()
    }


}