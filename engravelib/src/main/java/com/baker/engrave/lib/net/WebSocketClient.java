package com.baker.engrave.lib.net;

import android.util.Log;

import com.baker.engrave.lib.bean.RecordingSocketBean;
import com.baker.engrave.lib.net.interceptor.HttpLoggingInterceptor;
import com.baker.engrave.lib.util.LogUtil;
import com.baker.engrave.lib.util.WebSocketUtil;
import com.google.gson.Gson;

import java.net.URLEncoder;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {


    private WebSocketClient() {
    }


    private static class Singleton {
        private static final WebSocketClient socketClient = new WebSocketClient();
    }

    public static WebSocketClient getInstance() {
        return Singleton.socketClient;
    }

    private final String LOG_TAG = "HttpUtils>>>";
    private final String LOG_DIVIDER = "||=================================================================";
    private Request request;
    private OkHttpClient client;

    public void newClient(RecordingSocketBean.ParamBean paramBean, RecordingSocketBean.AudioBean audioBean) {
        String data = "";
        data = new Gson().toJson(WebSocketUtil.formatParameters(paramBean, audioBean));
        LogUtil.d("newClient " + data);
        HttpUrl httpUrl = HttpUrl.parse(NetConstants.BASE_URL + "websocket/fuke/" + NetConstants.VERSION)
                .newBuilder()
                .addQueryParameter("data", URLEncoder.encode(data))
                .build();
        String url = httpUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        LogUtil.e("url:" + url);
        request = new Request.Builder().url(url).build();
        client = new OkHttpClient().newBuilder()
              /*  .addInterceptor(new HttpLoggingInterceptor(message -> {
                    if (message.contains("--> END") || message.contains("<-- END")) {
                        Log.e(LOG_TAG, "||  " + message);
                        Log.e(LOG_TAG, LOG_DIVIDER);
                    } else if (message.contains("-->") || message.contains("<--")) {
                        Log.e(LOG_TAG, LOG_DIVIDER);
                        Log.e(LOG_TAG, "||  " + message);
                    } else {
                        Log.e(LOG_TAG, "||  " + message);
                    }
                }).setLevel(HttpLoggingInterceptor.Level.BODY))*/
                .build();
    }


    public WebSocket newWebSocket(WebSocketListener webSocketListener) {
        return client.newWebSocket(request, webSocketListener);
    }

}
