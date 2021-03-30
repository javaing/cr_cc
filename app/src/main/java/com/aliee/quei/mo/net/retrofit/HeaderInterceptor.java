package com.aliee.quei.mo.net.retrofit;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.aliee.quei.mo.BuildConfig;
import com.aliee.quei.mo.application.ReaderApplication;
import com.aliee.quei.mo.component.CommonDataProvider;
import com.aliee.quei.mo.data.Channel;
import com.aliee.quei.mo.data.bean.BaseParam;
import com.aliee.quei.mo.utils.DeviceInfoUtil;
import com.aliee.quei.mo.utils.MD5Util;
import com.aliee.quei.mq.DeviceInfo;
import com.elvishew.xlog.XLog;
import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * @Author: YangYang
 * @Date: 2017/6/1
 * @Version: 1.0.0
 * @Description:配置请求头信息的Interceptor
 */
public final class HeaderInterceptor implements Interceptor {

    /**
     * 请求头参数基础参数
     */
    private static final String HEADER_BASE_PARAM = "baseParam";

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static String BASE_PARAM;

    private Response response;
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String method = originalRequest.method();
        if (method.equals("POST")){
        Request.Builder builder = chain.request().newBuilder();
        String timeStamp = String.valueOf(new Date().getTime());
        //request
        Request request = chain.request();
        RequestBody requestBody = request.body();
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
            contentType.charset(charset);
        }
        String urlPath = request.url().url().getPath();
        TreeMap<String, String> map = transStringToMap(buffer.readString(charset));
        Log.d("tag", "map:" + map.toString());
        map.put("timestamp", timeStamp);
        String stringA = null;
        if (urlPath.equals("/2/cartoon/cartoon/search")){
            //漫画搜索
            stringA = transMapToString(map).replace("%2C", ",");
        }else{
            stringA = URLDecoder.decode(transMapToString(map).replace("%2C", ","));
        }
        Log.d("tag", "map:" + stringA);
        String stringSignTemp = stringA + "&key=Cartoon$2019&#";
        XLog.e("msg---params:" + URLDecoder.decode(stringSignTemp));
        String sign = MD5Util.md5(stringSignTemp.getBytes()).toUpperCase();
        builder.addHeader(HEADER_BASE_PARAM, BASE_PARAM);
        builder.addHeader(HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
        builder.addHeader("User-Agent", "Android");
        builder.addHeader("Cookie", readCookie());
        builder.addHeader("Access-Token", CommonDataProvider.Companion.getInstance().getToken());
        builder.addHeader("timestamp", timeStamp);
        builder.addHeader("sign", sign);
    /*    Pair<String,String> xp = DeviceInfo.getDeviceId(ReaderApplication.instance.getApplicationContext(),Long.valueOf(timeStamp));
        builder.addHeader("Access-Sign", xp.second.trim());
        builder.addHeader("Access-Key", xp.first.trim());*/
        response = chain.proceed(builder.build());
        saveCookie(response.header("Set-Cookie"));
        }else{
            response = chain.proceed(originalRequest);
        }
        return response;
    }

    private void saveCookie(String cookie) {
        MMKV.mmkvWithID("http").putString("cookie", cookie);
    }

    private String readCookie() {
        return MMKV.mmkvWithID("http").getString("cookie", "");
    }

    public static void initBaseParam(Context context) {
        BaseParam baseParam = new BaseParam();
        String imei;
        String imsi;
        try {
            imei = DeviceInfoUtil.getIMEI(context);
        } catch (Exception e) {
            imei = null;
        }
        if (null == imei || imei.equals("")) {
            imei = DeviceInfoUtil.getDeviceID(context);
            imsi = DeviceInfoUtil.getDeviceID(context);
        } else {
            imei = DeviceInfoUtil.getIMEI(context);
            imsi = DeviceInfoUtil.getIMSI(context);
        }
        baseParam.setImei(imei);
        baseParam.setImsi(imsi);
        baseParam.setMac(DeviceInfoUtil.getWifiMAC(context));
        baseParam.setClientVersion(BuildConfig.VERSION_NAME);
        baseParam.setModel(DeviceInfoUtil.getModel());
        baseParam.setBrand(DeviceInfoUtil.getBrand());
        baseParam.setVersion(BuildConfig.API_VERSION);
        baseParam.setDeviceId(DeviceInfoUtil.getDeviceID(ReaderApplication.Companion.getInstance()));
        baseParam.setRefId(Channel.INSTANCE.getRefId());
        baseParam.setTname("");
        BASE_PARAM = new Gson().toJson(baseParam);
        Log.d("msg---params:", "Base Param" + BASE_PARAM);
    }

    public TreeMap<String, String> transStringToMap(String mapString) {
        TreeMap<String, String> map = new TreeMap<>();
        java.util.StringTokenizer items;
        for (StringTokenizer entrys = new StringTokenizer(mapString, "&"); entrys.hasMoreTokens();
             map.put(items.nextToken(), items.hasMoreTokens() ? ((String) (items.nextToken())) : null))
            items = new StringTokenizer(entrys.nextToken(), "=");
        return map;
    }

    public static String transMapToString(TreeMap map) {
        java.util.Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            entry = (java.util.Map.Entry) iterator.next();
            sb.append(entry.getKey().toString()).append("=").append(null == entry.getValue() ? "" :
                    entry.getValue().toString()).append(iterator.hasNext() ? "&" : "");
        }
        return sb.toString();
    }


}
