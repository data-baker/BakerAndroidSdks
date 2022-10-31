package com.databaker.synthesizer.net.okhttp;


import com.baker.sdk.basecomponent.util.HLogger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Create by hsj55
 * 2019/11/27
 */
public class WebSocketClient {
    private final Request request;
    private final OkHttpClient client;
    private WebSocket webSocket;
    private int connectTimeOut = 10;
    //    private final String baseUrl = "ws://echo.websocket.org";
//    private final String baseUrl = "ws://192.168.1.21:9003";
//    private final String baseUrl = "wss://openapitest.data-baker.com/wss";
    private final String baseUrl = "wss://openapi.data-baker.com/wss";

    public WebSocketClient() {
        client = new OkHttpClient().newBuilder().retryOnConnectionFailure(true)
                .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
                .build();
        request = new Request.Builder()
                .url(baseUrl)
                .build();
    }

    public WebSocketClient(int timeOut) {
        connectTimeOut = timeOut;
        client = new OkHttpClient().newBuilder().retryOnConnectionFailure(true)
                .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
                .build();
        request = new Request.Builder()
                .url(baseUrl)
                .build();
    }

    public WebSocketClient(String url) {
        client = new OkHttpClient().newBuilder().retryOnConnectionFailure(true)
                .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
                .build();
        request = new Request.Builder()
                .url(url)
                .build();
    }

    public WebSocketClient(int timeOut, String url) {
        connectTimeOut = timeOut;
        client = new OkHttpClient().newBuilder().retryOnConnectionFailure(true)
                .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
                .build();
        request = new Request.Builder()
                .url(url)
                .build();
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    private WebSocket cancelSocket;

    public WebSocket getCancelSocket() {
        return cancelSocket;
    }

    /**
     * 在token过期时候，服务器主动与客户端断开连接，socket的onFailure方法会报一次错误。避免把此错误抛给上层调用者，
     */
    public void setCancelSocketNull() {
        cancelSocket = null;
    }

    public void start(WebSocketListener listener) {
        cancelSocket = webSocket;
        client.dispatcher().cancelAll();
        webSocket = client.newWebSocket(request, listener);
        HLogger.d("webSocket.id==" + webSocket);
        if (cancelSocket != null) {
            HLogger.d("webSocket.id==" + webSocket.toString() + "cancelSocket==" + cancelSocket.toString());
        }
    }

    public void stop() {
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
        client.dispatcher().executorService().shutdown();
    }
}
