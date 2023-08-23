package com.baker.sdk.longtime.asr.event;

/**
 * @author hsj55
 * 2020/9/24
 */
public interface EventManager {
    void send(String name, byte[] data, String params);
}
