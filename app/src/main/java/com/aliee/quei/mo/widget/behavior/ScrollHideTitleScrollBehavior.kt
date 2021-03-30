package com.aliee.quei.mo.widget.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View

class ScrollHideTitleScrollBehavior constructor(context: Context,attributes: AttributeSet?) : CoordinatorLayout.Behavior<ScrollHideTitle>(context,attributes){

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: ScrollHideTitle?, dependency: View?): Boolean {
        if(dependency is ScrollHideTitle)return true
        return false
    }

}