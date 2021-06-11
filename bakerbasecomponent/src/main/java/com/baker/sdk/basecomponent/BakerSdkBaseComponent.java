package com.baker.sdk.basecomponent;

import android.content.Context;
import android.content.SharedPreferences;

import com.baker.sdk.basecomponent.analyze.StatisticsUtils;
import com.baker.sdk.basecomponent.bean.BakerBaseComponent;
import com.baker.sdk.basecomponent.error.BakerCrashUtils;

import java.util.UUID;

import static com.baker.sdk.basecomponent.BakerBaseConstants.SP_STATISTICS_FLAG;
import static com.baker.sdk.basecomponent.BakerBaseConstants.SP_STATISTICS_FLAG_KEY_FIRST;
import static com.baker.sdk.basecomponent.BakerBaseConstants.components;

/**
 * @author hsj55
 * 2020/9/21
 */
public class BakerSdkBaseComponent {

    private BakerSdkBaseComponent() {
    }

    private static class InnerClass {
        private final static BakerSdkBaseComponent INSTANCE = new BakerSdkBaseComponent();
    }

    public static BakerSdkBaseComponent getInstance() {
        return InnerClass.INSTANCE;
    }

    /**
     * 计划收集sdk bug信息等（暂时废除未用）
     * @param context
     * @param clientId
     * @param packageName
     * @param versionName
     * @param tag
     */
    public void init(Context context, String clientId, String packageName, String versionName, String tag) {
        SharedPreferences mSharedPreferences = context.getApplicationContext().getSharedPreferences((tag + SP_STATISTICS_FLAG), Context.MODE_PRIVATE);
        String uuid = mSharedPreferences.getString((tag + SP_STATISTICS_FLAG_KEY_FIRST), UUID.randomUUID().toString());

        boolean isExist = false;
        for (BakerBaseComponent c : components) {
            if (tag.equals(c.getTag())){
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            components.add(new BakerBaseComponent(tag, clientId, packageName, versionName, uuid));
            StatisticsUtils.statistics(context, tag);
            BakerCrashUtils.getInstance().init(context, tag);
//            WriteLog.openStream(context);
        }
    }
}
