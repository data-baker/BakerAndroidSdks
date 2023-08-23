package com.baker.sdk.longtime.asr.event;

/**
 * @author hsj55
 * 2020/9/24
 */
public interface EventListener {
    void onEvent(String name, byte[] data);
}
