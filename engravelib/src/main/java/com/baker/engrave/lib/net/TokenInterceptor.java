package com.baker.engrave.lib.net;

import com.baker.engrave.lib.bean.BaseRespForNoData;
import com.baker.engrave.lib.util.HLogger;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * token 失效拦截器
 */
public class TokenInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.peekBody(Long.parseLong(1024 * 1024 + ""));
        BaseRespForNoData resp = new Gson().fromJson(responseBody.string(), BaseRespForNoData.class);
        if (resp != null && NetConstants.RESULT_CODE_TOKEN_EXPIRE.equals(resp.getCode())) {
            //Token失效
            HLogger.d("token 失效了");
            //请求token
            NetUtil.requestToken();
            ConcurrentHashMap<String, String> headers = NetUtil.getHeaders();
            Headers.Builder mHeaderBuild = new Headers.Builder();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                mHeaderBuild.add(entry.getKey(), entry.getValue());
            }
            Request newRequest = chain.request().newBuilder().headers(mHeaderBuild.build()).build();
            return chain.proceed(newRequest);
        }
        return response;
    }
}