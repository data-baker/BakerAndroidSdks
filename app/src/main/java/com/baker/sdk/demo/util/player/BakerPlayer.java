package com.baker.sdk.demo.util.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BakerPlayer {
    private final String TAG = "BakerPlayer";
    private static final List<OneSecPcmBlock> playData = new ArrayList<>();
    private static int position = 0;
    private static boolean isFinished = false;
    private boolean playing = false;

    private final int SAMPLE_RATE = 16000;
    // 初始化播放器
    private final int iMinBufSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT);

    private final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO
            , AudioFormat.ENCODING_PCM_16BIT,
            iMinBufSize * 10, AudioTrack.MODE_STREAM);

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 205) {
                if (audioTrack != null) {
                    audioTrack.pause();
                }
                if (callBack != null) {
                    callBack.onPlayCompleted();
                }
            }
        }
    };

    private final Thread ttsPlayerThread;
    public BakerPlayer() {
        playing = true;

        ttsPlayerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (playing) {
                        if (position == playData.size() && playData.size() > 0 && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING && isFinished) {
                            Log.e(TAG,"--onCompletion--");
                            position = 0;
//                                playData.clear();
                            //立刻暂停会导致最后0.xs声音不会播放。故延迟500ms暂停
                            handler.sendEmptyMessageDelayed(205, 500);
                        } else if (position < playData.size() && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                            audioTrack.write(playData.get(position).getBlockbytes(), 0, playData.get(position).getBlockbytes().length);
                            position++;
                        } else {
                            Thread.sleep(500);
                        }
                    }
                } catch (Exception e) {
                    if (callBack != null) {
                        callBack.onError("p03", "播放出错");
                    }
                }
            }
        });
        ttsPlayerThread.start();
    }

    private PlayerCallBack callBack;

    public void setCallBack(PlayerCallBack callBack) {
        this.callBack = callBack;
    }

    private byte[] remaining;
    private byte[] tempBytes;

    public void setData(byte[] data, boolean finish) {
        try {
            int oneSecLength = 3200;
            if (data != null && data.length > 0) {
                byte[] temp;
                //若之前有剩余的，先将剩余数据copy至temp数组。
                if (remaining != null && remaining.length > 0) {
                    int remainingLen = remaining.length;
                    temp = new byte[remainingLen + data.length];
                    System.arraycopy(remaining, 0, temp, 0, remainingLen);
                    System.arraycopy(data, 0, temp, remainingLen, data.length);
                    remaining = null;
                } else {
                    //若之前没有剩余的，直接将data数据copy至temp数组。
                    temp = new byte[data.length];
                    System.arraycopy(data, 0, temp, 0, data.length);
                }
                int size = temp.length / oneSecLength;

                //先处理整数倍的秒数数据。
                for (int i = 0; i < size; i++) {
                    System.arraycopy(temp, i * oneSecLength, tempBytes = new byte[oneSecLength], 0, oneSecLength);
                    setAudioData(new OneSecPcmBlock(tempBytes), false);
                }
                int remainder = temp.length % oneSecLength;
                //余数部分处理
                if (finish) {
                    //若当前是合成的最后一段返回数据
                    if (remainder > 0) {
                        //直接拼成最后一秒数据给播放器
                        System.arraycopy(temp, temp.length - remainder, tempBytes = new byte[oneSecLength], 0, remainder);
                        setAudioData(new OneSecPcmBlock(tempBytes), true);
                    } else {
                        //告知播放器已合成完成
                        setAudioData(null, true);
                    }
                    remaining = null;
                } else {
                    if (remainder > 0) {
                        //将余数部分copy至remaining数组，给下批数据回来后一起处理。
                        System.arraycopy(temp, temp.length - remainder, remaining = new byte[remainder], 0, remainder);
                    }
                }
            } else if (finish) {
                Log.e(TAG,"这是最后一片数据, 且数据为空.");
                if (remaining != null && remaining.length > 0) {
                    System.arraycopy(remaining, 0, tempBytes = new byte[oneSecLength], 0, remaining.length);
                    setAudioData(new OneSecPcmBlock(tempBytes), true);
                } else {
                    setAudioData(null, true);
                }
                remaining = null;
            } else {
                Log.e(TAG,"errorCode=p01, errorMsg=接收数据为空");
            }
            //通知播放器更新缓存进度
//            BakerMediaPlayer.getInstance().onCacheAvailable();
        } catch (Exception e) {
            Log.e(TAG,"errorCode=p02, errorMsg=" + e.getMessage());
        }
    }

    private void setAudioData(OneSecPcmBlock data, boolean finish) {
        if (data != null) {
            playData.add(data);
        }
        isFinished = finish;
    }

    public void play() {
        if (null == audioTrack) return;
        if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED
                || audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
            audioTrack.play();
            if (callBack != null) {
                callBack.onPlaying();
            }
        }
    }

    public void pause() {
        if (null == audioTrack) return;
        if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.pause();
            if (callBack != null) {
                callBack.onPaused();
            }
        }
    }

    public void stop() {
        pause();
        if (audioTrack != null) {
            audioTrack.flush();
        }
        playData.clear();
        position = 0;

        if (callBack != null) {
            callBack.onStopped();
        }
    }

    public void clean() {
        pause();
        if (audioTrack != null) {
            audioTrack.flush();
        }
        playData.clear();
        position = 0;
        isFinished = false;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}
