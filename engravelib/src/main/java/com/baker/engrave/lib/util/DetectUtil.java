package com.baker.engrave.lib.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.baker.engrave.lib.VoiceEngraveConstants;
import com.baker.engrave.lib.callback.BaseNetCallback;
import com.baker.engrave.lib.callback.innner.DetectUtilCallBack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;

/**
 * Create by hsj55
 * 2020/3/6
 */

public class DetectUtil {

    private static DetectUtilCallBack detectUtilCallBack;
    private static Recorder mRecorder;
    private static Context mContext;
    private static AudioManager mAudioManager;
    private static AudioFocusRequest mFocusRequest;
    private static TelephonyManager mTelephonyManager;
    //private static final File mAudioFile = new File(Environment.getExternalStorageDirectory() + File.separator + "DecibelDetection" + File.separator + "AudioTemporaryCache.pcm");
    private static File mAudioFile;
    //记录当前显示的分贝值
    private static int decibels = 0;
    //保存所有分贝值，用来计算平均值
    private static final List<Integer> decibelsList = new ArrayList<>();
    //显示最终分贝
    private static boolean isLast;
    private static boolean recording = false;
    private static int mNoise;

    public static File getAudioFile() {
        if (mContext != null && mAudioFile == null) {
            mAudioFile = new File(mContext.getFilesDir() + File.separator + "DecibelDetection" + File.separator + "AudioTemporaryCache.pcm");
        }
        return mAudioFile;
    }

    public static void setCallback(Context context, DetectUtilCallBack callback) {
        mContext = context;
        detectUtilCallBack = callback;
    }

    private static final CountDownTimer timer = new CountDownTimer(3250, 250) {
        @Override
        public void onTick(long l) {
            if (decibels > 0)
                decibelsList.add(decibels);
        }

        @Override
        public void onFinish() {
            isLast = true;
            stopRecording();
            decibelsList.add(decibels);
            List<Integer> highList = new ArrayList<>();
            List<Integer> lowList = new ArrayList<>();
            for (Integer i : decibelsList) {
                if (i > (mNoise+10)) {
                    highList.add(i);
                } else {
                    lowList.add(i);
                }
            }
            if (highList.size() > 2) {
                int sum = 0;
                for (Integer i : highList) {
                    sum += i;
                }
                if (detectUtilCallBack != null) {
                    detectUtilCallBack.dbDetectionResult(false, sum / highList.size());
                }
            } else {
                int sum = 0;
                for (Integer i : lowList) {
                    sum += i;
                }
                if (detectUtilCallBack != null) {
                    detectUtilCallBack.dbDetectionResult(true, sum / lowList.size());
                }
            }
        }
    };

    /**
     * 开始录制
     */
    public static void startRecording(int noise) {
        mNoise = noise;
        recording = true;
        isLast = false;
        mRecorder = OmRecorder.pcm(
                new PullTransport.Default(mic(), audioChunk -> {
                    decibels = (int) audioChunk.maxAmplitude();
                    if (decibels > 20 && !isLast) {
                        if (detectUtilCallBack != null) {
                            detectUtilCallBack.dbDetecting(decibels);
                        }
                    }
                }), file());
        getAudioFocus();
        mRecorder.startRecording();
        decibelsList.clear();
        if (timer != null) {
            timer.cancel();
            timer.start();
        }
    }

    /**
     * 停止录制
     */
    public static void stopRecording() {
        recording = false;
        isLast = true;
        if (mRecorder == null) return;
        try {
            mRecorder.stopRecording();
            reset();
        } catch (Exception e) {
            if (detectUtilCallBack != null) {
                detectUtilCallBack.netDetectError(VoiceEngraveConstants.ERROR_CODE_STOP_DETECT, "stop detect error: " + e.getMessage());
            }
        }
    }

    private static PullableSource mic() {
        //16，单声道，16000
        return new PullableSource.Default(
                new AudioRecordConfig.Default(MediaRecorder.AudioSource.MIC,
                        AudioFormat.ENCODING_PCM_16BIT, AudioFormat.CHANNEL_IN_MONO, 16000));
    }

    @NonNull
    private static File file() {
        if (getAudioFile().exists()) {
            getAudioFile().delete();
        }
        try {
            if (!getAudioFile().getParentFile().exists()) {
                getAudioFile().getParentFile().mkdirs();
            }
            if (!getAudioFile().exists()) {
                getAudioFile().createNewFile();
            }
        } catch (IOException e) {
            detectUtilCallBack.netDetectError(VoiceEngraveConstants.ERROR_CODE_FILE, "create detect file error: " + e.getMessage());
        }
        return getAudioFile();
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
        try {
            //手动注册对PhoneStateListener中的listen_call_state状态进行监听
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    private static final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //电话通话的状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    LogUtil.e("电话进来了:" + recording);
                    //电话响铃的状态
                    if (timer != null) {
                        timer.cancel();
                    }
                    if (detectUtilCallBack != null && recording) {
                        detectUtilCallBack.netDetectError(VoiceEngraveConstants.ERROR_CODE_INTERRUPT, "Detect interruption due to abnormality");
                    }
                    stopRecording();
                    break;
            }
            super.onCallStateChanged(state, phoneNumber);
        }
    };

    private static final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            if (i == AudioManager.AUDIOFOCUS_LOSS) {
                LogUtil.e("失去了焦点:" + recording);
                if (timer != null) {
                    timer.cancel();
                }
                if (detectUtilCallBack != null && recording) {
                    detectUtilCallBack.netDetectError(VoiceEngraveConstants.ERROR_CODE_INTERRUPT, "Detect interruption due to abnormality");
                }
                stopRecording();
            }
        }
    };

    private static void reset() {
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }
}
