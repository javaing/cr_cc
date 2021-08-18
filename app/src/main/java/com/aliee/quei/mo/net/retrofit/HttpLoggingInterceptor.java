package com.aliee.quei.mo.net.retrofit;

import android.util.Log;

import com.aliee.quei.mo.utils.FileUtils;
import com.elvishew.xlog.XLog;
import com.aliee.quei.mo.component.GsonProvider;
import com.aliee.quei.mo.data.bean.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @Author: YangYang
 * @Date: 2017/6/1
 * @Version: 1.0.0
 * @Description:
 */
public final class HttpLoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        //request

        Long requestTime = System.currentTimeMillis();
        Request request = chain.request();
        RequestBody requestBody = request.body();
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        Charset charset = StandardCharsets.UTF_8;
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
            contentType.charset(charset);
        }
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE);
        Buffer buffer2 = source.buffer();
        MediaType contentType2 = responseBody.contentType();
        if (contentType2 != null) {
            charset = contentType2.charset(charset);
        }

        String url = request.url().toString();
        String params = buffer.readString(charset);
        Headers headers = request.headers();
        String responseS = buffer2.clone().readString(charset);

        BaseResponse<String> responseObj = null;
        String data = "";
        try {
            responseObj = GsonProvider.INSTANCE.getGson().fromJson(responseS, BaseResponse.class);
            data = responseObj.getData();
        } catch (Exception e) {
        }

        StringBuilder builder = new StringBuilder();
        builder.append("url:\t");
        builder.append(url);
        builder.append("\n");
        if (!url.contains("uploads")) {
            builder.append("params:\t");
            builder.append(params);
            builder.append("\n");
            String originalParams = "";
            try {
                JSONObject jsonObject = new JSONObject(params);
//                originalParams = Base64Helper.decodeToString(jsonObject.getString("data"));
                originalParams = jsonObject.getString("data");
            } catch (JSONException e) {
            }
            builder.append("originalParams:\t");
            builder.append(originalParams);
            builder.append("\n");
        }
        builder.append("headers:\t");
        builder.append(headers.toString());
        if (headers.names().isEmpty()) {
            builder.append("\n");
        } else if (headers.names().contains("baseParam")) {
            builder.append("originalBaseParams:\t");
//            builder.append(Base64Helper.decodeToString(headers.values("baseParam").get(0)));
            builder.append(headers.values("baseParam").get(0));
            builder.append("\n");
        }
        builder.append("================================response=================================\n");
        Long responseTime = System.currentTimeMillis();
        if (responseObj != null) {
            builder.append("response time:" + System.currentTimeMillis());
            builder.append("\n");
            builder.append("url:" + response.request().url().toString());
            builder.append("\n");
            builder.append("HttpCode:" + response.code());
            builder.append("\n");
            builder.append("\t code:");
            builder.append(responseObj.getCode());
            builder.append("\n");
        }
        builder.append("response :\t");
        builder.append(responseS);
        builder.append("\n");
        if (responseObj != null) {
            builder.append("decodeData:\t");
            builder.append(data);
            builder.append("\n");
        }
        XLog.st(1).i(builder.toString());
        Log.d("HttpLoggingInterceptor", "HttpRequest:" + builder.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("================================REQUEST_TIME_START=================================\n")
                .append("url:")
                .append(url)
                .append("\n")
                .append("requestTime:")
                .append(requestTime)
                .append("\n")
                .append("responseTime:")
                .append(responseTime)
                .append("\n")
                .append("================================REQUEST_TIME_END=================================\n");
        FileUtils.writeLog("/mh_http.txt", sb.toString());

        return response;
    }


}
