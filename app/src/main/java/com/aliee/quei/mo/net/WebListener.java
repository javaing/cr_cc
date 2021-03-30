package com.aliee.quei.mo.net;

import java.io.IOException;

import okhttp3.Response;

public interface WebListener {

        void onResponse(Response response);
        void onErrorResponse(IOException ErrorResponse);


}
