package com.aliee.quei.mo.base.response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Author: YangYang
 * @Date: 2018/1/10
 * @Version: 1.0.0
 * @Description:
 */
public class ParameterizedTypeImpl implements ParameterizedType {
    private final Class raw;
    private final Type[] args;

    public ParameterizedTypeImpl(Class raw, Type[] args) {
        this.raw = raw;
        this.args = args != null ? args : new Type[0];
    }

    @Override
    public Type[] getActualTypeArguments() {
        return args;
    }

    @Override
    public Type getRawType() {
        return raw;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}