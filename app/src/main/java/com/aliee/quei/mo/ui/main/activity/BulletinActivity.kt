package com.aliee.quei.mo.ui.main.activity

import android.arch.lifecycle.Observer
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.GsonProvider.gson
import com.aliee.quei.mo.data.BulletinInfo
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.main.adapter.BulletinAdapter
import com.aliee.quei.mo.ui.main.vm.BulletinVModel
import com.aliee.quei.mo.utils.extention.click


import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_common_list.*
import kotlinx.android.synthetic.main.layout_title.*
import org.json.JSONArray
import org.json.JSONObject


@Route(path = Path.PATH_BULLETIN_ACTIVITY)
class BulletinActivity : BaseActivity(){
    override fun getLayoutId() = R.layout.activity_bulletin

    private val VM = BulletinVModel()
    private val adapter = BulletinAdapter()

    override fun initData() {



        VM.getBulletin(this)
    }

    override fun initView() {
        initTitle()
        initVM()
        initRecyclerView()

    }

    private fun initTitle() {
        titleBack.click { onBackPressed() }
        titleText.text = getString(R.string.bulletin)
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        adapter.itemClick = {
            Log.d("BulletinActivity", "BulletinID:"+it)
            ARouterManager.goBulletinDetailActivity(this,it.toInt())
        }


    }

    private fun initVM() {
        VM.bulletinFullLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Start -> {

                }
                Status.Success -> {

                    Log.d("BulletinActivity", "BulletinFullLiveData:"+it.data?.data)
                    val json: String = gson.toJson(it.data)
                    val data = JSONObject(json).getJSONObject("data");
                    val translations: JSONArray = data.getJSONArray("list")
                    val list = ArrayList<BulletinInfo>()
                    for (i in 0 until translations.length()) {
                        val jsonObject: JSONObject = translations.getJSONObject(i)
                        val bulletinInfo: BulletinInfo = Gson().fromJson(jsonObject.toString(), BulletinInfo::class.java)
                        list.add(bulletinInfo)
                    }
//                    Log.d("BulletinActivity", "Bulletin List Size:"+ list.size)
                    adapter.setData(list)

                }
                Status.Empty -> {

                }
                Status.Error -> {

                }
                Status.NoNetwork -> {

                }
                Status.NoMore -> {

                }
            }
        })
    }


    override fun getPageName() = "公告页面"
}