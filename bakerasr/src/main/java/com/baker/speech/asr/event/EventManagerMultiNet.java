package com.baker.speech.asr.event;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.baker.sdk.basecomponent.util.GsonConverter;
import com.baker.sdk.basecomponent.util.HLogger;
import com.baker.sdk.basecomponent.writelog.WriteLog;
import com.baker.sdk.http.BakerHttpConstants;
import com.baker.sdk.http.BakerTokenManager;
import com.baker.sdk.http.CallbackListener;
import com.baker.sdk.http.WebSocketClient;
import com.baker.speech.asr.BakerPrivateConstants;
import com.baker.speech.asr.base.BakerAsrConstants;
import com.baker.speech.asr.bean.AsrParams;
import com.baker.speech.asr.bean.BakerException;
import com.baker.speech.asr.bean.BaseResponse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static com.baker.speech.asr.BakerPrivateConstants.bufferSizeForUpload;

/**
 * @author hsj55
 * 2021/2/2
 */
public class EventManagerMultiNet implements EventManager {
    private EventManager mOwner;
    private WebSocketClient webSocketClient;
    private AtomicInteger mIdx = new AtomicInteger(-1);
    private String domain = "common";
    private String mTtsToken;
    private boolean isFinish = false;
    private String url = BakerPrivateConstants.baseUrl;
    //1=sdk麦克风录音 2=接收字节流
    private int type;
    private String audioFormat = "pcm";
    private int sampleRate = 16000;
    private boolean addPct = true;

    public void setmOwner(EventManager mOwner) {
        this.mOwner = mOwner;
    }

    public void setUrl(String u) {
        if (!url.equals(u)) {
            this.url = u;
            webSocketClient = null;
            webSocketClient = new WebSocketClient(url);
        }
    }

    @Override
    public void send(String name, byte[] data, String params) {
        switch (name) {
            case "net.start":
                if (BakerPrivateConstants.baseUrl.equals(url)) {
                    mTtsToken = BakerPrivateConstants.token;
                } else {
                    mTtsToken = "default";
                }
                if (TextUtils.isEmpty(mTtsToken)) {
                    onFault(BakerAsrConstants.ERROR_CODE_INIT_FAILED_TOKEN_FAULT, "The token of asr sdk is null.");
                    return;
                }

                if (!TextUtils.isEmpty(params)) {
                    AsrParams asrParams = GsonConverter.fromJson(params, AsrParams.class);
                    type = asrParams.getType();
                    domain = asrParams.getDomain();
                    addPct = asrParams.isAdd_pct();
                    audioFormat = asrParams.getAudio_format();
                    sampleRate = asrParams.getSample_rate();
                }
                if (webSocketClient == null) {
                    webSocketClient = new WebSocketClient(url);
//                    Log.e("hsj", "url request = " + url);
//                    webSocketClient = new WebSocketClient("ws://192.168.1.21:9002");
                }
                webSocketClient.start(listener);
                mIdx.set(-1);
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

    private void send() {
        try {
            mIdx.addAndGet(1);

            Map<String, Object> hashMapParams = new HashMap<>();
            hashMapParams.put("access_token", mTtsToken);
            hashMapParams.put("version", "1.0");
            Map<String, Object> asrParams = new HashMap<>();

            byte[] data = BakerPrivateConstants.dataQueue.poll(300, TimeUnit.MILLISECONDS);
            //音频数据，使用base64加密
            if (data != null && data.length > 0) {
                asrParams.put("audio_data", Base64.encodeToString(data, Base64.NO_WRAP));
            } else {
                asrParams.put("audio_data", "");
            }
            //音频编码格式，支持pcm, wav, bv32
            asrParams.put("audio_format", audioFormat);
            //音频采样率
            asrParams.put("sample_rate", sampleRate);
            //音频序号索引，步长为1递增 0：起始音频帧  >0：中间音频帧  -n：结束音频帧
            if (data == null || data.length < bufferSizeForUpload) {
                asrParams.put("req_idx", mIdx.get() * -1);
            } else {
                asrParams.put("req_idx", mIdx.get());
            }
            //识别类型  0：一句话识别，sdk做vad   1：长语音识别，服务端做vad  默认为0
            asrParams.put("speech_type", 0);

            //------以下是非必须字段-----
            //是否在短静音处添加标点，默认false
            asrParams.put("add_pct", addPct);
            //是否在后处理中执⾏ITN，默认false
            asrParams.put("enable_itn", false);
            //模型名称
            asrParams.put("domain", domain);

            hashMapParams.put("asr_params", asrParams);
            String s = GsonConverter.toJson(hashMapParams);
            HLogger.longError("发送的数据：" + s);
//            Log.e("hsjreadsize", "发送的数据：" + s);
            WriteLog.writeLogs("EventManagerMultiNet:sendData2Net:" + s);

            if (webSocketClient != null) {
                if (webSocketClient.getWebSocket() != null) {
                    webSocketClient.getWebSocket().send(s);
                }
            }
        } catch (Exception e) {
            onFault(BakerAsrConstants.ERROR_CODE_WEBSOCKET_SEND_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    private WebSocketListener listener = new WebSocketListener() {
        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            //1=sdk麦克风录音 2=接收字节流
            if (type == 1) {
                EventManagerMessagePool.offer(mOwner, "net.start-called-1");
            } else if (type == 2) {
                EventManagerMessagePool.offer(mOwner, "net.start-called-2");
            }
            isFinish = false;
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            HLogger.d("onClosed, reason = " + reason);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
            HLogger.d("onClosing, reason = " + reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            if (webSocketClient != null && webSocketClient.getCancelSocket() != null
                    && !webSocket.equals(webSocketClient.getCancelSocket()) && !isFinish) {
                onFault(BakerAsrConstants.ERROR_CODE_WEBSOCKET_ONFAILURE, t.getMessage());
            }
            HLogger.d("onClosing, error message = " + t.getMessage());
            Log.e("onFailure", "onFailure, error message = " + t.getMessage() + ", webSocket = " + webSocket.toString());
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
//            Log.d("hsj", "onMessage, text = " + text);
            HLogger.d("onMessage, text = " + text);
            if (webSocketClient != null && webSocket.equals(webSocketClient.getWebSocket())) {
                if (!TextUtils.isEmpty(text)) {
                    try {
                        BaseResponse response = GsonConverter.fromJson(text, BaseResponse.class);
                        if (response.getCode() == 90000) {
                            //将外层的traceId给data对象，一起传回上层。
                            response.getData().setTraceId(response.getTrace_id());
                            if (response.getData().getEnd_flag() == 1) {
                                //最后确定的结果
                                EventManagerMessagePool.clean();
                                EventManagerMessagePool.offer(mOwner, "asr.finish", GsonConverter.toJson(response.getData()));
//                                Log.e("hsj", "finish webSocket id = " + webSocket.toString());
                                isFinish = true;
                                webSocket.close(1001, null);
                            } else {
                                EventManagerMessagePool.offer(mOwner, "asr.partial", GsonConverter.toJson(response.getData()));
                            }
                        } else if (response.getCode() == 40008) {
                            //token已过期
                            onFault(String.valueOf(response.getCode()), "errorMsg = "
                                    + response.getMessage() + ", trace_id = " + response.getTrace_id()
                                    + ", sid = " + response.getSid());
                            //调用授权
                            new BakerTokenManager().authentication(BakerPrivateConstants.clientId, BakerPrivateConstants.clientSecret, new CallbackListener<String>() {
                                @Override
                                public void onSuccess(String response) {
                                    BakerPrivateConstants.token = response;
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("BakerRecognizer", "baker asr sdk init token error, " + e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                            webSocket.close(1001, null);
                        } else {
                            onFault(String.valueOf(response.getCode()), "errorMsg = "
                                    + response.getMessage() + ", trace_id = " + response.getTrace_id()
                                    + ", sid = " + response.getSid());
                            webSocket.close(1001, null);
                        }
                    } catch (Exception e) {
                        onFault(BakerAsrConstants.ERROR_CODE_GSON_ERROR, e.getMessage());
                        HLogger.e("发生错误：" + e.getMessage());
                        webSocket.close(1001, null);
                    }
                } else {
                    onFault(BakerAsrConstants.ERROR_CODE_RESPONSE_NULL, "onMessage text is null.");
                    webSocket.close(1001, null);
                }
            }
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            HLogger.d("onMessage, bytes return.");
        }

    };

    private void onFault(String code, String message) {
        EventManagerMessagePool.offer(mOwner, "net.error", GsonConverter.toJson(new BakerException(code, message)));
    }

    public void release() {
        if (webSocketClient.getWebSocket() != null) {
            webSocketClient.getWebSocket().close(1001, null);
        }
    }
}
