package com.aliee.quei.mo.ui.main.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.aliee.quei.mo.data.local.TaskBean
import com.aliee.quei.mo.ui.main.holder.TaskHolder
import io.realm.Realm

class TaskAdapter(val realm : Realm): RecyclerView.Adapter<TaskHolder>() {
    private val mData = mutableListOf<TaskBean>()

    var claimClick : ((bean : TaskBean) -> Unit)? = null
    fun setData(list : List<TaskBean>) {
        mData.clear()
        mData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        return TaskHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        holder.bind(mData[position],realm,claimClick)
    }

    fun updateBean(bean: TaskBean?) {
        bean?:return
        for (i in 0 until mData.size) {
            if (bean == mData[i]) {
                notifyItemChanged(i)
                break
            }
        }
    }
}