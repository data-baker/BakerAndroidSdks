package com.baker.engrave.lib.net;

import com.baker.engrave.lib.BakerVoiceEngraver;
import com.baker.engrave.lib.bean.RecordingSocketBean;
import com.baker.engrave.lib.util.HLogger;
import com.baker.engrave.lib.util.WebSocketUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {

    private WebSocket webSocket;

    private WebSocketClient() {
    }


    private static class Singleton {
        private static final WebSocketClient socketClient = new WebSocketClient();
    }

    public static WebSocketClient getInstance() {
        return Singleton.socketClient;
    }

    private Request request;
    private OkHttpClient client;

    public void newClient(RecordingSocketBean.ParamBean paramBean, RecordingSocketBean.AudioBean audioBean) {
        String data = "";
        data = new Gson().toJson(WebSocketUtil.formatParameters(paramBean, audioBean));
        HLogger.d("newClient " + data);
        HttpUrl httpUrl = HttpUrl.parse(NetConstants.BASE_URL + "/websocket/" + NetConstants.VERSION)
                .newBuilder()
                .addQueryParameter("data", URLEncoder.encode(data)).
                        build();
        String url = httpUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        request = new Request.Builder().url(url).build();
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//            @Override
//            public void log(@NotNull String s) {
//                HLogger.v("ws=>" + s);
//            }
//        });
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient().newBuilder()
//                .addInterceptor(loggingInterceptor)
                .build();
    }




    public WebSocket newWebSocket(WebSocketListener webSocketListener) {
        return client.newWebSocket(request, webSocketListener);
    }

}
