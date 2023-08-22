package com.baker.engrave.lib.util;

import android.util.Log;

public class LogUtil {

    private static String tag = "TAG----->";

    public static boolean isDebug = true;


    private static String className, methodName;
    private static int lineNumber;

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    public static void i(String message) {
        if (isDebug) {
            i(new Throwable().getStackTrace(), tag, message);
        }
    }

    public static void d(String message) {
        if (isDebug) {
            d(new Throwable().getStackTrace(), tag, message);
        }
    }

    public static void v(String message) {
        if (isDebug) {
            v(new Throwable().getStackTrace(), tag, message);
        }
    }

    public static void w(String message) {
        if (isDebug) {
            w(new Throwable().getStackTrace(), tag, message);
        }
    }

    public static void e(String message) {
        if (isDebug) {
            e(new Throwable().getStackTrace(), tag, message);
        }
    }

    public void i(String tag, String message) {
        if (isDebug) {
            i(new Throwable().getStackTrace(), tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (isDebug) {
            d(new Throwable().getStackTrace(), tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (isDebug) {
            v(new Throwable().getStackTrace(), tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (isDebug) {
            w(new Throwable().getStackTrace(), tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (isDebug) {
            e(new Throwable().getStackTrace(), tag, message);
        }
    }

    private static void i(StackTraceElement[] sElements, String tag, String message) {
        getMethodNames(sElements);
        Log.i(tag, String.format("(%1s#%2s()#%3s)%4s", className, methodName, lineNumber, message));
    }

    private static void d(StackTraceElement[] sElements, String tag, String message) {
        getMethodNames(sElements);
        Log.d(tag, String.format("(%1s#%2s()#%3s)%4s", className, methodName, lineNumber, message));
    }

    private static void v(StackTraceElement[] sElements, String tag, String message) {
        getMethodNames(sElements);
        Log.v(tag, String.format("(%1s#%2s()#%3s)%4s", className, methodName, lineNumber, message));
    }

    private static void w(StackTraceElement[] sElements, String tag, String message) {
        getMethodNames(sElements);
        Log.w(tag, String.format("(%1s#%2s()#%3s)%4s", className, methodName, lineNumber, message));
    }

    public static void e(StackTraceElement[] sElements, String tag, String message) {
        getMethodNames(sElements);
        Log.e(tag, String.format("(%1s#%2s()#%3s)%4s", className, methodName, lineNumber, message));
    }


}
