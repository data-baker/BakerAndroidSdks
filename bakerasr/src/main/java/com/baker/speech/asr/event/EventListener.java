package com.baker.speech.asr.event;

/**
 * @author hsj55
 * 2021/2/2
 */
public interface EventListener {
    void onEvent(String name, byte[] data);
}
