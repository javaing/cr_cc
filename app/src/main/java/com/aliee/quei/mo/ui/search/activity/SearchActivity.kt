package com.aliee.quei.mo.ui.search.activity

import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.data.bean.SearchHistoryBean
import com.aliee.quei.mo.database.DatabaseProvider
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.search.fragment.SearchFragment
import com.aliee.quei.mo.ui.search.fragment.SearchResultFragment
import com.aliee.quei.mo.utils.extention.addFragment
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.showHideFragment
import com.aliee.quei.mo.widget.view.panelSwitch.util.KeyboardUtil
import kotlinx.android.synthetic.main.layout_title_search.*

/**
 * Created by Administrator on 2018/4/28 0028.
 */
@Route(path = Path.PATH_SEARCH)
class SearchActivity : BaseActivity(){
    @Autowired
    @JvmField
    var keyword : String = ""

    override fun getPageName() = "搜索页面"
    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    override fun initData() {
        edit.setText(keyword)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (keyword.isNotEmpty()) {
            doSearch(keyword)
        }
    }

    fun doSearch(key : String){
        edit.setText(key)
        val keyword = edit.text.toString()
        if(TextUtils.isEmpty(keyword))return

        addToSearchHistory(keyword)
        fragment2.doSearch(key)
        showHideFragment(fragment2,fragment1)
        KeyboardUtil.hideKeyboard(edit)
    }

    private fun addToSearchHistory(keyword : String) {
        val realm = DatabaseProvider.getRealm()
        realm.executeTransaction {
            val bean = SearchHistoryBean()
            bean.time = System.currentTimeMillis()
            bean.keyword = keyword
            it.insertOrUpdate(bean)
        }
        realm.close()
    }


    override fun initView() {
        initFragment()
        initClick()
    }

    lateinit var fragment1 : SearchFragment
    lateinit var  fragment2 : SearchResultFragment
    private fun initFragment() {
        fragment1 = ARouterManager.getSearchFragment(this)
        fragment2 = ARouterManager.getSearchResultFragment(this)
        addFragment(fragment1,R.id.container)
        addFragment(fragment2,R.id.container)
        showHideFragment(fragment1,fragment2)
    }

    private fun initClick(){
        edit.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?){
                if(edit.text.toString().isNotEmpty()){
                    btn.text = getString(R.string.search)
                } else {
                    btn.text = getString(R.string.cancel)
                    showHideFragment(fragment1,fragment2)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)  = Unit
        })

        edit.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                val key = edit.text.toString()
                if (TextUtils.isEmpty(key))return@setOnEditorActionListener true
                doSearch(edit.text.toString())
                showHideFragment(fragment2,fragment1)
            }
            true
        }

        btn.click {
            if(edit.text.toString().isNotEmpty()){
                doSearch(edit.text.toString())
            } else {
                finish()
            }
        }
    }

}