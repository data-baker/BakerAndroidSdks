package com.baker.sdk.demo.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioTrackPlayer {
    private static final String TAG = "AudioTrackPlayer";
    private final int SAMPLE_RATE = 16000;
    private boolean playing = false;
    private final LinkedBlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>();

    // 初始化播放器
    private final int iMinBufSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT);

    private final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO
            , AudioFormat.ENCODING_PCM_16BIT,
            iMinBufSize * 10, AudioTrack.MODE_STREAM);

    private byte[] tempData;

    private Executor workService = Executors.newSingleThreadExecutor();

    private final Runnable mTTSPlayerRunnable = new Runnable() {

        @Override
        public void run() {
            tempData = null;
            while (playing) {
                tempData = audioQueue.poll();
                if (tempData == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
                        audioTrack.play();
                    }
                    audioTrack.write(tempData, 0, tempData.length);
                }
            }
        }
    };

    public AudioTrackPlayer() {

    }

    public void cleanAudioData() {
        audioQueue.clear();
    }

    public void setAudioData(byte[] data) {
        audioQueue.offer(data);
    }

    public void play() {
        setPlaying(true);
        workService.execute(mTTSPlayerRunnable);
        if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.play();
        }
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void stop() {
        setPlaying(false);
        audioTrack.pause();
        audioTrack.flush();
        audioQueue.clear();
    }
}
