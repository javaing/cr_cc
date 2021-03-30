package com.aliee.quei.mo.component

import com.google.gson.*
import java.lang.reflect.Type

/**
 * @Author: YangYang
 * @Date: 2018/1/10
 * @Version: 1.0.0
 * @Description:
 */
object GsonProvider {

    val gson: Gson = GsonBuilder()
            .registerTypeAdapter(Long::class.java,LongDefault0Adapter())
            .registerTypeAdapter(Int::class.java,IntDefaultAdapter())
            .create()
}

class IntDefaultAdapter : JsonSerializer<Int>, JsonDeserializer<Int>{
    override fun serialize(src: Int, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Int {
        try{
            return json.asInt
        } catch (e : Exception){
            return 0
        }
    }
}

