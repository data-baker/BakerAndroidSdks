package com.baker.sdk.http;

import com.baker.sdk.basecomponent.util.HLogger;
import com.baker.sdk.http.interceptor.HeaderInterceptor;
import com.baker.sdk.http.interceptor.HttpLoggingInterceptor;
import com.baker.sdk.http.interceptor.Logger;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author hsj55
 * 2020/9/17
 */
public class CommonOkHttpClient {
    private static int connectTimeout = 30;
    private static int readTimeout = 30;
    private static int writeTimeout = 30;
    private static OkHttpClient mClient;

    public static synchronized OkHttpClient init() {
        if (null == mClient) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
            clientBuilder.readTimeout(readTimeout, TimeUnit.SECONDS);
            clientBuilder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
            // 允许重定向
            clientBuilder.followRedirects(true);

//            clientBuilder.addInterceptor(new HeaderInterceptor());
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

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new Logger() {
        @Override
        public void log(String message) {
            HLogger.d(message);
        }
    });
}
