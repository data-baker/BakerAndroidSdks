package com.baker.sdk.basecomponent.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.baker.sdk.basecomponent.BakerBaseConstants;
import com.baker.sdk.basecomponent.bean.BakerBaseComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hsj55
 * 2020/9/15
 */
public class Util {
    public static boolean checkConnectNetwork(Context context) throws Exception {
        ConnectivityManager cm = (ConnectivityManager)
                context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
    }

    public static Map<String, Object> getInfo(String myTag) {
        BakerBaseComponent c = BakerBaseConstants.getBakerBaseComponentByTag(myTag);
        if (c != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("sdkUuid", c.getUuid());
            map.put("sdkName", myTag);
            map.put("packageName", c.getPackageName());//包名
            map.put("sdkType", "Android");//sdk类别
            map.put("sdkVersion", c.getVersionName());//sdk版本号
            map.put("sdkClientId", c.getClientId());//clientId
            return map;
        }
        return null;
    }
}
