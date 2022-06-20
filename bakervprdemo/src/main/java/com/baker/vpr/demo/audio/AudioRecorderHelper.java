package com.baker.vpr.demo.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorderHelper {
    private static final String TAG = AudioRecorderHelper.class.getSimpleName();

    private static AudioRecorderHelper mAudioRecorderHelper;

    private AudioRecord mAudioRecorder;

    private volatile boolean isRecording = false;

    private Thread mThread;

    private File file;

    private byte[] mBufferByte;
    private RecorderCallback mRecorderCallback;

    @SuppressLint("MissingPermission")
    private AudioRecorderHelper() {

    }

    public static AudioRecorderHelper getInstance() {
        if (mAudioRecorderHelper == null) {
            mAudioRecorderHelper = new AudioRecorderHelper();
        }
        return mAudioRecorderHelper;
    }

    public Boolean getRecordStatus() {
        return isRecording;
    }

    @SuppressLint("MissingPermission")
    public void startRecorder(File file, RecorderCallback callback) {
        mRecorderCallback = callback;
        this.file = file;
        if (isRecording) {
            Log.e(TAG, "Recorder already started !");
            return;
        }
  /*
          audioSource:    音频采集的输入源，可选的值以常量的形式定义在 MediaRecorder.AudioSource 类中,例如：MIC（由手机麦克风输入），VOICE_COMMUNICATION（用于VoIP应用）等等。
          sampleRateInHz: 采样率，注意，目前44100Hz是唯一可以保证兼容所有Android手机的采样率。
          channelConfig:  通道数的配置，可选的值以常量的形式定义在 AudioFormat 类中，常用的是 CHANNEL_IN_MONO（单通道），CHANNEL_IN_STEREO（双通道）
          audioFormat:    这个参数是用来配置“数据位宽”的，可选的值也是以常量的形式定义在 AudioFormat 类中，常用的是 ENCODING_PCM_16BIT（16bit），ENCODING_PCM_8BIT（8bit），注意，前者是可以保证兼容所有Android手机的。
          bufferSizeInBytes:  AudioRecord 内部的音频缓冲区的大小，该缓冲区的值不能低于一帧“音频帧”（Frame）的大小
         */
        int minBufferSize = AudioRecord.getMinBufferSize(Config.SAMPLE_RATE_INHZ, Config.CHANNEL_CONFIG, Config.ENCODING_FORMAT);
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter !");
            return;
        }

        mBufferByte = new byte[minBufferSize];

        mAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, Config.SAMPLE_RATE_INHZ,
                Config.CHANNEL_CONFIG, Config.ENCODING_FORMAT, minBufferSize);
        if (mAudioRecorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioRecord initialize fail !");
            return;
        }
        mAudioRecorder.startRecording();
        mThread = new Thread(new AudioRunnable());
        mThread.start();
        isRecording = true;
    }

    public void stopRecorder() {
        if (!isRecording) {
            return;
        }
        mAudioRecorder.release();
        isRecording = false;
        mAudioRecorder = null;
        Log.e(TAG, "Stop AudioRecorder success !");
    }

    /**
     * 获取某一段数据的分贝值-针对pcm的原始音频数据
     * <p>
     * data : 某一时间段pcm数据
     * dataLenngth :有效数据大小
     * 这个分贝值估计值
     */
    public double getDecibelForPcm(byte[] data, int audioFormat) {
        long sum = 0;
        long temp = 0;
        for (int i = 0; i < data.length; i += audioFormat) {
            if (audioFormat == 2) {
                temp = ((long) data[i + 1] * 128 + data[i]); //累加求和
            } else if (audioFormat == 4) {
                temp = ((long) data[i + 3] * 128 + data[i + 2] + data[i + 1] + data[i]); //累加求和
            }
            temp *= temp;
            sum += temp;
        }

        //平方和除以数据长度，得到音量大小
        double square = sum / ((double) data.length); //音量大小
        //16位的是96db ,32位的是193db
        double result = 10 * Math.log10(square * 4); //分贝值
        Log.i(TAG, "square:" + square + "\tresult:" + result);
        return result;
    }


    private class AudioRunnable implements Runnable {
        @Override
        public void run() {
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(file);
                while (isRecording) {
                    int ret = mAudioRecorder.read(mBufferByte, 0, mBufferByte.length);
                    mRecorderCallback.onRecorded(mBufferByte);
                    os.write(mBufferByte);
                    if (ret == AudioRecord.ERROR_INVALID_OPERATION) {
                        Log.e(TAG, "Error ERROR_INVALID_OPERATION");
                    } else if (ret == AudioRecord.ERROR_BAD_VALUE) {
                        Log.e(TAG, "Error ERROR_BAD_VALUE");
                    } else {
                        Log.e("TAG", "Audio captured: " + mBufferByte.length);
                    }
                }
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
