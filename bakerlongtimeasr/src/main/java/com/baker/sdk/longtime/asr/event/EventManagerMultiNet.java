package com.baker.sdk.longtime.asr.event;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.baker.sdk.basecomponent.util.GsonConverter;
import com.baker.sdk.basecomponent.util.HLogger;
import com.baker.sdk.http.BakerHttpConstants;
import com.baker.sdk.http.WebSocketClient;
import com.baker.sdk.longtime.asr.base.BakerLongTimeAsrConstants;
import com.baker.sdk.longtime.asr.base.BakerPrivateConstants;
import com.baker.sdk.longtime.asr.bean.LongTimeAsrBaseParams;
import com.baker.sdk.longtime.asr.bean.LongTimeAsrError;
import com.baker.sdk.longtime.asr.bean.LongTimeAsrParams;
import com.baker.sdk.longtime.asr.bean.LongTimeAsrResponse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static com.baker.sdk.longtime.asr.base.BakerPrivateConstants.baseUrl;
import static com.baker.sdk.longtime.asr.base.BakerPrivateConstants.bufferSizeForUpload;

/**
 * @author hsj55
 * 2020/9/24
 */
public class EventManagerMultiNet implements EventManager {
    private EventManager mOwner;
    private WebSocketClient webSocketClient;
    private LongTimeAsrBaseParams baseParams = new LongTimeAsrBaseParams();
    private LongTimeAsrParams longTimeAsrParams = new LongTimeAsrParams();
    private AtomicInteger mIdx = new AtomicInteger(-1);
    private boolean isLast = false;
    private String webSocketUrl;
    private int sampleRate = 16000;
    private boolean addPct = true;
    private String domain = "common";
    private String audioFormat = "pcm";

    //1=sdk麦克风录音 2=接收字节流
    private int type;

    public void setmOwner(EventManager mOwner) {
        this.mOwner = mOwner;
    }

    public void setUrl(String url) {
        webSocketUrl = url;
    }

    @Override
    public void send(String name, byte[] data, String params) {
        switch (name) {
            case "net.start":
                if (!TextUtils.isEmpty(params)) {
                    LongTimeAsrParams asrParams = GsonConverter.fromJson(params, LongTimeAsrParams.class);
                    audioFormat = asrParams.getAudio_format();
                    sampleRate = asrParams.getSample_rate();
                    addPct = asrParams.isAdd_pct();
                    domain = asrParams.getDomain();
                    type = asrParams.getType();
                }
                if (webSocketClient == null) {
                    if (!TextUtils.isEmpty(webSocketUrl)) {
                        webSocketClient = new WebSocketClient(webSocketUrl);
                    } else {
                        webSocketClient = new WebSocketClient(baseUrl);
                    }
                }
//                time_connect = System.currentTimeMillis();
//                show = true;
//                first = true;
                webSocketClient.start(listener);
                mIdx.set(-1);
                isLast = false;
                stringBuilder.delete(0, stringBuilder.length());
                break;
            case "net.upload":
                send();
                break;
            case "net.disconnect":
                release();
                break;
            default:
                break;
        }
    }

//    private long time_connect;
//    private long time_send;
//    private boolean show = true;
//    private boolean first = true;
//    private long time_last;

    private void send() {
        try {
            mIdx.addAndGet(1);
            byte[] data = BakerPrivateConstants.dataQueue.poll(300, TimeUnit.MILLISECONDS);
            longTimeAsrParams.setAudio_data(Base64.encodeToString(data, Base64.NO_WRAP));
            if (type == 2) {
                longTimeAsrParams.setAudio_format(audioFormat);
            }
            longTimeAsrParams.setSample_rate(sampleRate);
            longTimeAsrParams.setAdd_pct(addPct);
            longTimeAsrParams.setDomain(domain);
            if (data == null || data.length < bufferSizeForUpload) {
//                Log.d("hsj", "Req_idx = -1 服务器");
                longTimeAsrParams.setReq_idx(mIdx.get() * -1);
                isLast = true;
//                time_last = System.currentTimeMillis();
            } else {
//                Log.d("hsj", "data.length = " + data.length);
                longTimeAsrParams.setReq_idx(mIdx.get());
            }
//            Log.d("hsj", "上传id:" + longTimeAsrParams.getReq_idx());
            if (webSocketClient != null) {
                String token;
                if (!TextUtils.isEmpty(webSocketUrl)) {
                    token = "default";
                } else {
//                    token = BakerHttpConstants.getAuthorInfoByClientId(BakerPrivateConstants.clientId).getAccessToken();
                    token = BakerPrivateConstants.token;
                    if (TextUtils.isEmpty(token)) {
                        onFault(BakerLongTimeAsrConstants.ERROR_CODE_INIT_FAILED_TOKEN_FAULT, "The token of long time asr sdk is null.");
                        return;
                    }
                }
                baseParams.setAccess_token(token);
                baseParams.setAsr_params(longTimeAsrParams);
                String params = GsonConverter.toJson(baseParams);
//                Log.d("hsj","上传id：" + longTimeAsrParams.getReq_idx() + ", 已运行：" + (System.currentTimeMillis() - time));
                if (webSocketClient.getWebSocket() != null) {
//                    if (first) {
//                        time_send = System.currentTimeMillis();
//                        first = false;
//                    }
                    webSocketClient.getWebSocket().send(params);
                }
            }
        } catch (Exception e) {
            onFault(BakerLongTimeAsrConstants.ERROR_CODE_WEBSOCKET_SEND_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    private WebSocketListener listener = new WebSocketListener() {
        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            //1=sdk麦克风录音 2=接收字节流
//            Log.d("waste_time", "webSocket连接耗时：" + (System.currentTimeMillis() - time_connect));
            if (type == 1) {
                EventManagerMessagePool.offer(mOwner, "net.start-called-1");
            } else if (type == 2) {
                EventManagerMessagePool.offer(mOwner, "net.start-called-2");
            }
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            if (webSocketClient != null && webSocketClient.getCancelSocket() != null && !webSocket.equals(webSocketClient.getCancelSocket())) {
                onFault(BakerLongTimeAsrConstants.ERROR_CODE_WEBSOCKET_ONFAILURE, t.getMessage());
            }
            HLogger.d("onClosing, error message = " + t.getMessage());
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
//            if (show){
//                Log.d("waste_time", "发送首包后，首次返回耗时：" + (System.currentTimeMillis() - time_send));
//                show = false;
//            }
//            Log.d("hsj", "onMessage, text = " + text);
            HLogger.d("onMessage, text = " + text);
            if (!TextUtils.isEmpty(text)) {
                LongTimeAsrResponse response = GsonConverter.fromJson(text, LongTimeAsrResponse.class);
                if (response != null) {
                    if (response.getCode() == 90000 || response.getCode() == 0) {
//                        if (response.getEnd_flag() == 1){
//                            Log.d("waste_time", "尾包耗时：" + (System.currentTimeMillis() - time_last));
//                        }
                        EventManagerMessagePool.offer(mOwner, "asr.partial", text);
//                        if (!isLast) {
//                            EventManagerMessagePool.offer(mOwner, "asr.partial", stringBuilder.toString() + response.getAsr_text());
//                            if ("true".equals(response.getSentence_end())) {
//                                stringBuilder.append(response.getAsr_text());
//                            }
//                        } else {
//                            EventManagerMessagePool.offer(mOwner, "asr.finish", stringBuilder.toString() + response.getAsr_text());
//                            webSocket.close(1001, null);
//                        }
                    } else {
                        onFault(String.valueOf(response.getCode()), "trace_id is " + response.getTrace_id() + ", " + response.getMessage());
                        webSocket.close(1001, null);
                    }
                } else {
                    onFault(BakerLongTimeAsrConstants.ERROR_CODE_GSON_ERROR, "Response from text is null.");
                    webSocket.close(1001, null);
                }
            } else {
                onFault(BakerLongTimeAsrConstants.ERROR_CODE_RESPONSE_NULL, "onMessage text is null.");
                webSocket.close(1001, null);
            }
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            HLogger.d("onMessage, bytes return.");
        }

    };

    private StringBuilder stringBuilder = new StringBuilder();

    private void onFault(String code, String message) {
        EventManagerMessagePool.offer(mOwner, "net.error", GsonConverter.toJson(new LongTimeAsrError(code, message)));
    }

    public void release() {
        if (webSocketClient.getWebSocket() != null) {
            webSocketClient.getWebSocket().close(1001, null);
        }
    }
}
