package com.baker.engrave.lib.util;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

/**
 * Create by hsj55
 * 2020/3/6
 */
public class BaseUtil {
    public static boolean hasPermission(Context context, String... permissions) {
        for (String permission : permissions) {
            int granted = ContextCompat.checkSelfPermission(context, permission);
            if (granted != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
