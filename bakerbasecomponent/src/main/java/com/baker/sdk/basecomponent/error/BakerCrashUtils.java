package com.baker.sdk.basecomponent.error;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baker.sdk.basecomponent.BakerBaseConstants;
import com.baker.sdk.basecomponent.bean.BakerBaseComponent;
import com.baker.sdk.basecomponent.bean.UploadBean;
import com.baker.sdk.basecomponent.util.GsonConverter;
import com.baker.sdk.basecomponent.util.HttpUtil;
import com.baker.sdk.basecomponent.util.ThreadPoolUtil;
import com.baker.sdk.basecomponent.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static com.baker.sdk.basecomponent.BakerBaseConstants.SP_STATISTICS_FLAG;
import static com.baker.sdk.basecomponent.BakerBaseConstants.SP_SWITCH_KEY;
import static com.baker.sdk.basecomponent.BakerBaseConstants.components;

/**
 * @author hsj55
 * 2020/9/15
 */
public class BakerCrashUtils implements Thread.UncaughtExceptionHandler {
    private final String URL_POST_SUBMIT = "https://sdkinfo.data-baker.com:8677/sdk-submit/sdk-info/sign-upload";
    private static final String CRASH_TEMP_FILE_NAME = "crash_socket_tts_record.txt";
    private final Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    private BakerCrashUtils() {
        //系统默认处理类
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该类为系统默认处理类
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private static class InnerClass {
        private static final BakerCrashUtils INSTANCE = new BakerCrashUtils();
    }

    public static BakerCrashUtils getInstance() {
        return InnerClass.INSTANCE;
    }

    /**
     * 初始化
     */
    public void init(Context context, String tag) {
        if (context != null) {
            this.mContext = context.getApplicationContext();
        }

        //每次初始化检测上次如果有崩溃信息，就上传服务器，然后删除记录的信息
        uploadErrorInfo(tag);
    }

    private void uploadErrorInfo(String tag) {
        String code = mContext.getApplicationContext().getSharedPreferences((tag + SP_STATISTICS_FLAG), Context.MODE_PRIVATE)
                .getString((tag + SP_SWITCH_KEY), "");
        if ("40005".equals(code)) {
            return;
        }

        if (mContext != null) {
            File file = new File(mContext.getFilesDir().getAbsolutePath() + "/" + (tag + CRASH_TEMP_FILE_NAME));
            if (file.exists()) {
                try {
                    InputStream instream = new FileInputStream(file);
                    String content = "";
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                    if (!TextUtils.isEmpty(content)) {
                        net(content, tag);
                        file.delete();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public void net(final String crashContent, final String tag) {
        if (TextUtils.isEmpty(crashContent)) return;

        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map = Util.getInfo(tag);
                map.put("errorInfo", crashContent);
                map.put("submitType", 3);

                String response = HttpUtil.post(URL_POST_SUBMIT, GsonConverter.toJson(map));
                if (!TextUtils.isEmpty(response)) {
                    UploadBean bean = GsonConverter.fromJson(response, UploadBean.class);
                    if (bean.isSuccess()) {
                        cleanErrorFile(tag);

                        if ("40005".equals(bean.getCode())) {
                            mContext.getApplicationContext().getSharedPreferences((tag + SP_STATISTICS_FLAG), Context.MODE_PRIVATE)
                                    .edit().putString((tag + SP_SWITCH_KEY), bean.getCode()).apply();
                            return;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        for (BakerBaseComponent c : components) {
            if (Log.getStackTraceString(e).contains(c.getPackageName())) {
                //TODO 是SDK导致的错误，发送至服务器
                saveError(Log.getStackTraceString(e), c.getTag());
                break;
            }
        }

        //不拦截异常，继续向下一层抛出
        mDefaultHandler.uncaughtException(t, e);
    }

    private void saveError(String errorStr, String tag) {
        String code = mContext.getApplicationContext().getSharedPreferences((tag + SP_STATISTICS_FLAG), Context.MODE_PRIVATE)
                .getString((tag + SP_SWITCH_KEY), "");
        if ("40005".equals(code)) {
            return;
        }
        if (mContext != null) {
            try {
                File file = new File(mContext.getFilesDir().getAbsolutePath() + "/" + (tag + CRASH_TEMP_FILE_NAME));
                if (!file.getParentFile().exists()) {
                    //创建多层目录
                    file.getParentFile().mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(errorStr.getBytes());
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    private void cleanErrorFile(String tag) {
        if (mContext != null) {
            try {
                File file = new File(mContext.getFilesDir().getAbsolutePath() + "/" + (tag + CRASH_TEMP_FILE_NAME));
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
            }
        }
    }
}
