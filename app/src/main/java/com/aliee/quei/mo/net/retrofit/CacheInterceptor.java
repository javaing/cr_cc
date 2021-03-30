package com.aliee.quei.mo.net.retrofit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.aliee.quei.mo.application.ReaderApplication;
import com.aliee.quei.mo.cache.DBCacheManager;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 数据缓存拦截器，如果某一接口需要缓存，在请求添加localCache 的header . 单位为分钟
 * @author var_rain
 * @date 2018/05/30
 */
public class CacheInterceptor implements Interceptor {
    private DBCacheManager manager;

    /**
     *
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String key = analyzeKey(request);

        int cacheDuration = 0;
        String localCache = request.header("localCache");
        try {
            cacheDuration = Integer.parseInt(localCache);
        } catch (Exception e){
            e.printStackTrace();
        }
        String cache = queryDataInValidTime(key,cacheDuration);
        if(!TextUtils.isEmpty(cache)){
            try {
                if(new JSONObject(cache).optInt("code",-1) == 0){
                    return build(request,cache);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Response response = chain.proceed(request);
            if(response != null){
                ResponseBody body = response.body();
                if(body != null){
                    String json = body.string();
                    saveData(key,json);
                    response = build(request,json);
                }
            }
            return response;
        } catch (Exception e){
            e.printStackTrace();
            String data = queryData(key);
            if(data.length() > 0){
                return build(request,data);
            } else {
                throw new IOException(e);
            }
        }
    }

    /**
     * 储存数据
     *
     * @param key   键值
     * @param value 数据
     */
    private void saveData(String key, String value) {
        if (manager == null) {
            manager = new DBCacheManager();
        }
        manager.saveCache(key, value);
    }

    /**
     * 查询数据
     *
     * @param key 键值
     * @return 如果有数据则返回对应的数据, 没有对应的数据则返回空字符串
     */
    private String queryData(String key) {
        if (manager == null) {
            manager = new DBCacheManager();
        }
        return manager.getValueByKey(key);
    }

    /**
     *
     */
    private String queryDataInValidTime(String key,int minute){
        if(manager == null){
            manager = new DBCacheManager();
        }
        return manager.getValueByKeyAndValidity(key,minute * 60 * 1000);
    }

    /**
     * 通过字符串数据构建返回对象
     *
     * @param request 请求对象
     * @param data    字符串数据
     * @return 返回一个Response对象
     */
    private Response build(Request request, String data) {
        ResponseBody body = ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), data);
        return new Response.Builder()
                .code(200)
                .message("OK")
                .request(request)
                .body(body)
                .protocol(Protocol.HTTP_1_1)
                .build();
    }

    /**
     * 网络状态判断
     *
     * @return 当前网络可用时返回true, 不可用时返回false
     */
    private boolean haveNetwork() {
        ConnectivityManager manager = (ConnectivityManager) ReaderApplication.instance
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo mNetworkInfo = manager.getActiveNetworkInfo();
            return mNetworkInfo != null && mNetworkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 解析Key
     *
     * @param request 请求对象
     * @return 如果本地存在, 则返回本地缓存数据的KEY,如果本地不存在相应数据则返回NULL
     */
    private String analyzeKey(Request request) {
        String params = null;
        try {
            params = analyzeParams(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (params != null && params.length() > 0) {
            params = params.substring(0, params.lastIndexOf("&cdt="));
            return analyzeUrl(request) + "?" + params;
        } else {
            return analyzeUrl(request);
        }
    }

    /**
     * 解析请求地址
     *
     * @param request 请求对象
     * @return 返回该次请求的目标地址
     */
    private String analyzeUrl(Request request) {
        return request.url().toString();
    }

    /**
     * 解析请求参数
     *
     * @param request 请求对象
     * @return 如果有请求参数则返回参数的字符串, 如果没有请求参数则返回空字符串
     * @throws IOException 该操作会引发IOException
     */
    private String analyzeParams(Request request) throws IOException {
        RequestBody body = request.body();
        if (body == null) return "";
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        Charset charset = Charset.forName("UTF-8");
        MediaType type = body.contentType();
        if (type != null) {
            charset = type.charset();
        }
        return charset == null ? "" : buffer.readString(charset);
    }
}
