package com.baker.engrave.lib.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.baker.engrave.lib.BakerVoiceEngraver;
import com.baker.engrave.lib.BuildConfig;
import com.baker.engrave.lib.VoiceEngraveConstants;
import com.baker.engrave.lib.bean.RecordResult;
import com.baker.engrave.lib.bean.RecordingCheckDto;
import com.baker.engrave.lib.bean.RecordingSocketBean;
import com.baker.engrave.lib.callback.BaseNetCallback;
import com.baker.engrave.lib.net.NetConstants;
import com.baker.engrave.lib.net.NetUtil;
import com.baker.engrave.lib.net.WebSocketClient;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;

/**
 * Create by hsj55
 * 2020/3/9
 */
public class RecordUtil {
    private static Context mContext;
    private static AudioManager mAudioManager;
    private static AudioFocusRequest mFocusRequest;
    private static TelephonyManager mTelephonyManager;
    private static Recorder mRecorder;

    private static File mAudioFile = new File(Environment.getExternalStorageDirectory()
            + File.separator + "RecordingCollection" + File.separator + "audio.pcm");
    private static BaseNetCallback netCallback;
    private static String mSessionId, mContentText;
    private static boolean recording = false;
    private static final List<RecordingSocketBean.ParamBean> rerecords = new ArrayList<>();//重录的时候告诉后台
    private static WebSocket mWebSocket;
    private static boolean isFirst = true; //是否是ws第一次发送

    public static void setNetCallback(Context context, BaseNetCallback netCallback) {
        mContext = context;
        RecordUtil.netCallback = netCallback;
    }

    private static Thread mThread = null;//子线程去发送socket


    /**
     * 开始录音
     */
    public static void startRecord(String mouldId, String contentText) {
        String pathname = Environment.getExternalStorageDirectory()
                + File.separator + "RecordingCollection" + File.separator + "audio" + BakerVoiceEngraver.getCurrentIndex() + ".pcm";
        mAudioFile = new File(pathname);
        RecordResult recordResult = BakerVoiceEngraver.getRecordList().get(BakerVoiceEngraver.getCurrentIndex());
        recordResult.setFilePath(pathname);
        recording = true;
        HLogger.e("---3-startRecord");
        mSessionId = mouldId;
        mContentText = contentText;
        isFirst = true;
        linkedBlockingDeque.clear();
        mRecorder = OmRecorder.pcm(new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
            @Override
            public void onAudioChunkPulled(AudioChunk audioChunk) {
                HLogger.v("正在录音...");
                byte[] bytes = audioChunk.toBytes();
                if (netCallback != null) {
                    netCallback.recordVolume((int) audioChunk.maxAmplitude());
                }
                try {
                    if (isFirst) {
                        isFirst = false;
                        linkedBlockingDeque.put(new PcmBean(0, bytes));
                    } else {
                        linkedBlockingDeque.put(new PcmBean(1, bytes));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    HLogger.e("linkedBlockingDeque.put ERROR!!" + e.getMessage());
                }
            }
        }), file());

        getAudioFocus();

        //打开websocket链接
        RecordingSocketBean.ParamBean paramBean = new RecordingSocketBean.ParamBean(mSessionId, mContentText);
        WebSocketClient.getInstance().newClient(paramBean, new RecordingSocketBean.AudioBean(0, 0, ""));
        mWebSocket = WebSocketClient.getInstance().newWebSocket(new RecordingWebSocketListener());

        mRecorder.startRecording();
        if (netCallback != null) {
            HLogger.e("---4");
            //1=录音中， 2=识别中， 3=最终结果：通过， 4=最终结果：不通过
            netCallback.recordsResult(1, 0);
        }
    }

    private static void endRecord(int typeCode) {
        try {
            mRecorder.stopRecording();
            linkedBlockingDeque.put(new PcmBean(2, new byte[1]));
            netCallback.recordsResult(typeCode, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制并开始识别
     */
    public static void endRecordAndStartRecognize() {
        try {
            mRecorder.stopRecording();
            linkedBlockingDeque.put(new PcmBean(2, new byte[1]));
            //发送最后一帧，开始识别
            netCallback.recordsResult(2, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 临时存储使用PCM数据使用
     */
    static class PcmBean {
        public PcmBean(int state, byte[] pcm) {
            this.state = state;
            this.pcm = pcm;
        }

        int state;
        byte[] pcm;
    }

    private static RecordingSocketBean.AudioBean audioBean;
    private static final LinkedBlockingDeque<PcmBean> linkedBlockingDeque = new LinkedBlockingDeque<>();//队列存放音频buffer，子线程取buffer去发送socket;退出activity时候发送state=-1来结束线程

    private static class RecordingWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(@NotNull WebSocket webSocket, Response response) {
            //通道打开，首次发送数据
            HLogger.i("首次接收数据:" + response.toString());
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            HLogger.i("onMessage 服务器返回值" + text);
            //服务器返回值
            RecordingCheckDto recordingCheckDto = new Gson().fromJson(text, RecordingCheckDto.class);
            HLogger.i("onMessage 服务器返回值xx" + recordingCheckDto.toString());
            if ("20000".equals(recordingCheckDto.getCode())) {
                RecordingSocketBean.AudioBean data = recordingCheckDto.getData();
                if (data.getType() == 0) {
                    //开始录音
                    HLogger.i("onMessage 开始上传线程");
                    audioBean = data;
                    newThread();
                    if (mThread != null)
                        mThread.start();
                } else if (data.getType() == 1) {
                    //上传
                    endRecordAndStartRecognize();
                    HLogger.i("onMessage 识别结果");
                    if (netCallback != null) {
                        if (data.getPassStatus() == 1) {
                            //1=录音中， 2=识别中， 3=最终结果：通过， 4=最终结果：不通过
                            netCallback.recordsResult(3, (int) data.getPercent());
                            BakerVoiceEngraver.getRecordList().get(BakerVoiceEngraver.getCurrentIndex()).setPass(true);
                        } else {
                            netCallback.recordsResult(4, (int) data.getPercent());
                            BakerVoiceEngraver.getRecordList().get(BakerVoiceEngraver.getCurrentIndex()).setPass(BuildConfig.DEBUG);
                        }
                        webSocket.close(1000, "正常关闭");
                    }
                }
            } else if (NetConstants.RESULT_CODE_TOKEN_EXPIRE.equals(recordingCheckDto.getCode() + "")) {
                HLogger.i("onMessage token失效");
                webSocket.close(1000, "Token expired");
                try {
                    NetUtil.requestToken();
                    //打开websocket链接
                    RecordingSocketBean.ParamBean paramBean = new RecordingSocketBean.ParamBean(mSessionId, mContentText);
                    WebSocketClient.getInstance().newClient(paramBean, new RecordingSocketBean.AudioBean(0, 0, ""));
                    mWebSocket = WebSocketClient.getInstance().newWebSocket(new RecordingWebSocketListener());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                HLogger.i("onMessage 服务器异常");
                webSocket.close(1000, "Token expired");
            }
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            endRecord(2);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, Throwable t, Response response) {
            //返回错误信息
            t.printStackTrace();
            HLogger.e("返回错误信息 " + t);
            endRecord(4);
        }
    }


    private static void newThread() {
        mThread = new Thread() {
            @Override
            public void run() {
                super.run();
                //不停止录音，并且队列有数据就持续发送
                while (true) {
                    HLogger.d("-----上传音频的子线程-----");
                    try {
                        PcmBean pcmBean = linkedBlockingDeque.take();
                        if (pcmBean.state == 2) {
                            //最后一帧发送完成跳出循环
                            sendPcm(pcmBean.pcm, pcmBean.state);
                            return;
                        }
                        sendPcm(pcmBean.pcm, pcmBean.state);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        };
    }

    private static int countUploadAudioIndex = 0;//上传音频buffer的计数

    private static void sendPcm(byte[] pcm, int state) {
        HLogger.i("发送中--1--" + pcm.length + "state" + state + "\n" + Base64.encodeToString(pcm, Base64.NO_WRAP));
        String data = "";
        audioBean.setStatus(state);
        audioBean.setInfo(Base64.encodeToString(pcm, Base64.NO_WRAP));
        audioBean.setSequence(countUploadAudioIndex);
        data = new Gson().toJson((WebSocketUtil.formatParameters(new RecordingSocketBean.ParamBean(mSessionId, mContentText), audioBean)));
        HLogger.d("wsReq: " + data);
        countUploadAudioIndex++;
        mWebSocket.send((data));
    }

    public static void stopRecord() {
        recording = false;
        if (mRecorder == null) return;
        try {
            mRecorder.stopRecording();
            reset();
        } catch (Exception e) {
            e.printStackTrace();
            HLogger.e(e.getMessage());
        }
    }

    /**
     * @deprecated - 修改为 webSocket 上传录音，不在全部录音完成时上传，开始录音的时候就开始往后台传递 / 2020年6月28日
     * 停止录制并上传录音
     */
    public static void stopRecording(String sessionId, String contentText) {
        recording = false;
        if (mRecorder == null) return;
        try {
            mRecorder.stopRecording();
            reset();
            if (!mSessionId.equals(sessionId) && netCallback != null) {
                netCallback.netRecordError(VoiceEngraveConstants.ERROR_CODE_MOULD_DIFFERENT, "the mouldId is different.");
                return;
            }
            if (!mContentText.equals(contentText) && netCallback != null) {
                netCallback.netRecordError(VoiceEngraveConstants.ERROR_CODE_TEXT_DIFFERENT, "the contentText is different.");
                return;
            }
            //上传
            if (netCallback != null) {
                //1=录音中， 2=识别中， 3=最终结果：通过， 4=最终结果：不通过
                netCallback.recordsResult(2, 0);
            }
//            NetUtil.uploadRecords(sessionId, contentText, mAudioFile);
        } catch (Exception e) {
            if (netCallback != null) {
                netCallback.netRecordError(VoiceEngraveConstants.ERROR_CODE_STOP_RECORD, "stop record error: " + e.getMessage());
            }
        }
    }

    /**
     * @deprecated
     */
    public static void reUploadRecord(String sessionId, String contentText) {
        if (!mSessionId.equals(sessionId) && netCallback != null) {
            netCallback.netRecordError(VoiceEngraveConstants.ERROR_CODE_MOULD_DIFFERENT, "the mouldId is different.");
            return;
        }
        if (!mContentText.equals(contentText) && netCallback != null) {
            netCallback.netRecordError(VoiceEngraveConstants.ERROR_CODE_TEXT_DIFFERENT, "the contentText is different.");
            return;
        }
        //上传
        if (netCallback != null) {
            //1=录音中， 2=识别中， 3=最终结果：通过， 4=最终结果：不通过
            netCallback.recordsResult(2, 0);
        }
//        NetUtil.uploadRecords(sessionId, contentText, mAudioFile);
    }

    private static PullableSource mic() {
        //16，单声道，16000
        return new PullableSource.Default(
                new AudioRecordConfig.Default(MediaRecorder.AudioSource.MIC,
                        AudioFormat.ENCODING_PCM_16BIT, AudioFormat.CHANNEL_IN_MONO, 16000));
    }

    @NonNull
    private static File file() {
        if (mAudioFile.exists()) {
            mAudioFile.delete();
        }
        try {
            if (!mAudioFile.getParentFile().exists()) {
                mAudioFile.getParentFile().mkdirs();
            }
            if (!mAudioFile.exists()) {
                mAudioFile.createNewFile();
            }
        } catch (IOException e) {
            netCallback.netRecordError(VoiceEngraveConstants.ERROR_CODE_FILE, "create record file error: " + e.getMessage());
        }
        return mAudioFile;
    }

    private static void getAudioFocus() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }
        //获取音频焦点
        try {
            //8.0版本以后
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();
                mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                        .setAudioAttributes(mPlaybackAttributes)
                        .setAcceptsDelayedFocusGain(true)
                        .setOnAudioFocusChangeListener(audioFocusChangeListener)
                        .setWillPauseWhenDucked(true)
                        .build();
                mAudioManager.requestAudioFocus(mFocusRequest);

            } else {
                //流媒体设置焦点参数
                mAudioManager.requestAudioFocus(audioFocusChangeListener,
                        // Use the music stream.(默认音乐流)
                        AudioManager.USE_DEFAULT_STREAM_TYPE,
                        // Request permanent focus.(要求永久焦点)
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        }
        //手动注册对PhoneStateListener中的listen_call_state状态进行监听
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private static final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //电话通话的状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    HLogger.e("电话进来了");
                    //电话响铃的状态
                    if (netCallback != null && recording) {
                        netCallback.netRecordError(VoiceEngraveConstants.ERROR_CODE_INTERRUPT, "Recording interruption due to abnormality");
                    }
                    stopRecord();
                    break;
            }
            super.onCallStateChanged(state, phoneNumber);
        }
    };

    private static final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            if (i == AudioManager.AUDIOFOCUS_LOSS) {
                HLogger.e("失去了焦点");
                if (netCallback != null && recording) {
                    netCallback.netRecordError(VoiceEngraveConstants.ERROR_CODE_INTERRUPT, "Recording interruption due to abnormality");
                }
                stopRecord();
            }
        }
    };

    private static void reset() {
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }
}
