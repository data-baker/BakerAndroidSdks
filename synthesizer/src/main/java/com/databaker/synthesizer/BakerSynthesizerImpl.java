package com.databaker.synthesizer;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.baker.sdk.basecomponent.BakerBaseConstants;
import com.baker.sdk.basecomponent.BakerSdkBaseComponent;
import com.baker.sdk.basecomponent.bean.BakerError;
import com.baker.sdk.basecomponent.util.GsonConverter;
import com.baker.sdk.basecomponent.util.HLogger;
import com.baker.sdk.basecomponent.writelog.WriteLog;
import com.databaker.synthesizer.bean.BaseResponse;
import com.databaker.synthesizer.net.okhttp.WebSocketClient;
import com.databaker.synthesizer.net.okhttp.base.CallbackListener;
import com.databaker.synthesizer.util.AuthenticationUtils;
import com.databaker.synthesizer.util.SynthesizerErrorUtils;
import com.databaker.synthesizer.util.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Create by hsj55
 * 2019/11/8
 *
 * @author hsj55
 */
class BakerSynthesizerImpl implements SynthesizerInterface {
    private WebSocketClient webSocketClient;
    private AuthenticationUtils authenticationUtils;
    private String originText;
    private String contentStr;
    private SynthesizerCallback callback;
    private String mUrl;
    private String voiceName = BakerBaseConstants.VOICE_NORMAL;
    private String language = BakerBaseConstants.LANGUAGE_ZH;
    private float speed = 5.0f;
    private int timeOut;
    private int volume = 5;
    private float pitch = 5.0f;
    private boolean k16OrK8 = BakerBaseConstants.K16;
    private int audioType = BakerBaseConstants.AUDIO_TYPE_PCM_16K;
    private int rate = BakerBaseConstants.RATE_16K;
    private boolean enableTimestamp = false;
    private boolean isFirst;
    private int perDuration = 260;

    private LinkedBlockingQueue<String> textQueue = new LinkedBlockingQueue<>();

    @Override
    public void setDebug(Context context, boolean debug) {
        BakerBaseConstants.setIsDebug(debug);
        WriteLog.openStream(context);
    }

    @Override
    public void bakerPlay() {
        BakerMediaPlayer.getInstance().play();
    }

    @Override
    public void bakerPause() {
        BakerMediaPlayer.getInstance().pause();
    }

    @Override
    public void bakerStop() {
        BakerMediaPlayer.getInstance().stop();
    }

    @Override
    public boolean isPlaying() {
        return BakerMediaPlayer.getInstance().isPlaying();
    }

    @Override
    public int getCurrentPosition() {
        return BakerMediaPlayer.getInstance().getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return BakerMediaPlayer.getInstance().getDuration();
    }

    private HsjWebSocketListener listener = new HsjWebSocketListener();

    private class HsjWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            WriteLog.writeLogs("webSocket onOpen???" + response.toString());
            if (isFirst && callback != null) {
                callback.onSynthesisStarted();
                if (callback instanceof BakerMediaCallback) {
                    BakerMediaPlayer.getInstance().init(k16OrK8);
                    BakerMediaPlayer.getInstance().clean();
                    BakerMediaPlayer.getInstance().setDuration(originText.length() * perDuration / 100);
                    BakerMediaPlayer.getInstance().setCallback((BakerMediaCallback) callback);
                }
            }
            send();
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            WriteLog.writeLogs("?????????????????????" + text);
            if (webSocketClient != null && webSocket.equals(webSocketClient.getWebSocket())) {
                if (!TextUtils.isEmpty(text)) {
                    try {
                        BaseResponse response = GsonConverter.fromJson(text, BaseResponse.class);
                        if (response.getCode() == 90000) {
                            if (callback != null) {
                                if (isFirst && response.getData().getIdx() == 1) {
//                                    ((BakerMediaCallback) callback).log(String.valueOf(System.currentTimeMillis() - time));
//                                    time = System.currentTimeMillis();
                                    isFirst = false;
                                    callback.onPrepared();
                                }
                                byte[] bytes = Base64.decode(response.getData().getAudio_data(), Base64.DEFAULT);
                                callback.onBinaryReceived(bytes, response.getData().getAudio_type(), response.getData().getInterval(),
                                        textQueue.size() < 1 && response.getData().getEnd_flag() == 1);
                            }

                            if (response.getData().getEnd_flag() == 1) {
                                contentStr = textQueue.poll();
                                if (TextUtils.isEmpty(contentStr)) {
                                    closeSocket();
                                    if (callback != null) {
                                        callback.onSynthesisCompleted();
                                    }
                                } else {
                                    webSocketClient.start(listener);
                                }
                            }
                        } else if (response.getCode() == 30000) {
                            webSocket.close(1001, null);
                            getTtsToken(2);
//                            handler.sendEmptyMessageDelayed(102, 2000);
                        } else {
                            onFault(SynthesizerErrorUtils.formatErrorBean(response.getCode(), response.getMessage(), response.getTrace_id()));
//                            HLogger.d("webSocket.id==" + webSocket.toString());
                            webSocket.close(1001, null);
                        }
                    } catch (Exception e) {
//                        CrashNetUtils.net(Log.getStackTraceString(e));
                        onFault(SynthesizerErrorUtils.formatErrorBean(BakerSynthesizerErrorConstants.ERROR_CODE_RESPONSE_NOT_AVAILABLE_ANALYSIS_FAULT, e.getMessage()));
//                        HLogger.d("webSocket.id==" + webSocket.toString());
                        webSocket.close(1001, null);
//                        WriteLog.writeLogs("onMessage==" + e.getMessage());
                    }
                } else {
                    onFault(SynthesizerErrorUtils.formatErrorBean(BakerSynthesizerErrorConstants.ERROR_CODE_RESPONSE_NOT_AVAILABLE_ISNULL_FAULT));
//                    HLogger.d("webSocket.id==" + webSocket.toString());
                    webSocket.close(1001, null);
                }
            }
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            if (webSocketClient != null && webSocketClient.getCancelSocket() != null && !webSocket.equals(webSocketClient.getCancelSocket())) {
                onFault(SynthesizerErrorUtils.formatErrorBean(BakerSynthesizerErrorConstants.ERROR_CODE_ERROR_INFO, t.getMessage()));
                webSocket.close(1001, null);
                try {
                    t.printStackTrace();
                } catch (Exception e) {
                    WriteLog.writeLogs("onFailure==" + e.getMessage());
                }
            }
        }
    }

    private void send() {
        if (TextUtils.isEmpty(contentStr)) {
            closeSocket();
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("access_token", SynthesizerConstants.ttsToken);
        params.put("version", "1.0");

        Map<String, String> ttsParams = new HashMap<>();
        ttsParams.put("text", contentStr);
        ttsParams.put("voice_name", voiceName);
        ttsParams.put("language", language);
        ttsParams.put("domain", "1");
        if (speed != 0)
            ttsParams.put("speed", String.valueOf(speed));
        if (volume != 0)
            ttsParams.put("volume", String.valueOf(volume));
        if (pitch != 0)
            ttsParams.put("pitch", String.valueOf(pitch));
        ttsParams.put("audiotype", String.valueOf(audioType));
        if (rate != 0)
            ttsParams.put("rate", String.valueOf(rate));
        if (enableTimestamp) {
            ttsParams.put("interval", "1");
        } else {
            ttsParams.put("interval", "0");
        }

        params.put("tts_params", ttsParams);

        String s = GsonConverter.toJson(params);
        HLogger.d(s);
        WriteLog.writeLogs("webSocket ??????????????????????????????" + s);
//        time = System.currentTimeMillis();
        if (webSocketClient != null) {
            webSocketClient.getWebSocket().send(s);
        }
    }

    public BakerSynthesizerImpl(Context context) {
    }

    public BakerSynthesizerImpl(Context context, String clientId, String clientSecret) {
        setClientId(clientId);
        setClientSecret(clientSecret);
        getTtsToken(0);

        webSocketClient = new WebSocketClient();
    }

    public BakerSynthesizerImpl(Context context, String clientId, String clientSecret, int connectTimeOut) {
        setClientId(clientId);
        setClientSecret(clientSecret);
        getTtsToken(0);

        timeOut = connectTimeOut;
        webSocketClient = new WebSocketClient(connectTimeOut);
    }

    public BakerSynthesizerImpl(Context context, String clientId, String clientSecret, String url) {
        setClientId(clientId);
        setClientSecret(clientSecret);
        getTtsToken(0);

        mUrl = url;
        webSocketClient = new WebSocketClient(url);
    }

    private int isInit;

    //0=?????????token  1=??????token????????????start??????  2=??????poll?????????????????????????????????
    private void getTtsToken(int is_init) {
        isInit = is_init;
        if (authenticationUtils == null) {
            authenticationUtils = new AuthenticationUtils(mAuthenticationUtilsListener);
        }
        authenticationUtils.authentication(true);
    }

    private CallbackListener mAuthenticationUtilsListener = new AuthenticationUtilsListener();

    private class AuthenticationUtilsListener implements CallbackListener<String> {

        @Override
        public void onSuccess(String response) {
            SynthesizerConstants.ttsToken = response;
            WriteLog.writeLogs("????????????ttsToken???" + SynthesizerConstants.ttsToken);
            HLogger.d("????????????ttsToken???" + SynthesizerConstants.ttsToken);
            if (isInit == 1) {
                HLogger.d("BakerSynthesizerImpl.this.start()");
                BakerSynthesizerImpl.this.start();
            } else if (isInit == 2) {
                HLogger.d("webSocketClient.start(listener)");
                webSocketClient.start(listener);
            } else if (isInit == 0) {
                HLogger.d("webSocketClient.start(listener)=0");
            }
        }

        @Override
        public void onFailure(Exception e) {
            onFault(SynthesizerErrorUtils.formatErrorBean(BakerSynthesizerErrorConstants.ERROR_CODE_INIT_FAILED_TOKEN_FAULT));
        }
    }

    @Override
    public void setTtsToken(String token) {
        if (!TextUtils.isEmpty(token)) {
            SynthesizerConstants.ttsToken = token;
        }
    }

    @Override
    public void start() {
        WriteLog.writeLogs("start() synthesizer start");
        if (TextUtils.isEmpty(SynthesizerConstants.ttsToken)) {
            if (TextUtils.isEmpty(SynthesizerConstants.mClientId)) {
                onFault(SynthesizerErrorUtils.formatErrorBean(BakerSynthesizerErrorConstants.ERROR_CODE_INIT_FAILED_CLIENT_ID_FAULT));
                return;
            }
            if (TextUtils.isEmpty(SynthesizerConstants.mClientSecret)) {
                onFault(SynthesizerErrorUtils.formatErrorBean(BakerSynthesizerErrorConstants.ERROR_CODE_INIT_FAILED_CLIENT_SECRET_FAULT));
                return;
            }
            getTtsToken(1);
            return;
        }
        if (webSocketClient == null) {
            if (timeOut > 0 && !TextUtils.isEmpty(mUrl)) {
                webSocketClient = new WebSocketClient(timeOut, mUrl);
            } else if (timeOut > 0) {
                webSocketClient = new WebSocketClient(timeOut);
            } else if (!TextUtils.isEmpty(mUrl)) {
                webSocketClient = new WebSocketClient(mUrl);
            } else {
                webSocketClient = new WebSocketClient();
            }
        }
        if (textQueue.size() < 1) {
            onFault(SynthesizerErrorUtils.formatErrorBean(BakerSynthesizerErrorConstants.ERROR_CODE_STRING_NULL));
            return;
        }
        isFirst = true;
        contentStr = textQueue.poll();

        webSocketClient.start(listener);
        WriteLog.writeLogs("start() synthesizer end");
    }

    private void closeSocket() {
        if (webSocketClient != null) {
            webSocketClient.getWebSocket().close(1001, null);
        }
    }

    @Override
    public void onDestroy() {
        webSocketClient.stop();
    }

    /**
     * ?????????????????????callback
     *
     * @param c
     */
    @Override
    public void setBakerCallback(SynthesizerCallback c) {
        callback = c;
    }

    /**
     * ???????????????????????????url?????????
     *
     * @param u
     */
    @Override
    public void setUrl(String u) {
        mUrl = u;
    }

    /**
     * ???????????????????????????????????????????????????_????????????_??????
     *
     * @param name
     */
    @Override
    public void setVoice(String name) {
        if (!TextUtils.isEmpty(name)) {
            try {
                voiceName = new String(name.getBytes(BakerBaseConstants.UTF_8), BakerBaseConstants.UTF_8);
            } catch (UnsupportedEncodingException e) {
                onFault(SynthesizerErrorUtils.formatErrorBean(BakerSynthesizerErrorConstants.ERROR_CODE_PARAMS_NOT_AVAILABLE_SET_VOICE_FAULT, e.getMessage()));
                WriteLog.writeLogs("setVoice==" + e.getMessage());
            }
        }
    }

    /**
     * ????????????????????????????????????,??????????????????utf-8???????????????
     *
     * @param text
     */
    @Override
    public void setText(String text) {
        try {
            if (!TextUtils.isEmpty(text)) {
                originText = text;
                if (textQueue.size() > 0) {
                    textQueue.clear();
                }
                List<String> resList = Util.splitText(text);
                for (String str : resList) {
                    String s = URLEncoder.encode(str, BakerBaseConstants.UTF_8);
                    textQueue.offer(Base64.encodeToString(s.getBytes(), Base64.NO_WRAP));
                }
            }
        } catch (Exception e) {
            onFault(SynthesizerErrorUtils.formatErrorBean(BakerSynthesizerErrorConstants.ERROR_CODE_PARAMS_NOT_AVAILABLE_TXT_TRANSCODING_FAULT, e.getMessage()));
            WriteLog.writeLogs("setText==" + e.getMessage());
        }
    }

    /**
     * ??????????????????????????????????????????ZH(??????????????????)???ENG(????????????????????????????????????),?????????ZH
     *
     * @param l
     */
    @Override
    public void setLanguage(String l) {
        if (!TextUtils.isEmpty(l)) {
            language = l;
        }
    }

    /**
     * ???????????????????????????0???9????????????????????????????????????????????????5
     *
     * @param s
     */
    @Override
    public void setSpeed(float s) {
        if (s < 0 || s > 9) {
            return;
        }
        speed = s;
    }

    /**
     * ???????????????????????????0???9??????????????????????????????????????????????????????5
     *
     * @param v
     */
    @Override
    public void setVolume(int v) {
        if (v < 0 || v > 9) {
            return;
        }
        volume = v;
    }

    /**
     * ??????????????????????????????0-9?????????????????????5?????????
     *
     * @param p
     */
    @Override
    public void setPitch(float p) {
        if (p < 0 || p > 9) {
            return;
        }
        pitch = p;
    }

    /**
     * ??????????????????????????????3?????????mp3??????
     * audiotype=4 ?????????16K????????????pcm??????
     * audiotype=5 ?????????8K????????????pcm??????
     * audiotype=6 ?????????16K????????????wav??????
     * audiotype=6&rate=1 ?????????8K???wav??????
     *
     * @param type
     */
    @Override
    public void setAudioType(int type) {
        audioType = type;
        if (type == BakerBaseConstants.AUDIO_TYPE_PCM_16K || type == BakerBaseConstants.AUDIO_TYPE_WAV_16K) {
            k16OrK8 = true;
        } else if (type == BakerBaseConstants.AUDIO_TYPE_PCM_8K) {
            k16OrK8 = false;
        }
    }

    /**
     * ??????????????????????????????2???????????????1-8???2???????????????????????????MP3??????????????????????????????
     * 1 ?????? 8kbps
     * 2 ?????? 16kbps
     * 3 ?????? 24kbps
     * 4 ?????? 32kbps
     * 5 ?????? 40kbps
     * 6 ?????? 48kbps
     * 7 ?????? 56kbps
     * 8 ?????? 64kbps
     *
     * @param r
     */
    @Override
    public void setRate(int r) {
        rate = r;
    }

    /**
     * ????????????????????????????????????true=???????????????false=????????????????????????????????????false????????????
     *
     * @param enable
     */
    @Override
    public void setEnableTimestamp(boolean enable) {
        enableTimestamp = enable;
    }

    @Override
    public void setClientId(String clientId) {
        SynthesizerConstants.mClientId = clientId;
    }

    @Override
    public void setClientSecret(String clientSecret) {
        SynthesizerConstants.mClientSecret = clientSecret;
    }

    /**
     * ??????????????????????????????
     *
     * @param duration
     */
    @Override
    public void setPerDuration(int duration) {
        if (duration > 0) {
            this.perDuration = duration;
        }
    }

    private void onFault(BakerError errorBean) {
        String errorMes = String.format("???????????????errorCode= %s, errorMsg= %s, traceId= %s.", errorBean.getCode(), errorBean.getMessage(), errorBean.getTrace_id());
        Log.e("BakerSynthesizer", errorMes);
        WriteLog.writeLogs(errorMes);
        if (callback != null) {
            callback.onTaskFailed(errorBean);
        }
    }
}
