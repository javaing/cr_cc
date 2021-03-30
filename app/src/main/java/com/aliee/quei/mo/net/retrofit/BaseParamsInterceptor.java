package com.aliee.quei.mo.net.retrofit;

import com.aliee.quei.mo.BuildConfig;
import com.aliee.quei.mo.application.ReaderApplication;
import com.aliee.quei.mo.component.CommonDataProvider;
import com.aliee.quei.mo.data.BeanConstants;
import com.meituan.android.walle.WalleChannelReader;
import com.aliee.quei.mo.ui.user.activity.ShareActivity;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BaseParamsInterceptor implements Interceptor {
    public static final MediaType FORM_CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private Response response;

    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取原始的Requset请求
        Request originalRequest = chain.request();
        //获取请求的方法
        String method = originalRequest.method();
        //判断是GET请求还是POST请求
        if ("GET".equals(method)) {
           /* HttpUrl httpUrl = originalRequest.url()
                    .newBuilder()
                    .addQueryParameter("linkid", WalleChannelReader.getChannel(ReaderApplication.instance, "0"))
                    .build();*/
            HttpUrl httpUrl = originalRequest.url().newBuilder().build();
            //获取添加公共参数之后的requset对象
            Request request = new Request.Builder().url(httpUrl).build();
            //发送拼接完成后的请求
            response = chain.proceed(request);
        } else if ("POST".equals(method)) {
            RequestBody requestBody = originalRequest.body();
            if (requestBody instanceof FormBody) {
                FormBody.Builder builder = new FormBody.Builder();
                FormBody originalFormBody = (FormBody) originalRequest.body();
                for (int i = 0; i < originalFormBody.size(); i++) {
                    builder.add(originalFormBody.name(i), originalFormBody.value(i));
                }
               /* String urlPath = originalRequest.url().url().getPath();
                if (urlPath.equals("/2/cartoon/domain/get")&& originalFormBody.name(0).equals("type") && originalFormBody.value(0).equals("2")) {
                    response = chain.proceed(originalRequest);
                }else{*/
                builder.add("linkid", WalleChannelReader.getChannel(ReaderApplication.instance, "98"));
               //builder.add("linkid", "100");
                builder.add("app", "2");
                builder.add("lang", BeanConstants.INSTANCE.LANG_CH + "");
                builder.add("appver", BuildConfig.VERSION_NAME);
                FormBody formBody = builder.build();
                Request request = originalRequest.newBuilder().post(formBody).build();
                response = chain.proceed(request);
                //   }
            } else {
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("linkid", WalleChannelReader.getChannel(ReaderApplication.instance, "98"));
                builder.add("app", "2");
                builder.add("lang", BeanConstants.INSTANCE.LANG_CH + "");
                builder.add("appver", BuildConfig.VERSION_NAME);
                Request request = originalRequest.newBuilder().post(builder.build()).build();
                response = chain.proceed(request);
            }
        }
        return response;
    }
}
