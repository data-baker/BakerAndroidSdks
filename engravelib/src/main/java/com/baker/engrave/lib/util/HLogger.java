package com.baker.engrave.lib.util;

import android.text.TextUtils;
import android.util.Log;

/**
 * Create by hsj55
 * 2020/3/6
 */
public class HLogger {
    public static boolean DEBUG = false;

    private static final int CHUNK_SIZE = 100;
    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char MIDDLE_CORNER = '├';
    private static final char HORIZONTAL_LINE = '│';
    private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    private static String className;
    private static String methodName;
    private static int lineNumber;

    public static void setDebug(boolean DEBUG) {
        HLogger.DEBUG = DEBUG;
    }

    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("bakerLog=>");
        buffer.append("TN:").append(Thread.currentThread().getName());
//        buffer.append(" method:").append(methodName);
        buffer.append(" (").append(className).append(":").append(lineNumber).append("):");
        buffer.append(log);
        return buffer.toString();
    }

    private static String createLog() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
        buffer.append(methodName);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    //超长文本打印
    public static void longError(String message) {
        if (!DEBUG) return;
        if (!TextUtils.isEmpty(message)) {
            getMethodNames(new Throwable().getStackTrace());
            Log.e(className, TOP_BORDER);
            Log.i(className, HORIZONTAL_LINE + " " + createLog());
            Log.i(className, MIDDLE_BORDER + " ");
            byte[] bytes = message.getBytes();
            int length = bytes.length;
            if (length <= CHUNK_SIZE) {
                String[] lines = message.split(System.getProperty("line.separator"));
                for (String line : lines) {
                    Log.e(className, HORIZONTAL_LINE + " " + line);
                }
            } else {
                for (int i = 0; i < length; i += CHUNK_SIZE) {
                    int count = Math.min(length - i, CHUNK_SIZE);
                    String[] lines = new String(bytes, i, count).split(System.getProperty("line.separator"));
                    for (String line : lines) {
                        Log.e(className, HORIZONTAL_LINE + " " + line);
                    }
                }
            }
            Log.e(className, BOTTOM_BORDER);
        }
    }

    //超长文本打印
    public static void longInfo(String message) {
        if (!DEBUG) return;
        if (!TextUtils.isEmpty(message)) {
            getMethodNames(new Throwable().getStackTrace());
            //先打印横线
            Log.i(className, TOP_BORDER);
            //再打印类名和行号及调用方法名
            Log.i(className, HORIZONTAL_LINE + " " + createLog());
            //再打印中间横线
            Log.i(className, MIDDLE_BORDER + " ");
            byte[] bytes = message.getBytes();
            int length = bytes.length;
            //判断文本内容长度
            if (length <= CHUNK_SIZE) {
                //短文本，再判断文本内容是否有换行符
                String[] lines = message.split(System.getProperty("line.separator"));
                for (String line : lines) {
                    Log.i(className, HORIZONTAL_LINE + " " + line);
                }
            } else {
                //长文本循环打印，每一行中判断是否有换行符
                for (int i = 0; i < length; i += CHUNK_SIZE) {
                    int count = Math.min(length - i, CHUNK_SIZE);
                    String[] lines = new String(bytes, i, count).split(System.getProperty("line.separator"));
                    for (String line : lines) {
                        Log.i(className, HORIZONTAL_LINE + " " + line);
                    }
                }
            }
            Log.i(className, BOTTOM_BORDER);
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            if (!TextUtils.isEmpty(msg)) {
                getMethodNames(new Throwable().getStackTrace());
                Log.d(className, createLog(msg));
            }
        }
    }

    public static void v(String msg) {
        if (DEBUG) {
            if (!TextUtils.isEmpty(msg)) {
                getMethodNames(new Throwable().getStackTrace());
                Log.v(className, createLog(msg));
            }
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            if (!TextUtils.isEmpty(msg)) {
                getMethodNames(new Throwable().getStackTrace());
                Log.e(className, createLog(msg));
            }
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            if (!TextUtils.isEmpty(msg)) {
                getMethodNames(new Throwable().getStackTrace());
                Log.i(className, createLog(msg));
            }
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            if (!TextUtils.isEmpty(msg)) {
//                Log.d(tag, msg);
                getMethodNames(new Throwable().getStackTrace());
                Log.d(className, createLog(msg));
            }
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            if (!TextUtils.isEmpty(msg)) {
                Log.v(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            if (!TextUtils.isEmpty(msg)) {
                Log.i(tag, msg);
            }
        }
    }
}
