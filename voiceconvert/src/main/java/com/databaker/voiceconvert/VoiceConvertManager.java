package com.databaker.voiceconvert;

import android.Manifest;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.baker.sdk.basecomponent.util.Util;
import com.baker.sdk.http.BakerTokenManager;
import com.baker.sdk.http.CallbackListener;
import com.baker.sdk.http.WebSocketClient;
import com.databaker.voiceconvert.bean.AudioReq;
import com.databaker.voiceconvert.bean.AudioResp;
import com.databaker.voiceconvert.callback.AuthCallback;
import com.databaker.voiceconvert.callback.VoiceConvertCallBack;
import com.databaker.voiceconvert.util.ArrayUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.databaker.voiceconvert.Constants.ERROR_NO_PERMISSION;
import static com.databaker.voiceconvert.Constants.ERROR_NO_TOKEN;
import static com.databaker.voiceconvert.Constants.ERROR_WEB_SOCKET;

public class VoiceConvertManager {
    private Context mContext = null;
    private BakerTokenManager tokenManager;
    private String token;
    private String mVoiceName = "Vc_jiaojiao";
    private boolean enableVad = false;
    private boolean enableAlign = false;
    private VoiceConvertCallBack mCallBack;
    private WebSocketClient webSocketClient;
    private boolean needRecorder = true;

    public void init(@NotNull Context context, @NotNull String clientId, @NotNull String clientSecret, AuthCallback authCallback) {
        mContext = context;

        if (TextUtils.isEmpty(clientId)) {
            if (authCallback != null) {
                authCallback.onError(new IllegalArgumentException("??????ClientId"));
            }
            return;
        }
        if (TextUtils.isEmpty(clientSecret)) {
            if (authCallback != null) {
                authCallback.onError(new IllegalArgumentException("??????Secret"));
            }
            return;
        }

        try {
            if (!Util.checkConnectNetwork(mContext)) {
                if (authCallback != null) {
                    authCallback.onError(new RuntimeException("?????????????????????"));
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (authCallback != null) {
                authCallback.onError(e);
                return;
            }
        }

        //????????????
        tokenManager = new BakerTokenManager();
        tokenManager.authentication(clientId, clientSecret, new CallbackListener<String>() {
            @Override
            public void onSuccess(String response) {
                token = response;
                if (authCallback != null) {
                    authCallback.onSuccess();
                    return;
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("LongTimeAsrImpl", "baker long time asr sdk init token error, " + e.getMessage());
                e.printStackTrace();
                if (authCallback != null) {
                    authCallback.onError(new RuntimeException("baker long time asr sdk init token error, " + e.getMessage()));
                    return;
                }
            }
        });
    }

    /**
     * ??????sdk??????????????????????????????????????????
     */
    public void startFromMic(VoiceConvertCallBack callBack) {
        mCallBack = callBack;
        if (Build.VERSION.SDK_INT > 23) {
            int resultCode = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO);
            if (resultCode != PERMISSION_GRANTED) {
                onError(ERROR_NO_PERMISSION, "??????????????????????????????????????? Manifest.permission.RECORD_AUDIO ??????", "");
                return;
            }
        }

        if (token.isEmpty()) {
            onError(ERROR_NO_TOKEN, "???????????? init ???????????????", "");
            return;
        }
        needRecorder = true;
        startWebSocket();
    }

    /**
     * ??????????????????app?????????????????????
     */
    public void startFromBytes(VoiceConvertCallBack callBack) {
        mCallBack = callBack;
        if (token.isEmpty()) {
            onError(ERROR_NO_TOKEN, "???????????? init ???????????????", "");
            return;
        }
        needRecorder = false;
        startWebSocket();
    }

    public void sendAudio(byte[] byteArray, boolean isLast) {
        if (token.isEmpty()) {
            onError(ERROR_NO_TOKEN, "???????????? init ???????????????", "");
            return;
        }
        if (webSocketClient == null || webSocketClient.getWebSocket() == null) {
            onError(ERROR_WEB_SOCKET, "webSocket is null,???????????? startFromBytes ??????????????????", "");
            return;
        }
        packageAudioSend(byteArray, isLast);
    }

    private void startWebSocket() {
        if (webSocketClient == null) {
            webSocketClient = new WebSocketClient("wss://openapi.data-baker.com/ws/voice_conversion");
        }
        webSocketClient.start(webSocketListener);
    }

    private WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            if (mCallBack != null) {
                mCallBack.onReady();
            }
            if (needRecorder) {
                startRecord();
            }
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            try {
                byte[] resultByteArray = bytes.toByteArray();
                byte[] prefixArray = Arrays.copyOfRange(resultByteArray, 0, 4);
                int length = (prefixArray[0] << 24) + (prefixArray[1] << 16) + (prefixArray[2] << 8) + prefixArray[3];
                if (length > 0) {
                    byte[] jsonArray = Arrays.copyOfRange(resultByteArray, 4, 4 + length);
                    byte[] audioArray = Arrays.copyOfRange(resultByteArray, 4 + length, resultByteArray.length);
                    String jsonStr = new String(jsonArray);
                    AudioResp audioResp = new Gson().fromJson(jsonStr, AudioResp.class);
                    if (audioResp.getErrcode() == 0 && mCallBack != null) {
                        if (audioResp.isLastpkg()) {
                            mCallBack.onAudioOutput(audioArray, true, audioResp.getTraceid());
                            stopConvert();
                        } else {
                            mCallBack.onAudioOutput(audioArray, false, audioResp.getTraceid());
                        }
                    } else {
                        onError(audioResp.getErrcode() + "", audioResp.getErrmsg(), audioResp.getTraceid());
                        stopConvert();
                    }
                } else {
                    onError(ERROR_WEB_SOCKET, "??????JSON????????????", "");
                    stopConvert();
                }
            } catch (Exception e) {
                e.printStackTrace();
                onError(ERROR_WEB_SOCKET, "????????????" + e.getMessage(), "");
                stopConvert();
            }
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            if (webSocketClient != null && webSocketClient.getCancelSocket() != null
                    && !webSocket.equals(webSocketClient.getCancelSocket())) {
                onError(ERROR_WEB_SOCKET, t.getMessage(), "");
            }
        }
    };

    private void packageAudioSend(byte[] audioArray, boolean isLast) {
        AudioReq req = new AudioReq(token, mVoiceName, enableVad, enableAlign, isLast);
        String json = new Gson().toJson(req);
        byte[] jsonArray = ArrayUtils.toByteArray(json);
        byte[] arrayPrefix = new byte[4];
        arrayPrefix[0] = (byte) (jsonArray.length >> 24 & 0xFF);
        arrayPrefix[1] = (byte) (jsonArray.length >> 16 & 0xFF);
        arrayPrefix[2] = (byte) (jsonArray.length >> 8 & 0xFF);
        arrayPrefix[3] = (byte) (jsonArray.length & 0xFF);
        byte[] resultBA = ArrayUtils.plus(ArrayUtils.plus(arrayPrefix, jsonArray), audioArray);
        if (webSocketClient != null) {
            if (webSocketClient.getWebSocket() != null) {
                webSocketClient.getWebSocket().send(new ByteString(resultBA));
            }
        }
    }

    public void setVoiceName(String voiceName) {
        mVoiceName = voiceName;
    }

    public void setVadEnable(boolean bool) {
        enableVad = bool;
    }

    public void setAudioAlign(boolean bool) {
        enableAlign = bool;
    }

    public void stopConvert() {
        if (webSocketClient != null) {
            if (webSocketClient.getWebSocket() != null) {
                webSocketClient.getWebSocket().close(1001, null);
            }
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int readsize = 0;
            // new??????byte????????????????????????????????????????????????????????????
            byte[] audiodata;
            while (status == Status.STATUS_START) {
                audiodata = new byte[Constants.bufferSizeForUpload];
                readsize = audioRecord.read(audiodata, 0, Constants.bufferSizeForUpload);
                if (readsize == Constants.bufferSizeForUpload) {
                    Log.e("VoiceConvertManager", "readsize = " + readsize);
                    packageAudioSend(audiodata, false);
                    calculateVolume(audiodata);
                } else {
                    Log.e("VoiceConvertManager", "readsize < 0, " + readsize);
                    status = Status.STATUS_STOP;
                    if (readsize > 0) {
                        packageAudioSend(audiodata, true);
                        calculateVolume(audiodata);
                    } else {
                        packageAudioSend(new byte[]{}, true);
                    }
                }
            }
        }
    };

    /**
     * ????????????
     */
    public void startRecord() {
        if (audioRecord == null) {
            // ???????????????????????????
            bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                    AUDIO_CHANNEL, AUDIO_ENCODING);
            audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
        }
        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING
                || status == Status.STATUS_START) {
            audioRecord.stop();
            //??????????????????????????????????????????
            status = Status.STATUS_STOP;
        }
        audioRecord.startRecording();
        if (mCallBack != null) {
            mCallBack.canSpeech();
        }
        //??????????????????????????????????????????
        status = Status.STATUS_START;
        mSingleExecutorServiceForOrderRequest.submit(runnable);
    }

    public void stopRecord() {
        if (audioRecord != null) {
            audioRecord.stop();
        }
        //??????????????????????????????????????????
        status = Status.STATUS_STOP;
    }

    private long time;

    private void calculateVolume(byte[] audioData) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - time > 100) {
            short[] shorts = toShorts(audioData);
            int nMaxAmp = 0;
            int arrLen = shorts.length;
            int peakIndex;
            for (peakIndex = 0; peakIndex < arrLen; peakIndex++) {
                if (shorts[peakIndex] >= nMaxAmp) {
                    nMaxAmp = shorts[peakIndex];
                }
            }
//            int volume = (int) (20 * Math.log10(nMaxAmp / 0.6));
            int volume = (int) (20 * Math.log10(nMaxAmp));

            if (mCallBack != null) {
                mCallBack.onOriginData(audioData, volume);
            }
            time = currentTime;
        }
    }

    private short[] toShorts(byte[] audioData) {
        short[] shorts = new short[audioData.length / 2];
        ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        return shorts;
    }

    private void onError(String errorCode, String errorMsg, String traceId) {
        if (mCallBack != null) {
            mCallBack.onError(errorCode, errorMsg, traceId);
        }
    }

    //************************************************ ?????????mic?????????????????? *************************************
    private static final LinkedBlockingQueue<Runnable> AudioRecordQueue = new LinkedBlockingQueue<>(10);
    private static final ExecutorService mSingleExecutorServiceForOrderRequest =
            new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, AudioRecordQueue);
    //????????????-?????????
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //????????????
    //44100???????????????????????????????????????????????????22050???16000???11025
    //???????????????????????????22.05KHz???44.1KHz???48KHz????????????
    private static int AUDIO_SAMPLE_RATE = 16000;
    //?????? ?????????
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //??????
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // ?????????????????????
    private int bufferSizeInBytes = 0;

    //????????????
    private AudioRecord audioRecord;

    //????????????
    private Status status = Status.STATUS_NO_READY;

    /**
     * ?????????????????????
     */
    public enum Status {
        //?????????
        STATUS_NO_READY,
        //??????
        STATUS_READY,
        //??????
        STATUS_START,
        //??????
        STATUS_STOP
    }
    //************************************************ mic?????????????????? END *************************************

    public void release() {
        stopConvert();

        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }

        if (mCallBack != null) {
            mCallBack = null;
        }
    }
}
