package com.baker.sdk.basecomponent.util;

import android.content.Context;
import android.util.Log;
import com.baker.sdk.basecomponent.BakerBaseConstants;
import java.lang.reflect.Method;

public class LogUtils {

    private static volatile LogUtils instance;


    private LogUtils() {
    }

    public static LogUtils getInstance() {
        if (null == instance) {
            synchronized (LogUtils.class) {
                if (null == instance) {
                    instance = new LogUtils();
                }
            }
        }
        return instance;
    }

    private final String TAG = "sdk-tts";

    private Object logConstants;
    private Class<?> mLogUtil;


    public void init(Context mContext) {
        try {
            //LogConstants
            Class<?> mLogConstants = Class.forName("com.baker.log.util.LogConstants");
            Method logConstantsInstance = mLogConstants.getDeclaredMethod("getInstance");
            logConstants = logConstantsInstance.invoke(null, (Object) null);

            //LogUtil
            mLogUtil = Class.forName("com.baker.log.util.LogUtil");

            //ExceptionHandlerUtil
            Class<?> mExceptionClazz = Class.forName("com.baker.log.util.ExceptionHandlerUtil");
            Method exceptionHandlerUtilGetInstance = mExceptionClazz.getDeclaredMethod("getInstance");
            Object mExceptionHandlerUtil = exceptionHandlerUtilGetInstance.invoke(null, (Object[]) null);

            Method setUploadConfig = mLogConstants.getMethod("setUploadConfig", String.class, String.class, String.class);
            setUploadConfig.invoke(logConstants, "bbxnr-sdk-android", "3.0.0", "离在线TTS");
            Method setLogConfig = mLogConstants.getMethod("setLogConfig", String.class, String.class, boolean.class, boolean.class);
            setLogConfig.invoke(logConstants, "https://openapitest.data-baker.com/logapp/log/uploadLog", "17sMTZ4tzrGwf11NloSWCy1HUip9Xhmgu2O17RILa0ceIkP1qxVvFT1bBdJKQo", false, false);

            Method openLogUtilStream = mLogUtil.getDeclaredMethod("openLogUtilStream", Context.class);
            openLogUtilStream.invoke(null, mContext);

            Method initMethod = mExceptionClazz.getDeclaredMethod("init", Context.class);
            initMethod.invoke(mExceptionHandlerUtil, mContext);

        } catch (Exception e) {
            // e.printStackTrace();
        }
    }


    public void d(String message) {
        if (!BakerBaseConstants.isIsDebug()) return;
        if (logConstants != null) {
            try {
                Method mMethod = mLogUtil.getDeclaredMethod("d", String.class, String.class);
                mMethod.invoke(null, TAG, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, message);
        }
    }

    public void e(String message) {
        if (!BakerBaseConstants.isIsDebug()) return;
        if (logConstants != null) {
            try {
                Method mMethod = mLogUtil.getDeclaredMethod("e", String.class, String.class);
                mMethod.invoke(null, TAG, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, message);
        }
    }

    public void error(String message) {
        if (!BakerBaseConstants.isIsDebug()) return;
        if (logConstants != null) {
            try {
                Method mMethod = mLogUtil.getDeclaredMethod("error", String.class, String.class);
                mMethod.invoke(null, TAG, "msg:" + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "msg:" + message);
        }
    }
}
