package com.aliee.quei.mo.data.repository;


import com.aliee.quei.mo.base.response.function.PageSuccessFunc;
import com.aliee.quei.mo.base.response.function.SuccessFunc;
import com.aliee.quei.mo.data.bean.BaseResponse;
import com.aliee.quei.mo.data.bean.ListBean;
import io.reactivex.ObservableTransformer;

import java.util.List;

/**
 * @Author: YangYang
 * @Date: 2018/1/10
 * @Version: 1.0.0
 * @Description:
 */
public class BaseRepository {

    //response

    /**
     * 判读请求是否成功+解密+转为List的集合
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<BaseResponse<ListBean<T>>, ListBean<T>> handleListBean(int page,int pageSize) {
        return upstream -> upstream.map(new PageSuccessFunc<>(page, pageSize));
    }

    /**
     * 判读请求是否成功+解密+转为List的集合
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<BaseResponse<List<T>>, List<T>> handleList() {
        return upstream ->
                upstream.map(new SuccessFunc<>());
    }

    /**
     * 判读请求是否成功+解密+转为Bean的集合
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<BaseResponse<T>, T> handleBean() {
        return upstream ->
                upstream.map(new SuccessFunc<>());
    }

    /**
     * 判读请求是否成功+解密+转为Bean的集合
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<BaseResponse<T>, T> handleBean(T defaultValue) {
        return upstream ->
                upstream.map(tBaseResponse -> {
                    tBaseResponse.setData(defaultValue);
                    return tBaseResponse;
                }).map(new SuccessFunc<T>());
    }
//
//    /**
//     * 判读请求是否成功+解密+转为Bean的集合
//     *
//     * @param <T>
//     * @return
//     */
//    public static <T> ObservableTransformer<BaseResponse<T>, T> handleBean() {
//        return upstream -> upstream.map(new SuccessFunc<>());
//    }


}
