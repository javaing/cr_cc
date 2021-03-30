package com.aliee.quei.mo.analyze.bean

/**
 * Created by liyang on 2018/6/13 0013.
 */
data class ShareAnalyzeBean(
        var entrance : String,
        var channel : String? = null,
        var result : String? = null,
        var novelId : Long? = 0,
        var chapterId : Long? = 0,
        var uid : Int? = 0,
        var message : String? = null
){
    fun toMap() : Map<String,String>{
        return mapOf<String,String>(
                "entrance" to entrance,
                "channel" to channel.toString(),
                "result" to result.toString(),
                "novelId" to novelId.toString(),
                "chapterId" to chapterId.toString(),
                "id" to uid.toString()
        )
    }
}

data class InviteAnalyzeBean(
        var entrance : String?,
        var channel : String? = null,
        var result : String? = null,
        var novelId : Long? = 0,
        var chapterId : Long? = 0,
        var uid : Int? = 0,
        var message : String? = null
) {
    fun toMap(): Map<String,String> {
        return mapOf<String,String>(
                "entrance" to entrance.toString(),
                "channel" to channel.toString(),
                "result" to result.toString(),
                "novelId" to novelId.toString(),
                "chapterId" to chapterId.toString(),
                "id" to uid.toString()
        )
    }
}
