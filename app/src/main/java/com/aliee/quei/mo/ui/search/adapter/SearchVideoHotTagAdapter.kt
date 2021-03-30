package com.aliee.quei.mo.ui.search.adapter

import android.view.View
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.utils.extention.inflate
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter

class SearchVideoHotTagAdapter(arr: Array<String>) : TagAdapter<String>(arr) {
    override fun getView(parent: FlowLayout, position: Int, t: String): View {
        val textView = parent.context.inflate(R.layout.item_book_flow_title, parent, false) as TextView
        textView.text = t
        textView.tag = t
        return textView
    }

    override fun getCount(): Int {
        val size = super.getCount()
        if (size > 9) return 9
        return size
    }
}