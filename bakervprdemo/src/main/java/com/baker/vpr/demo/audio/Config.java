package com.baker.vpr.demo.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Config {
    /**
     * 采样率。现在能够保证在所有设备上使用的采样率是44100Hz
     */
    public static final int SAMPLE_RATE_INHZ = 44100;
    /**
     * 输入声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
     */
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    public static final int ENCODING_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * 输出声道数
     */
    public static final int TRACK_CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    /**
     * AudioTrack播放的类型
     */
    public static final int TRACK_PLAY_MODE = AudioTrack.MODE_STREAM;
    /**
     * AudioTrack播放的Type
     */
    public static final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;
}
