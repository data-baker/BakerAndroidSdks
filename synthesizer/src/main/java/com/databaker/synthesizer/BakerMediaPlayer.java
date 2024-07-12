package com.databaker.synthesizer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.baker.sdk.basecomponent.BakerBaseConstants;
import com.baker.sdk.basecomponent.util.HLogger;
import com.baker.sdk.basecomponent.util.LogUtils;
import com.baker.sdk.basecomponent.writelog.WriteLog;
import com.databaker.synthesizer.bean.OneSecPcmBlock;
import com.databaker.synthesizer.util.SynthesizerErrorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by hsj55
 * 2019/12/13
 */
public class BakerMediaPlayer {
    private boolean k16OrK8 = BakerBaseConstants.K16;
    private static final List<OneSecPcmBlock> playData = new ArrayList<>();
    private static int duration = 0;  //单位是：秒，预估时取整秒数。
    private static int position = 0;
    private static boolean isFinished = false;
    private BakerMediaCallback callback;

    // 初始化播放器
    private AudioTrack audioTrack;

    private Thread ttsPlayerThread;

    private static final class HolderClass {
        private static final BakerMediaPlayer inStance = new BakerMediaPlayer();
    }

    public static BakerMediaPlayer getInstance() {
        return HolderClass.inStance;
    }

    private BakerMediaPlayer() {
        LogUtils.getInstance().d("BakerMediaPlayer构造方法初始化");
    }

    public void init(boolean rate) {
        LogUtils.getInstance().d("BakerMediaPlayer init 开始");
        if (k16OrK8 != rate || audioTrack == null) {
            k16OrK8 = rate;
            int iMinBufSize = AudioTrack.getMinBufferSize(k16OrK8 ? 16000 : 8000,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, k16OrK8 ? 16000 : 8000,
                    AudioFormat.CHANNEL_OUT_MONO
                    , AudioFormat.ENCODING_PCM_16BIT,
                    iMinBufSize, AudioTrack.MODE_STREAM);
        }

        if (ttsPlayerThread == null) {
            ttsPlayerThread = new Thread(() -> {
                try {
                    while (true) {
                        if (position == playData.size() && playData.size() > 0 && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING && isFinished) {
                            if (callback != null) {
                                LogUtils.getInstance().d("--onCompletion--");
                                audioTrack.pause();
                                callback.onCompletion();
                            }
                        } else if (position < playData.size() && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                            audioTrack.write(playData.get(position).getBlockbytes(), 0, playData.get(position).getBlockbytes().length);
                            position++;
                        } else {
                            Thread.sleep(500);
                        }
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onError(SynthesizerErrorUtils.formatErrorBean(BakerSynthesizerErrorConstants.ERROR_CODE_MEDIA_ERROR));
                        LogUtils.getInstance().error("errorCode=90006, errorMsg=" + e.getMessage());
                    }
                }
            });
        }
        if (!ttsPlayerThread.isAlive()) {
            ttsPlayerThread.start();
        }
        LogUtils.getInstance().d("BakerMediaPlayer init 结束");
    }

    public void play() {
        LogUtils.getInstance().d("BakerMediaPlayer play 开始");
        if (null == audioTrack) return;
        if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED
                || audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
            audioTrack.play();
            LogUtils.getInstance().d("BakerMediaPlayer play()");
            if (callback != null) {
                callback.playing();
            }
        }
        LogUtils.getInstance().d("BakerMediaPlayer play 结束");
    }

    public void pause() {
        LogUtils.getInstance().d("BakerMediaPlayer pause 开始");
        if (null == audioTrack) return;
        if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.pause();
            LogUtils.getInstance().d("BakerMediaPlayer pause()");
            if (callback != null) {
                callback.noPlay();
            }
        }
        LogUtils.getInstance().d("BakerMediaPlayer pause 结束");
    }

    public void setAudioData(OneSecPcmBlock data, boolean finish) {
        if (null != data) {
            LogUtils.getInstance().d("BakerMediaPlayer setAudioData " + (null != data.getBlockbytes() ? data.getBlockbytes().length : null) + finish);
        }
        if (data != null)
            playData.add(data);
        isFinished = finish;
        if (finish) {
            duration = playData.size();
            LogUtils.getInstance().d("finish, duration=" + duration);
        }
    }

    public void onCacheAvailable() {
        if (callback != null && duration != 0) {
            callback.onCacheAvailable(playData.size() * 100 / duration);
        }
    }

    public int getDuration() {
        return duration / 10;
    }

    public void setDuration(int d) {
        duration = d;
    }

    public int getCurrentPosition() {
        return position / 10;
    }

    public boolean isPlaying() {
        if (null == audioTrack) return false;
        return audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    public void stop() {
        LogUtils.getInstance().d("BakerMediaPlayer stop 开始");
        pause();
        if (audioTrack != null) {
            audioTrack.flush();
        }
        playData.clear();
        position = 0;
        LogUtils.getInstance().d("BakerMediaPlayer stop 结束");
    }

    public void setCallback(BakerMediaCallback c) {
        this.callback = c;
    }

    public boolean get8KOr16K() {
        return k16OrK8;
    }

    public void clean() {
        LogUtils.getInstance().d("BakerMediaPlayer clean 开始");
        pause();
        if (audioTrack != null) {
            audioTrack.flush();
        }
        playData.clear();
        position = 0;
        isFinished = false;
        LogUtils.getInstance().d("BakerMediaPlayer clean 结束");
    }
}
