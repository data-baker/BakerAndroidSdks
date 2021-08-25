package com.databaker.voiceconvert;

import android.Manifest;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
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
import com.databaker.voiceconvert.callback.SpeechCallback;
import com.databaker.voiceconvert.callback.VoiceConvertCallBack;
import com.databaker.voiceconvert.callback.WebSocketOpenCallback;
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
                authCallback.onError(new IllegalArgumentException("缺少ClientId"));
            }
            return;
        }
        if (TextUtils.isEmpty(clientSecret)) {
            if (authCallback != null) {
                authCallback.onError(new IllegalArgumentException("缺少Secret"));
            }
            return;
        }

        try {
            if (!Util.checkConnectNetwork(mContext)) {
                if (authCallback != null) {
                    authCallback.onError(new RuntimeException("网络连接不可用"));
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

        //调用授权
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
     * 使用sdk内部唤起的录音，进行声音转换
     */
    public void startFromMic(VoiceConvertCallBack callBack) {
        if (Build.VERSION.SDK_INT > 23) {
            int resultCode = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO);
            if (resultCode != PERMISSION_GRANTED) {
                throw new RuntimeException("没有申请录音的权限，请申请 Manifest.permission.RECORD_AUDIO 权限");
            }
        }

        if (token.isEmpty()) {
            throw new RuntimeException("请先调用 init 方法初始化");
        }
        mCallBack = callBack;
        needRecorder = true;
        startWebSocket();
    }

    /**
     * 声音文件或者app内录音数据转换
     */
    public void startFromBytes(VoiceConvertCallBack callBack) {
        if (token.isEmpty()) {
            throw new RuntimeException("请先调用 init 方法初始化");
        }
        mCallBack = callBack;
        needRecorder = false;
        startWebSocket();
    }

    public void sendAudio(byte[] byteArray, boolean isLast) {
        if (token.isEmpty()) {
            throw new RuntimeException("请先调用 init 方法初始化");
        }
        if (webSocketClient == null || webSocketClient.getWebSocket() == null) {
            throw new IllegalStateException("webSocket is null,请先调用 startFromBytes 方法建立连接");
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
                        if (mCallBack != null) {
                            mCallBack.onError(audioResp.getErrcode() + "", audioResp.getErrmsg(), audioResp.getTraceid());
                        }
                        stopConvert();
                    }
                } else {
                    if (mCallBack != null) {
                        mCallBack.onError(ERROR_WEB_SOCKET, "解析JSON长度出错", "");
                    }
                    stopConvert();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mCallBack != null) {
                    mCallBack.onError(ERROR_WEB_SOCKET, "非法异常" + e.getMessage(), "");
                }
                stopConvert();
            }
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            if (webSocketClient != null && webSocketClient.getCancelSocket() != null
                    && !webSocket.equals(webSocketClient.getCancelSocket())) {
                if (mCallBack != null) {
                    mCallBack.onError(ERROR_WEB_SOCKET, t.getMessage(), "");
                }
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
            // new一个byte数组用来存一些字节数据，大小为缓冲区大小
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
     * 开始录音
     */
    public void startRecord() {
        if (audioRecord == null) {
            // 获得缓冲区字节大小
            bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                    AUDIO_CHANNEL, AUDIO_ENCODING);
            audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
        }
        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING
                || status == Status.STATUS_START) {
            audioRecord.stop();
            //将录音状态设置成正在录音状态
            status = Status.STATUS_STOP;
        }
        audioRecord.startRecording();
        if (mCallBack != null) {
            mCallBack.canSpeech();
        }
        //将录音状态设置成正在录音状态
        status = Status.STATUS_START;
        mSingleExecutorServiceForOrderRequest.submit(runnable);
    }

    public void stopRecord() {
        if (audioRecord != null) {
            audioRecord.stop();
        }
        //将录音状态设置成正在录音状态
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

    //************************************************ 以下是mic配置相关信息 *************************************
    private static final LinkedBlockingQueue<Runnable> AudioRecordQueue = new LinkedBlockingQueue<>(10);
    private static final ExecutorService mSingleExecutorServiceForOrderRequest =
            new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, AudioRecordQueue);
    //音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;

    //录音对象
    private AudioRecord audioRecord;

    //录音状态
    private Status status = Status.STATUS_NO_READY;

    /**
     * 录音对象的状态
     */
    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //停止
        STATUS_STOP
    }
    //************************************************ mic配置相关信息 END *************************************

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
