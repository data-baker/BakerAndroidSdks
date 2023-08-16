package com.baker.engrave.lib;

import android.Manifest;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.baker.engrave.lib.bean.Mould;
import com.baker.engrave.lib.bean.RecordResult;
import com.baker.engrave.lib.callback.BaseNetCallback;
import com.baker.engrave.lib.callback.ContentTextCallback;
import com.baker.engrave.lib.callback.DetectCallback;
import com.baker.engrave.lib.callback.InitListener;
import com.baker.engrave.lib.callback.MouldCallback;
import com.baker.engrave.lib.callback.PlayListener;
import com.baker.engrave.lib.callback.RecordCallback;
import com.baker.engrave.lib.callback.UploadRecordsCallback;
import com.baker.engrave.lib.net.NetUtil;
import com.baker.engrave.lib.util.BaseUtil;
import com.baker.engrave.lib.util.DetectUtil;
import com.baker.engrave.lib.util.HLogger;
import com.baker.engrave.lib.util.RecordUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;

/**
 * 接口需要相关数据存储到本地
 */
public class BakerVoiceEngraver implements BaseNetCallback {

    /**
     * 录音结果总条数
     */
    private static final List<RecordResult> mRecordList = new ArrayList<>();
    /**
     * 当前录音index
     */
    private static int currentIndex = 0;


    private Context mContext;
    private static String mClientId;
    private static String mClientSecret;
    private static String mQueryID;
    private static String mSessionId;
    private ContentTextCallback contentTextCallback;
    private DetectCallback detectCallback;
    private RecordCallback recordCallback;
    private UploadRecordsCallback uploadRecordsCallback;
    private MouldCallback mouldCallback;

    public static List<RecordResult> getRecordList() {
        return mRecordList;
    }

    public static int getCurrentIndex() {
        return currentIndex;
    }

    public static String getClientId() {
        return mClientId;
    }

    public static String getClientSecret() {
        return mClientSecret;
    }

    private BakerVoiceEngraver() {
    }


    public boolean isRecord(int index) {
        return getRecordList().get(index).isPass();
    }


    private final int SAMPLE_RATE = 16000;
    private boolean isPlaying = false;

    public void stopPlay() {
        isPlaying = false;
    }

    /**
     * 试听播放
     */
    public void startPlay(final int currentIndex, final PlayListener listener) {
        runOnWorkerThread(() -> {
            try {
                HLogger.d("startPlay");
                RecordResult recordResult = mRecordList.get(currentIndex);
                String filePath = recordResult.getFilePath();
                int iMinBufSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        iMinBufSize, AudioTrack.MODE_STREAM);
                audioTrack.play();
                Source source = Okio.source(new File(filePath));
                BufferedSource buffer = Okio.buffer(source);
                byte[] tempBytes = new byte[iMinBufSize];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.playStart();
                    }
                });
                isPlaying = true;
                for (int len; (len = buffer.read(tempBytes)) != -1; ) {
                    if (isPlaying) {
                        audioTrack.write(tempBytes, 0, len);
                    } else {
                        break;
                    }
                }
                HLogger.i("播放完毕");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.playEnd(); //回调
                    }
                });
            } catch (final Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.playError(e);
                    }
                });
            }
        });
    }

    private void runOnWorkerThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    private void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }


    private static final class HolderClass {
        private static final BakerVoiceEngraver instance = new BakerVoiceEngraver();
    }

    public static BakerVoiceEngraver getInstance() {
        return HolderClass.instance;
    }

    public void initSDK(Context context, String clientId, String clientSecret, String queryID, final InitListener listener) {
        mContext = context;
        mClientId = clientId;
        mClientSecret = clientSecret;
        mQueryID = queryID;

        NetUtil.setNetCallback(this);
        DetectUtil.setCallback(mContext, this);
        RecordUtil.setNetCallback(mContext, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String token = null;
                try {
                    token = NetUtil.requestToken();
                    if (token == null || token.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onInitError(new NullPointerException("token is null"));
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onInitSuccess();
                                }
                            }
                        });
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onInitError(e);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 设置QueryId
     *
     * @param queryID
     */
    public void setQueryId(String queryID) {
        mQueryID = queryID;
    }

    /**
     * 提供文本内容接口。
     */
    public void getTextList() {
        NetUtil.getTextList();
    }

    /**
     * 开启环境检测
     */
    public int startDBDetection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (BaseUtil.hasPermission(mContext, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                DetectUtil.startRecording();
                return 1;
            } else {
                return 0;
            }
        }
        DetectUtil.startRecording();
        return 1;
    }

    /**
     * 根据token申请创建模型的MID
     */
    public void getVoiceMouldId() {
        NetUtil.getVoiceMouldId(mQueryID);
    }


    /**
     * 开启录音
     *
     * @param contentIndex 录音文本下标
     */
    public int startRecord(int contentIndex) {
        currentIndex = contentIndex;
        HLogger.e("---1");
        if (TextUtils.isEmpty(mSessionId)) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (BaseUtil.hasPermission(mContext, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                HLogger.e("---2");
                RecordUtil.startRecord(mSessionId, mRecordList.get(contentIndex).getAudioText());
                return 2;
            } else {
                return 1;
            }
        }
        RecordUtil.startRecord(mSessionId, mRecordList.get(contentIndex).getAudioText());
        return 2;
    }

    public void endRecord() {
        RecordUtil.endRecordAndStartRecognize();
    }

    /**
     * 非正常结束录制
     */
    public void recordInterrupt() {
        NetUtil.recordInterrupt(mSessionId);
        RecordUtil.stopRecord();
    }

    /**
     * 声音合成时所需token。
     */
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
    public boolean finishRecords(String phone, String notifyUrl) {
        //判断是否都录制完毕
        boolean isAllRecordOver = true;
        for (RecordResult recordResult : mRecordList) {
            if (!recordResult.isPass()) {
                isAllRecordOver = false;
                break;
            }
        }
        if (isAllRecordOver) {
            NetUtil.finishRecords(mSessionId, phone, notifyUrl);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据mouldId查询mould信息回调
     */
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
    public void getMouldList(int page, int limit, String queryId) {
        NetUtil.getMouldList(page, limit, queryId);
    }

    /**
     * 复刻文本相关回调
     *
     * @param callback
     */
    public void setContentTextCallback(ContentTextCallback callback) {
        if (callback != null) {
            contentTextCallback = callback;
        }
    }

    /**
     * 噪音检测相关回调
     *
     * @param callback
     */
    public void setDetectCallback(DetectCallback callback) {
        if (callback != null) {
            detectCallback = callback;
        }
    }

    /**
     * 录音上传及识别回调
     *
     * @param callback
     */
    public void setRecordCallback(RecordCallback callback) {
        if (callback != null) {
            recordCallback = callback;
        }
    }

    /**
     * 开启训练回调
     *
     * @param callback
     */
    public void setUploadRecordsCallback(UploadRecordsCallback callback) {
        if (callback != null) {
            uploadRecordsCallback = callback;
        }
    }

    public void setMouldCallback(MouldCallback callback) {
        if (callback != null) {
            mouldCallback = callback;
        }
    }

    private void onFault(int errorCode, String errorMsg) {
//        WriteLog.writeLogs("发生错误：errorCode=" + errorCode + ",errorMsg=" + errorMsg);
        HLogger.e("发生错误：errorCode=" + errorCode + ",errorMsg=" + errorMsg);
    }

    @Override
    public void netTokenError(int errorCode, String message) {
        Log.e("BakerVoiceEngraver", "error happened: " + errorCode + ", " + message);
    }

    @Override
    public void token(String token) {
        if (!TextUtils.isEmpty(token)) {
        }
    }


    @Override
    public void recordTextList(String[] recordTextList) {
        if (recordTextList != null && contentTextCallback != null) {
            HLogger.d("取到了text，text.length=" + recordTextList.length);
            mRecordList.clear();
            for (String text : recordTextList) {
                RecordResult recordResult = new RecordResult(text, 0, false);
                mRecordList.add(recordResult);
            }
            contentTextCallback.contentTextList(recordTextList);
        }
    }

    @Override
    public void netContentTextError(int errorCode, String message) {
        onFault(errorCode, message);
        if (contentTextCallback != null) {
            contentTextCallback.onContentTextError(errorCode, message);
        }
    }

    @Override
    public void dbDetecting(int value) {
        if (detectCallback != null) {
            detectCallback.dbDetecting(value);
        }
    }

    @Override
    public void dbDetectionResult(boolean result, int value) {
        HLogger.i("result=" + result + ", value=" + value);
        if (detectCallback != null) {
            detectCallback.dbDetectionResult(result, value);
        }
    }

    @Override
    public void netDetectError(int errorCode, String message) {
        onFault(errorCode, message);
        if (detectCallback != null) {
            detectCallback.onDetectError(errorCode, message);
        }
    }

    @Override
    public void voiceSessionId(String sessionId) {
        if (!TextUtils.isEmpty(sessionId)) {
            mSessionId = sessionId;
        }
    }

    /**
     * 录音中、识别中、识别结果回调。
     *
     * @param typeCode        typeCode=1，录音中 typeCode=2，识别中 typeCode=3，最终结果：通过  typeCode=4，最终结果：不通过
     * @param recognizeResult 识别率
     */
    @Override
    public void recordsResult(int typeCode, int recognizeResult) {
        if (recordCallback != null) {
            HLogger.e("---5");
            recordCallback.recordsResult(typeCode, recognizeResult);
        }
    }

    @Override
    public void recordVolume(int volume) {
        if (recordCallback != null) {
            recordCallback.recordVolume(volume);
        }
    }

    @Override
    public void netRecordError(int errorCode, String message) {
        onFault(errorCode, message);
        if (recordCallback != null) {
            recordCallback.onRecordError(errorCode, message);
        }
    }

    @Override
    public void uploadRecordsResult(boolean result) {
        if (uploadRecordsCallback != null) {
            String mouldId = null;
            if (!TextUtils.isEmpty(mSessionId)) {
                mouldId = mSessionId.substring(0, mSessionId.length() - 13);
                HLogger.e("截取后mouldId：" + mouldId);
            }
            uploadRecordsCallback.uploadRecordsResult(result, mouldId);
        }
    }

    @Override
    public void onUploadError(int errorCode, String message) {
        onFault(errorCode, message);
        if (uploadRecordsCallback != null) {
            uploadRecordsCallback.onUploadError(errorCode, message);
        }
    }

    @Override
    public void mouldInfo(Mould mould) {
        if (mouldCallback != null) {
            mouldCallback.mouldInfo(mould);
        }
    }

    @Override
    public void mouldList(List<Mould> list) {
        if (mouldCallback != null) {
            mouldCallback.mouldList(list);
        }
    }

    @Override
    public void onMouldError(int errorCode, String message) {
        onFault(errorCode, message);
        if (mouldCallback != null) {
            mouldCallback.onMouldError(errorCode, message);
        }
    }
}
