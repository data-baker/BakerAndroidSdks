package com.baker.engrave.lib;

import android.Manifest;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.baker.engrave.lib.bean.RecordResult;
import com.baker.engrave.lib.callback.BaseNetCallback;
import com.baker.engrave.lib.callback.ContentTextCallback;
import com.baker.engrave.lib.callback.DetectCallback;
import com.baker.engrave.lib.callback.InitListener;
import com.baker.engrave.lib.callback.MouldCallback;
import com.baker.engrave.lib.callback.PlayListener;
import com.baker.engrave.lib.callback.RecordCallback;
import com.baker.engrave.lib.callback.UploadRecordsCallback;
import com.baker.engrave.lib.callback.innner.DetectCallbackImpl;
import com.baker.engrave.lib.callback.innner.NetCallbackImpl;
import com.baker.engrave.lib.callback.innner.RecordCallbackImpl;
import com.baker.engrave.lib.configuration.EngraverType;
import com.baker.engrave.lib.net.NetUtil;
import com.baker.engrave.lib.util.BaseUtil;
import com.baker.engrave.lib.util.DetectUtil;
import com.baker.engrave.lib.util.LogUtil;
import com.baker.engrave.lib.util.RecordUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;

/**
 * 接口需要相关数据存储到本地
 */
public class BakerVoiceEngraver implements BaseNetCallback {


    /*================================================== 单例实现 Start ==================================================*/
    private BakerVoiceEngraver() {
    }

    private static final class HolderClass {
        private static final BakerVoiceEngraver instance = new BakerVoiceEngraver();
    }

    public static BakerVoiceEngraver getInstance() {
        return HolderClass.instance;
    }
    /*================================================== 单例实现 End  ==================================================*/


    /*================================================== 获取所需对象 Start ==================================================*/
    private ExecutorService workService;
    private Handler mHandler;

    private void runOnWorkerThread(Runnable runnable) {
        if (workService == null) {
            workService = Executors.newSingleThreadExecutor();
        }
        workService.execute(runnable);
    }

    private void runOnUiThread(Runnable runnable) {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.post(runnable);
    }

    private NetCallbackImpl netCallback;

    private NetCallbackImpl getNetCallBack() {
        if (netCallback == null) {
            netCallback = new NetCallbackImpl();
        }
        return netCallback;
    }

    private RecordCallbackImpl recordUtilCallback;

    private RecordCallbackImpl getRecordCallBack() {
        if (recordUtilCallback == null) {
            recordUtilCallback = new RecordCallbackImpl();
        }
        return recordUtilCallback;
    }

    private DetectCallbackImpl detectUtilCallBack;

    private DetectCallbackImpl getDetectCallBack() {
        if (detectUtilCallBack == null) {
            detectUtilCallBack = new DetectCallbackImpl();
        }
        return detectUtilCallBack;
    }
    /*================================================== 获取所需对象 End ==================================================*/


    //当前录音index
    private int currentIndex = 0;
    private Context mContext;
    private String mClientId;
    private String mClientSecret;
    private String mQueryID;
    private EngraverType type = EngraverType.Common;

    private final int SAMPLE_RATE = 16000;
    private boolean isPlaying = false;

    public List<RecordResult> getRecordList() {
        return getNetCallBack().getRecordList();
    }

    public EngraverType getType() {
        return type;
    }

    public void setType(EngraverType type) {
        this.type = type;
    }


    /**
     * 获取 当前录制条目的下标
     *
     * @return index
     */
    @Override
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * 获取 ClientId
     *
     * @return ClientId
     */
    @Override
    public String getClientId() {
        return mClientId;
    }

    /**
     * 获取ClientSecret
     *
     * @return ClientSecret
     */
    @Override
    public String getClientSecret() {
        return mClientSecret;
    }

    /**
     * 查询当前下标条目是否录制
     *
     * @param index 下标
     * @return 是否录制
     */
    @Override
    public boolean isRecord(int index) {
        return getRecordList().get(index).isPass();
    }


    /**
     * 停止试听播放
     */
    @Override
    public void stopPlay() {
        isPlaying = false;
    }

    /**
     * 播放已录制条目试听
     *
     * @param currentIndex 需要播放的条目的下标
     * @param listener     播放状态监听
     */
    @Override
    public void startPlay(final int currentIndex, final PlayListener listener) {
        runOnWorkerThread(() -> {
            try {
                LogUtil.d("startPlay");
                RecordResult recordResult = getRecordList().get(currentIndex);
                String filePath = recordResult.getFilePath();
                if (!TextUtils.isEmpty(filePath)){
                    int iMinBufSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
                    AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, iMinBufSize, AudioTrack.MODE_STREAM);
                    audioTrack.play();
                    Source source = Okio.source(new File(filePath));
                    BufferedSource buffer = Okio.buffer(source);
                    byte[] tempBytes = new byte[iMinBufSize];
                    runOnUiThread(listener::playStart);
                    isPlaying = true;
                    for (int len; (len = buffer.read(tempBytes)) != -1; ) {
                        if (isPlaying) {
                            audioTrack.write(tempBytes, 0, len);
                        } else {
                            break;
                        }
                    }
                    LogUtil.i("播放完毕");
                    //回调
                    runOnUiThread(listener::playEnd);
                }else {
                    String audioUrl = recordResult.getAudioUrl();
                    if (!TextUtils.isEmpty(audioUrl)){
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build());
                        mediaPlayer.setDataSource(audioUrl);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> listener.playError(e));
            }
        });
    }

    /**
     * 初始化SDK
     *
     * @param context      上下文
     * @param clientId     clientid
     * @param clientSecret secret
     * @param queryID      querid
     * @param listener     回调方法
     */
    @Override
    public void initSDK(Context context, String clientId, String clientSecret, String queryID, final InitListener listener) {
        mContext = context;
        mClientId = clientId;
        mClientSecret = clientSecret;
        mQueryID = queryID;

        NetUtil.setNetCallback(getNetCallBack());
        DetectUtil.setCallback(mContext, getDetectCallBack());
        RecordUtil.setRecordUtilCallback(mContext, getRecordCallBack());
        runOnWorkerThread(() -> {
            String token;
            try {
                token = NetUtil.requestToken();
                if (token == null || token.isEmpty()) {
                    runOnUiThread(() -> {
                        if (listener != null) {
                            listener.onInitError(new NullPointerException("token is null"));
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        if (listener != null) {
                            listener.onInitSuccess();
                        }
                    });
                }
            } catch (final IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    if (listener != null) {
                        listener.onInitError(e);
                    }
                });
            }
        });
    }

    /**
     * 设置QueryId
     *
     * @param queryID
     */
    @Override
    public void setQueryId(String queryID) {
        mQueryID = queryID;
    }

    /**
     * 提供文本内容接口。
     */
    @Override
    public void getTextList() {
        NetUtil.getTextList();
    }

    /**
     * 开启环境检测
     */
    @Override
    public int startDBDetection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (BaseUtil.hasPermission(mContext, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (getNetCallBack().getConfigData() != null && getNetCallBack().getConfigData().environmentalNoiseDetectionThreshold != 0) {
                    DetectUtil.startRecording(getNetCallBack().getConfigData().environmentalNoiseDetectionThreshold);
                }
                return 1;
            } else {
                return 0;
            }
        }
        if (getNetCallBack().getConfigData() != null && getNetCallBack().getConfigData().environmentalNoiseDetectionThreshold != 0) {
            DetectUtil.startRecording(getNetCallBack().getConfigData().environmentalNoiseDetectionThreshold);
        }
        return 1;
    }

    /**
     * 根据token申请创建模型的MID
     */
    @Override
    public void getVoiceMouldId() {
        NetUtil.getVoiceMouldId(mQueryID, getNetCallBack().getSessionId());
    }


    /**
     * 开启录音
     *
     * @param contentIndex 录音文本下标
     */
    @Override
    public int startRecord(int contentIndex) {
        currentIndex = contentIndex;
        LogUtil.e("---1");
        if (TextUtils.isEmpty(getNetCallBack().getSessionId())) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (BaseUtil.hasPermission(mContext, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                LogUtil.e("---2");
                RecordUtil.startRecord(getNetCallBack().getSessionId(), getRecordList().get(contentIndex).getAudioText());
                return 2;
            } else {
                return 1;
            }
        }
        RecordUtil.startRecord(getNetCallBack().getSessionId(), getRecordList().get(contentIndex).getAudioText());
        return 2;
    }

    /**
     * 结束录制
     */
    @Override
    public void endRecord() {
        RecordUtil.endRecordAndStartRecognize();
    }

    /**
     * 非正常结束录制
     */
    @Override
    public void recordInterrupt() {
        NetUtil.recordInterrupt(getNetCallBack().getSessionId());
        RecordUtil.stopRecord();
    }

    /**
     * 声音合成时所需token。
     */
    @Override
    public String getToken() {
        try {
            return NetUtil.requestToken();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 录制完成，提交确认信息，模型开始训练
     *
     * @return false 不满足条件|未录制完成
     */
    @Override
    public boolean finishRecords(String phone, String notifyUrl) {
        //判断是否都录制完毕
        boolean isAllRecordOver = true;
        for (RecordResult recordResult : getRecordList()) {
            if (!recordResult.isPass()) {
                isAllRecordOver = false;
                break;
            }
        }
        if (isAllRecordOver) {
            NetUtil.finishRecords(getNetCallBack().getSessionId(), phone, notifyUrl);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据mouldId查询mould信息回调
     */
    @Override
    public void getMouldInfo(String mouldId) {
        NetUtil.getMouldInfo(mouldId);
    }

    /**
     * 根据queryId分页查询mould信息回调
     *
     * @param page
     * @param limit
     * @param queryId
     */
    @Override
    public void getMouldList(int page, int limit, String queryId) {
        NetUtil.getMouldList(page, limit, queryId);
    }

    /**
     * 复刻文本相关回调
     *
     * @param callback
     */
    @Override
    public void setContentTextCallback(ContentTextCallback callback) {
        getNetCallBack().setContentTextCallback(callback);
    }

    /**
     * 噪音检测相关回调
     *
     * @param callback
     */
    @Override
    public void setDetectCallback(DetectCallback callback) {
        getDetectCallBack().setDetectCallback(callback);
    }

    /**
     * 录音上传及识别回调
     *
     * @param callback
     */
    public void setRecordCallback(RecordCallback callback) {
        getNetCallBack().setRecordCallback(callback);
        getRecordCallBack().setRecordCallback(callback);
    }

    /**
     * 开启训练回调
     *
     * @param callback
     */
    @Override
    public void setUploadRecordsCallback(UploadRecordsCallback callback) {
        getNetCallBack().setUploadRecordsCallback(callback);
    }

    /**
     * 获取模型list
     *
     * @param callback
     */
    @Override
    public void setMouldCallback(MouldCallback callback) {
        getNetCallBack().setMouldCallback(callback);
    }

    @Override
    public void requestConfig() {
        NetUtil.getConfigData();
    }

    /**
     * 设置seesionid
     *
     * @param sessionId
     */
    @Override
    public void setRecordSessionId(String sessionId) {
        getNetCallBack().voiceSessionId(sessionId);
    }
}
