package com.baker.sdk.longtime.asr.event;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * @author hsj55
 * 2020/9/24
 */
public class EventManagerMessagePool {
    private static Handler sWorkHandler;
    static {
        HandlerThread t = new HandlerThread("msg-owner-long-time-asr");
        t.start();
        sWorkHandler = new Handler(t.getLooper());
    }

    public static void offer(final EventManager manager, final String name,final byte[] data, final String params) {
        if (null == manager) {
            return;
        }
        sWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                manager.send(name, data, params);
            }
        });
    }

    public static void offer(EventManager owner, String name) {
        offer(owner, name, null, null);
    }

    public static void offer(EventManager owner, String name, byte[] data) {
        offer(owner, name, data, null);
    }

    public static void offer(EventManager owner, String name, String params) {
        offer(owner, name, null, params);
    }

    public static void clean(){
        sWorkHandler.removeCallbacksAndMessages(null);
    }
}
