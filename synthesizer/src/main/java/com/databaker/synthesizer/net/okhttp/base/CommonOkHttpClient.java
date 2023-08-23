package com.databaker.synthesizer.net.okhttp.base;

import com.baker.sdk.basecomponent.util.HLogger;
import com.databaker.synthesizer.net.okhttp.interceptor.HttpLoggingInterceptor;
import com.databaker.synthesizer.net.okhttp.interceptor.Logger;
import com.databaker.synthesizer.util.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author yanteng on 2020/8/20.
 * @Email 1019395018@qq.com
 */

public class CommonOkHttpClient {
    private static final int connectTimeout = 30;
    private static final int readTimeout = 30;
    private static final int writeTimeout = 30;
    private static OkHttpClient mClient;

    public static synchronized OkHttpClient init() {
        if (null == mClient) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
            clientBuilder.readTimeout(readTimeout, TimeUnit.SECONDS);
            clientBuilder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
            // 允许重定向
            clientBuilder.followRedirects(true);
            //添加请求头
            clientBuilder.addInterceptor(headerInterceptor);
            //拦截器
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(logging);

            // https支持
            clientBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
            mClient = clientBuilder.build();
        }
        return mClient;
    }

    public static Call sendRequest(Request request, CallbackListener listener) {
        Call call = init().newCall(request);
        call.enqueue(new CommonJSONCallBack(listener));
        return call;
    }

    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new Logger() {
        @Override
        public void log(String message) {
            HLogger.d(message);
        }
    });
    /**
     * 设置头信息
     */
    private static final Interceptor headerInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            String nounce, timestamp, signature;

            nounce = String.valueOf(Util.random6num());
            timestamp = String.valueOf(System.currentTimeMillis() / 1000);

            Map<String, String> params = new HashMap<>();
            params.put("nounce", nounce);
            params.put("timestamp", timestamp);
            signature = Util.genSignature("", nounce, params);

            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    //所有header不能传null
                    .addHeader("nounce", nounce)
                    .addHeader("timestamp", timestamp)
                    .addHeader("signature", signature)
                    .method(originalRequest.method(), originalRequest.body());

            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    };
}
