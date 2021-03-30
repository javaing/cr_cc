package com.aliee.quei.mo.ui.catalog.vm

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseViewModel
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.base.response.UIListDataBean
import com.aliee.quei.mo.data.bean.CatalogItemBean
import com.aliee.quei.mo.data.repository.CatalogRepository
import com.aliee.quei.mo.database.DatabaseProvider
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.realm.Realm
import io.realm.RealmResults

class CatalogVModel : BaseViewModel(){
    private val catalogRepository = CatalogRepository()
    val getCatalogLiveData = MediatorLiveData<UIListDataBean<CatalogItemBean>>()

    private var page = 1
    private var pageSize = 500

    private var realm : Realm? = null
    private var realmList : RealmResults<CatalogItemBean>? = null
    @SuppressLint("CheckResult")
    fun loadAllCatalogAndCacheWithRealm(lifecycleOwner: LifecycleOwner, bookid: Int){
        page = 1
        getCatalogLiveData.postValue(UIListDataBean(Status.Start, mutableListOf()))

        catalogRepository.getCatalog(ReaderApplication.instance,bookid,1,20,0) //检查本地目录是否完整
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .subscribe({
                    val realm = DatabaseProvider.getRealm()
                    val localList = realm.where(CatalogItemBean::class.java)?.equalTo("bookid",bookid)?.findAll()
                    if(localList?.size == it.count){ //本地缓存有效
                        getCatalogLiveData.value = UIListDataBean(Status.Success, mutableListOf())
                        getCatalogLiveData.value = UIListDataBean(Status.Complete, mutableListOf())
                    } else {
                        val list = it.list
                        realm.executeTransaction {
                            it.copyToRealmOrUpdate(list)
                        }
                        pageSize = it.count / 3
                        if(pageSize < 300)pageSize = 300
                        if(pageSize > 500)pageSize = 500
                        requestAndCacheToRealm(lifecycleOwner,bookid)
                    }
                    realm.close()
                },{
                    it.printStackTrace()
                    getCatalogLiveData.value = UIListDataBean(Status.Error, mutableListOf())
                    getCatalogLiveData.value = UIListDataBean(Status.Complete, mutableListOf())
                })
    }

    @SuppressLint("CheckResult")
    private fun requestAndCacheToRealm(lifecycleOwner: LifecycleOwner, bookid: Int){
        catalogRepository.getCatalog(ReaderApplication.instance,bookid,page,pageSize,0)
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .doOnDispose {

                    if(realm?.isClosed == false){
                        realm?.close()
                    }
                }
                .subscribe({
                    getCatalogLiveData.value = UIListDataBean(Status.Success, mutableListOf())
                    getCatalogLiveData.value = UIListDataBean(Status.Complete, mutableListOf())
                    if(realm == null || realm?.isClosed == true){
                        realm = DatabaseProvider.getRealm()
                        realmList = realm?.where(CatalogItemBean::class.java)?.equalTo("bookid",bookid)?.findAll()
                    }
                    val data = it.list
                    realm?.executeTransaction {
                        it.copyToRealmOrUpdate(data)
                    }
                    if(realmList?.size?:0 < it.count && !data.isEmpty()){
                        page++
                        requestAndCacheToRealm(lifecycleOwner,bookid)
                    } else {
                        if(realm?.isClosed == false){
                            realm?.close()
                        }
                    }
                },{
                    getCatalogLiveData.value = UIListDataBean(Status.Error, mutableListOf())
                    it.printStackTrace()
                })
    }

    private val catalogList = mutableListOf<CatalogItemBean>()
    fun refreshAllCatalogAndCacheWithRealm(lifecycleOwner: LifecycleOwner,bookid: Int){
        page = 1
        refreshCatalog(lifecycleOwner,bookid = bookid)
    }

    private fun refreshCatalog(lifecycleOwner: LifecycleOwner,bookid: Int){
        catalogRepository.getCatalog(ReaderApplication.instance,bookid,page,pageSize,0)
                .bindUntilEvent(lifecycleOwner,Lifecycle.Event.ON_DESTROY)
                .doOnDispose {

                    if(realm?.isClosed == false){
                        realm?.close()
                    }
                }
                .subscribe({
                    if(it.page == 1)catalogList.clear()

                    if(realm == null || realm?.isClosed == true){
                        realm = DatabaseProvider.getRealm()
                    }
                    val data = it.list
                    catalogList.addAll(data)
                    realm?.executeTransaction {
                        it.copyToRealmOrUpdate(data)
                    }
                    if(catalogList.size < it.count){
                        page++
                        refreshCatalog(lifecycleOwner,bookid)
                    } else {
                        if(realm?.isClosed == false){
                            realm?.close()
                        }
                    }
                },{
                    it.printStackTrace()
                })
    }

}