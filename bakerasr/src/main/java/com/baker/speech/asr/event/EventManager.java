package com.baker.speech.asr.event;

/**
 * @author hsj55
 * 2021/2/2
 */
public interface EventManager {
    void send(String name, byte[] data, String params);
}
