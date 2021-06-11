package com.baker.sdk.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @author hsj55
 * 2020/9/17
 */
public class WebSocketClient {
    private Request request;
    private OkHttpClient client;
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

    public void start(WebSocketListener listener) {
        cancelSocket = webSocket;
        client.dispatcher().cancelAll();
        webSocket = client.newWebSocket(request, listener);
    }

    public void stop() {
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
        client.dispatcher().executorService().shutdown();
    }
}
