package com.baker.sdk.basecomponent;

import com.baker.sdk.basecomponent.bean.BakerBaseComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hsj55
 * 2020/9/15
 */
public class BakerBaseConstants {
    private static boolean isDebug = false;
    public static String TAG_TTS_ONLINE = "android_sdk_tts_online";
    public static String TAG_TTS_OFFLINE = "android_sdk_tts_offline";
    public static String TAG_ASR_ONLINE = "android_sdk_asr_online";
    public static String TAG_ASR_LONG_TIME = "android_sdk_asr_long_time";
    public static String TAG_VOICE_ENGRAVE = "android_sdk_voice_engrave";


    public static final String UTF_8 = "UTF-8";
    public static final String LANGUAGE_ZH = "ZH";
    public static final String LANGUAGE_ENG = "ENG";
    public static final String LANGUAGE_CAT = "CAT";
    public static final int RATE_16K = 2;
    public static final int RATE_8K = 1;

    public static final int AUDIO_TYPE_PCM_16K = 4;
    public static final int AUDIO_TYPE_PCM_8K = 5;
    public static final int AUDIO_TYPE_WAV_16K = 6;

    public static final String VOICE_NORMAL = "标准合成_模仿儿童_果子";

    public static boolean isIsDebug() {
        return isDebug;
    }

    public static void setIsDebug(boolean debug) {
        isDebug = debug;
    }

    public static final boolean K16 = true;
    public static final boolean K8 = false;

    public static String ttsToken;
    public static String mClientId;
    public static String mClientSecret;

    public static String SP_STATISTICS_FLAG = "sp_statistics_flag";
    public static String SP_STATISTICS_FLAG_KEY_FIRST = "sp_statistics_flag_key_first";
    public static String SP_SWITCH_KEY = "sp_switch_key";

    public static List<BakerBaseComponent> components = new ArrayList<>();

    public static BakerBaseComponent getBakerBaseComponentByTag(String tag) {
        for (BakerBaseComponent c : components) {
            if (tag.equals(c.getTag())) {
                return c;
            }
        }
        return null;
    }
}
