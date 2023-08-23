package com.databaker.voiceconvert.util;

import android.util.Log;

public class Utils {

    public static boolean isPrintLog = true;

    public static void log(String msg) {
        if (isPrintLog) {
            Log.d("dbvclog", msg);
        }
    }

}