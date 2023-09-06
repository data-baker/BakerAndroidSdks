package com.baker.engrave.lib.util;

import android.content.Context;
import androidx.annotation.NonNull;
import com.baker.engrave.lib.bean.BaseInfo;
import com.baker.engrave.lib.bean.RequestJsonBody;
import com.baker.engrave.lib.net.BakerOkHttpClient;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BakerLogUpload {
    private volatile static BakerLogUpload instance = null;

    private BakerLogUpload() {

    }

    public static BakerLogUpload getInstance() {
        if (null == instance) {
            synchronized (BakerLogUpload.class) {
                if (null == instance) {
                    instance = new BakerLogUpload();
                }
            }
        }
        return instance;
    }


    private final BaseInfo baseInfo = new BaseInfo(DeviceIdUtil.getManufacturer() + "-" + DeviceIdUtil.getModel(), "1.1.0", "声音复刻", Locale.getDefault().getLanguage(), DeviceIdUtil.getSDKVersionName() + "-" + DeviceIdUtil.getSDKVersionCode());

    private final RequestJsonBody requestJsonBody = new RequestJsonBody(baseInfo, "info", "bbyy-sdk-android");
    private Context mContext;
    private final MediaType contentType = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();


    private String getAuthorizationString() {
        TreeMap<String, String> originParams = new TreeMap<>();
        originParams.put("level", requestJsonBody.getLevel());
        originParams.put("userid", requestJsonBody.getUserid());
        originParams.put("businessType", requestJsonBody.getBusinessType());
        originParams.put("time", requestJsonBody.getTime());
        originParams.put("systemVersion", baseInfo.getSystemVersion());
        originParams.put("appVersion", baseInfo.getAppVersion());
        originParams.put("appName", baseInfo.getAppName());
        originParams.put("language", baseInfo.getLanguage());
        originParams.put("appSystemVersion", baseInfo.getAppSystemVersion());
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : originParams.entrySet()) {
            builder.append(entry.getKey());
            builder.append("_");
            builder.append(entry.getValue());
            builder.append("_");
        }
        String authorizationString = builder.toString();
        return HMACSHA256Utils
                .sha256_HMAC(authorizationString.substring(0, authorizationString.length() - 1),
                        "17sMTZ4tzrGwf11NloSWCyEHUip9Xhmgu2O37RILa0ceIkP1qxVvFTAbBdJKQo");

    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public void d(String contentString) {
        d(Arrays.asList(contentString));
    }

    public void d(List<String> logs) {
        upload(logs, "info");
    }


    public void e(String contentString) {
        e(Arrays.asList(contentString));
    }

    public void e(List<String> logs) {
        upload(logs, "error");
    }


    private void upload(List<String> logs, String level) {
        if (mContext == null) return;
        requestJsonBody.setContentList(logs);
        requestJsonBody.setTime(String.valueOf(System.currentTimeMillis()));
        requestJsonBody.setUserid(DeviceIdUtil.getDeviceId(mContext));
        requestJsonBody.setLevel(level);
        String paramJson = GsonUtils.toJson(requestJsonBody);

        RequestBody body = RequestBody.create(contentType, paramJson);

        String url = "https://openapitest.data-baker.com/logapp/log/uploadLog";
        Request request = new Request.Builder().addHeader("Authorization", getAuthorizationString()).url(url).post(body).build();

        BakerOkHttpClient.getInstance().getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }


}
