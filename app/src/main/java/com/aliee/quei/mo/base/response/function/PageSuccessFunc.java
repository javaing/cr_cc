package com.aliee.quei.mo.base.response.function;

import com.aliee.quei.mo.data.bean.BaseResponse;
import com.aliee.quei.mo.data.bean.ListBean;
import com.aliee.quei.mo.data.exception.RequestException;
import io.reactivex.functions.Function;

public class PageSuccessFunc<T> implements Function<BaseResponse<T>, T> {
    private final int page;
    private final int pageSize;

    public PageSuccessFunc(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    @Override
    public T apply(BaseResponse<T> tBaseResponse) throws Exception {
        if (tBaseResponse.getCode() == 0) {
            T data = tBaseResponse.getData();
            if (data == null){
                data = (T) new Object();
            }

            if (tBaseResponse.getData() instanceof ListBean){
                ListBean<T> listBean = (ListBean<T>) tBaseResponse.getData();
                listBean.setPageSize(pageSize);
                listBean.setPage(page);
            }
            return data;
        } else {
            throw (new RequestException(tBaseResponse.getCode(), tBaseResponse.getMsg()));
        }
    }
}
