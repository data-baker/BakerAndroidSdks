package com.baker.vpr.demo.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.media.AudioTrack;
import android.util.Log;

import com.baker.vpr.demo.audio.AudioEffect.AudioProcessor;


public class AudioTrackPlay {
    private static final String TAG = AudioTrackPlay.class.getSimpleName();

    private AudioTrack mAudioTrack;

    private volatile boolean isPlaying;

    private File mFile;

    private Thread mThread;

    private float ratio;

    private AudioProcessor audioProcessor;

    private int mMinBufferSize;//最小缓存大小

    public void startPlay(File file, float ratio) {
        mFile = file;
        this.ratio = ratio;

        if (isPlaying) {
            Log.e(TAG, "Player already started !");
            return;
        }
        mMinBufferSize = AudioTrack.getMinBufferSize(Config.SAMPLE_RATE_INHZ, Config.TRACK_CHANNEL_CONFIG, Config.ENCODING_FORMAT);
        if (mMinBufferSize == AudioTrack.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter !");
            return;
        }
        Log.e(TAG, "getMinBufferSize = " + mMinBufferSize + " bytes !");
        mAudioTrack = new AudioTrack(Config.DEFAULT_STREAM_TYPE, Config.SAMPLE_RATE_INHZ,
                Config.TRACK_CHANNEL_CONFIG, Config.ENCODING_FORMAT, mMinBufferSize, Config.TRACK_PLAY_MODE);
        if (mAudioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioTrack initialize fail !");
            return;
        }

        audioProcessor = new AudioProcessor(2048);

        isPlaying = true;

        mThread = new Thread(new AudioTrackRunnable());
        mThread.start();
    }

    public void stopPlay() {
        if (!isPlaying) {
            return;
        }
        if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.stop();
        }
        //释放
        mAudioTrack.release();
        isPlaying = false;
        Log.e(TAG, "Stop audio player success !");
    }

    private class AudioTrackRunnable implements Runnable {
        @Override
        public void run() {
            FileInputStream inStream = null;
            int readCount;
            try {
                byte[] tempBuffer = new byte[mMinBufferSize];
                inStream = new FileInputStream(mFile);
                while (inStream.available() > 0) {
                    readCount = inStream.read(tempBuffer);
                    if (readCount == AudioTrack.ERROR_BAD_VALUE || readCount == AudioTrack.ERROR_INVALID_OPERATION) {
                        continue;
                    }
                    //对读到的流进行处理
                    tempBuffer = ratio == 1 ? tempBuffer :
                            audioProcessor.process(ratio, tempBuffer, Config.SAMPLE_RATE_INHZ);
                    if (readCount != 0 && readCount != -1 && isPlaying) {
                        mAudioTrack.write(tempBuffer, 0, readCount);
                        mAudioTrack.play();
                    }
                    Log.d(TAG, "OK, Played " + tempBuffer + " bytes !");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
