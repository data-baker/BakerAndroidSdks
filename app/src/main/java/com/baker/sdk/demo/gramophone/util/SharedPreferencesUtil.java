package com.baker.sdk.demo.gramophone.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.baker.sdk.demo.base.Constants;

import java.util.UUID;

public class SharedPreferencesUtil {

    /**
     * 将mould存在手机本地，方便体验自己的声音模型。
     */
    public static String getQueryId(Context context) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(Constants.SP_TABLE_NAME, Context.MODE_PRIVATE);
        String queryId = mSharedPreferences.getString(Constants.GRAMOPHONE_QUERY_ID, null);
        if (TextUtils.isEmpty(queryId)) {
            queryId = UUID.randomUUID().toString();
            mSharedPreferences.edit().putString(Constants.GRAMOPHONE_QUERY_ID, queryId).apply();
            return queryId;
        } else {
            return queryId;
        }
    }
}
