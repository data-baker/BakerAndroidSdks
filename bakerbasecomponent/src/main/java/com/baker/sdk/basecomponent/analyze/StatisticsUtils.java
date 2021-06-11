package com.baker.sdk.basecomponent.analyze;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.baker.sdk.basecomponent.BakerBaseConstants;
import com.baker.sdk.basecomponent.bean.BakerBaseComponent;
import com.baker.sdk.basecomponent.bean.UploadBean;
import com.baker.sdk.basecomponent.util.GsonConverter;
import com.baker.sdk.basecomponent.util.HttpUtil;
import com.baker.sdk.basecomponent.util.ThreadPoolUtil;
import com.baker.sdk.basecomponent.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.baker.sdk.basecomponent.BakerBaseConstants.SP_STATISTICS_FLAG;
import static com.baker.sdk.basecomponent.BakerBaseConstants.SP_STATISTICS_FLAG_KEY_FIRST;
import static com.baker.sdk.basecomponent.BakerBaseConstants.SP_SWITCH_KEY;

/**
 * @author hsj55
 * 2020/9/15
 */
public class StatisticsUtils {
    private static final String URL_POST_SUBMIT = "https://sdkinfo.data-baker.com:8677/sdk-submit/sdk-info/sign-upload";
    private static SharedPreferences mSharedPreferences;
    private static String time;
    private static String SP_STATISTICS_FLAG_KEY_EVERY_DAY = "sp_statistics_flag_key_every_day";

    @SuppressLint({"CommitPrefEdits", "SimpleDateFormat"})
    public static void statistics(Context context, String myTag) {
        if (context != null) {
            if (mSharedPreferences == null) {
                mSharedPreferences = context.getApplicationContext().getSharedPreferences((myTag + SP_STATISTICS_FLAG), Context.MODE_PRIVATE);
            }
            String code = mSharedPreferences.getString((myTag + SP_SWITCH_KEY), "");
            if ("40005".equals(code)) {
                return;
            }
            String first = mSharedPreferences.getString((myTag + SP_STATISTICS_FLAG_KEY_FIRST), "");
            if (TextUtils.isEmpty(first)) {
                //TODO 首次安装上传服务器记录一次
                commit(1, myTag);
            } else {
                String day = mSharedPreferences.getString((myTag + SP_STATISTICS_FLAG_KEY_EVERY_DAY), "");
                time = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(System.currentTimeMillis()));
                if (!(("" + day).equals(time))) {
                    //当前日期跟存储的日期不同，则是当天第一次使用，然后记录标示

                    //TODO 每天上传服务器记录一次
                    commit(2, myTag);
                }
            }
        }
    }

    /**
     * 统计信息上传至服务器
     *
     * @param type 1-->首次安装上传一次；2-->每天首次使用上传一次
     */
    private static void commit(final int type, final String myTag) {
        //产生用户唯一标识，存到SDK内、SDK类别、版本号，bundleID（包名)，client_id，网络地理位置信息（区级）
        ThreadPoolUtil.execute(new Runnable() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void run() {
                Map<String, Object> map = Util.getInfo(myTag);
                map.put("submitType", type);

                String response = HttpUtil.post(URL_POST_SUBMIT, GsonConverter.toJson(map));
                if (!TextUtils.isEmpty(response)) {
                    UploadBean bean = GsonConverter.fromJson(response, UploadBean.class);
                    if (bean.isSuccess()) {
                        if ("40005".equals(bean.getCode())) {
                            mSharedPreferences.edit().putString((myTag + SP_SWITCH_KEY), bean.getCode()).apply();
                            return;
                        }
                        if (type == 1) {
                            BakerBaseComponent component = BakerBaseConstants.getBakerBaseComponentByTag(myTag);
                            if (component != null) {
                                mSharedPreferences.edit().putString((myTag + SP_STATISTICS_FLAG_KEY_FIRST), component.getUuid()).apply();
                            }

                            String day = mSharedPreferences.getString((myTag + SP_STATISTICS_FLAG_KEY_EVERY_DAY), "");
                            time = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(System.currentTimeMillis()));
                            if (!(("" + day).equals(time))) {
                                //当前日期跟存储的日期不同，则是当天第一次使用，然后记录标示

                                //TODO 每天上传服务器记录一次
                                commit(2, myTag);
                            }
                        } else if (type == 2) {
                            mSharedPreferences.edit().putString((myTag + SP_STATISTICS_FLAG_KEY_EVERY_DAY), time).apply();
                        }
                        Log.d("StatisticsUtils", "commit success !");
                    }
                }
            }
        });
    }
}
