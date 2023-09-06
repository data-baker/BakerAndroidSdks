package com.baker.engrave.lib.util;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceUtil {


    /**
     * 获取SharedPreferences实例对象
     *
     * @param fileName
     */
    private volatile static PreferenceUtil instance = null;

    private PreferenceUtil() {

    }

    public static PreferenceUtil getInstance() {
        if (null == instance) {
            synchronized (PreferenceUtil.class) {
                if (null == instance) {
                    instance = new PreferenceUtil();
                }
            }
        }
        return instance;
    }

    private final String CONFIG = "baker_crash";
    private Context mContext;

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    private SharedPreferences getSharedPreference(String fileName) {
        return mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * 保存一个String类型的值！
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putString(key, value).commit();
    }

    /**
     * 获取String的value
     */
    public String getString(String key, String defValue) {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getString(key, defValue);
    }

}
