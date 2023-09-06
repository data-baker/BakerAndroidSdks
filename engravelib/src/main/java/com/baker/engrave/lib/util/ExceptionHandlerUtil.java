package com.baker.engrave.lib.util;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 抓错误日志
 */
public class ExceptionHandlerUtil implements UncaughtExceptionHandler {

    private volatile static ExceptionHandlerUtil instance = null;

    private ExceptionHandlerUtil() {

    }

    public static ExceptionHandlerUtil getInstance() {
        if (null == instance) {
            synchronized (ExceptionHandlerUtil.class) {
                if (null == instance) {
                    instance = new ExceptionHandlerUtil();
                }
            }
        }
        return instance;
    }


    public void init(Context mContext) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandlerUtil());
        PreferenceUtil.getInstance().setContext(mContext);
        String baker_crash_error = PreferenceUtil.getInstance().getString("baker_crash_error", "");
        Log.e("TAG--->","baker_crash_error:"+baker_crash_error);
        if (!TextUtils.isEmpty(baker_crash_error)) {
            BakerLogUpload.getInstance().e(baker_crash_error);
            PreferenceUtil.getInstance().putString("baker_crash_error", "");
        }
    }


    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable e) {
        handException(e);
    }

    /**
     * 处理错误信息
     *
     * @param e
     */
    private void handException(Throwable e) {
        e.printStackTrace();
        saveCrash(e);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Process.killProcess(Process.myPid());
        }
    }

    private void saveCrash(final Throwable e) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String crash_error = writer.toString();
        if (crash_error.contains("com.baker.engrave.lib")) {
            PreferenceUtil.getInstance().putString("baker_crash_error", writer.toString());
        }
    }

}

