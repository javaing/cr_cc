package com.aliee.quei.mo.data.bean

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ShelfBean : RealmObject(){
	@PrimaryKey
	var bookid : Int = 0
	var add_time : Long = 0
	var bookInfo : ComicBookBean? = null
}