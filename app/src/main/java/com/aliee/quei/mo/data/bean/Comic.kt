package com.aliee.quei.mo.data.bean

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ComicBookBean : RealmObject(){
    @PrimaryKey
    var id: Int = 0
    var author: String? =""
    var description: String = ""
    var sexy: Int = 0
    var status: Int = 0
    var tag: String = ""
    var thumb: String = ""
    var tid: Int = 0
    var title: String = ""
    var typename: String = ""
    var bookcover : String = ""
    var thumbX = ""

}

data class RecommendPositionList(
    val list : MutableList<RecommendBookBean>
)

data class RecommendBookBean(
        val author: String?,
        val bookcover: String?,
        val id: Int,
        val desc: String?,
        val status: Int?,
        var thumb: String?,
        val title: String?,
        val typename: String?
) {
    var showType: Int = 0;// 0 grid ,1 linear
    var tagText : String = ""
    var tagColor : Int = 0
    var rid : String? = ""
    var adClickUrl:String?=""
    var adCallbackUrl:String?=""

    override fun equals(other: Any?): Boolean {
        if (other is RecommendBookBean) {
            return id == other.id
        }
        return false
    }
}

data class RecommendListBean(val rid : String,val name : String,val list : MutableList<RecommendBookBean>? = null)
