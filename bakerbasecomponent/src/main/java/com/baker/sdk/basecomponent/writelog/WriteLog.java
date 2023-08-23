package com.baker.sdk.basecomponent.writelog;

import android.content.Context;

import com.baker.sdk.basecomponent.BakerBaseConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hsj55
 * 2020/9/15
 */
public class WriteLog {

    private static FileOutputStream fos2;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final StringBuilder stringBuilder = new StringBuilder();

    public static void openStream(Context context) {
        if (!BakerBaseConstants.isIsDebug() || context == null) {
            return;
        }
        try {
            fos2 = new FileOutputStream(context.getFilesDir().getAbsolutePath() + File.separator + "logInfo.txt", true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeLogs(String logString) {
        if (!BakerBaseConstants.isIsDebug()) {
            return;
        }
        if (stringBuilder == null) return;
        if (fos2 == null) return;
        try {
            stringBuilder.setLength(0);
            stringBuilder.append(simpleDateFormat.format(new Date(System.currentTimeMillis())));
            stringBuilder.append("    ");
            stringBuilder.append(logString);
            fos2.write(stringBuilder.toString().getBytes());
            fos2.write("\r\n".getBytes());
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    public static void closeStream() {
        if (!BakerBaseConstants.isIsDebug()) {
            return;
        }
        // 释放资源
        try {
            fos2.close();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }
}
