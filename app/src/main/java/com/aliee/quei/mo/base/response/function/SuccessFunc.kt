package com.aliee.quei.mo.base.response.function


import com.aliee.quei.mo.data.bean.BaseResponse
import com.aliee.quei.mo.data.exception.RequestException
import io.reactivex.functions.Function

class SuccessFunc<T> : Function<BaseResponse<T>, T?> {
    override fun apply(t: BaseResponse<T>): T? {
        var msg = t.msg
        when (t.code) {
            0 -> {
                if (t.data == null) {
                    throw RequestException(RequestException.ERROR_REQUEST_SUCCESS_BUT_RETURN_NULL)
                }
                return t.data
            }
            /* if (t.code == 1009){
                 throw ObjectException(t.data)
             }*/
        }
        /* if (t.code == 1009){
             throw ObjectException(t.data)
         }*/

        when (t.code) {
            1013, 1015, 2003, -2 -> msg = ""
            1002-> t.msg
        }
        throw RequestException(t.code, msg ?: "")

    }
}