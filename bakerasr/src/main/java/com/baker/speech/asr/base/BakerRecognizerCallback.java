package com.baker.speech.asr.base;

import com.baker.speech.asr.bean.BakerException;

import java.util.List;

/**
 * @author hsj55
 * 2021/2/2
 */
public interface BakerRecognizerCallback {
    /**
     * 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
     */
    void onReadyOfSpeech();

    /**
     * 返回音量大小
     *
     * @param volume
     */
    void onVolumeChanged(int volume);

    /**
     * 识别结果
     *
     * @param nbest
     * @param uncertain
     * @param isLast
     */
    void onResult(List<String> nbest, List<String> uncertain, boolean isLast, String traceId);

    /**
     * 此回调表示：sdk内部录音机识别到用户开始输入声音。
     */
//    void onBeginOfSpeech();

    /**
     * 此回调表示：检测到了语音的尾端点，语音输入结束。
     */
    void onEndOfSpeech();

    /**
     * 发生错误时
     *
     * @param errorBean 错误详细信息
     */
    void onError(BakerException errorBean);
}
