package com.aliee.quei.mo.ui.main.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.Tag
import com.aliee.quei.mo.ui.launch.activity.LaunchAdFragment
import com.aliee.quei.mo.ui.main.fragment.VideoChildFragment

class LaunchAdFragmentAdapter(fm: FragmentManager, adInfos: MutableList<AdInfo>) : FragmentPagerAdapter(fm) {
    private var fm: FragmentManager = fm

    private var adInfos: MutableList<AdInfo> = adInfos



    override fun getItem(position: Int): Fragment {
        return LaunchAdFragment.newInstance(adInfos[position],position)
    }

    override fun getCount(): Int {
        if (adInfos.size > 0) {
            return adInfos.size
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