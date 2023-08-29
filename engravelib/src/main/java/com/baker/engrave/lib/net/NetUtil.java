package com.baker.engrave.lib.net;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.baker.engrave.lib.BakerVoiceEngraver;
import com.baker.engrave.lib.VoiceEngraveConstants;
import com.baker.engrave.lib.bean.ConfigBean;
import com.baker.engrave.lib.bean.Mould;
import com.baker.engrave.lib.bean.MouldList;
import com.baker.engrave.lib.bean.RecordTextData;
import com.baker.engrave.lib.bean.TokenResp;
import com.baker.engrave.lib.callback.BaseNetCallback;
import com.baker.engrave.lib.callback.innner.NetCallback;
import com.baker.engrave.lib.configuration.EngraverType;
import com.baker.engrave.lib.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Create by hsj55
 * 2020/3/5
 */
public class NetUtil {
    private static NetCallback netCallback;
    private static String mToken = "";
    private static final Gson gson = new Gson();
    private static String[] recordTextList;
    private static final int TYPE_TOKEN = 1;
    private static final int TYPE_CONTENT_TEXT = 2;
    private static final int TYPE_RECORD = 3;
    private static final int TYPE_MOULD = 4;

    public static void setNetCallback(NetCallback callback) {
        netCallback = callback;
    }

    public static String getClientId() {
        return BakerVoiceEngraver.getInstance().getClientId();
    }


    public static String getToken() {
        return mToken;
    }

    /**
     * 获取token
     * 同步执行
     */
    public static String requestToken() throws IOException {
        LogUtil.i("getToken()");
        BakerOkHttpClient client = BakerOkHttpClient.getInstance();
        Response response = client.execute(client.createGetRequest(String.format(NetConstants.URL_GET_TOKEN,
                BakerVoiceEngraver.getInstance().getClientSecret(),
                BakerVoiceEngraver.getInstance().getClientId())));
        ResponseBody body = response.body();
        String string = Objects.requireNonNull(body).string();
        TokenResp resp = new Gson().fromJson(string, TokenResp.class);
        mToken = resp.getAccess_token();
        return mToken;
    }


    /**
     * 获取配置信息
     */

    public static void getConfigData() {
        LogUtil.i("getConfigData()");
        ConcurrentHashMap<String, String> headers = getHeaders();
        BakerOkHttpClient.getInstance().enqueue(BakerOkHttpClient.getInstance().createPostRequest(NetConstants.URL_GET_CONFIG, null, headers), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                try {
                    String result = response.body().string();
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("code");
                    if ("20000".equals(resultCode)) {
                        String dataString = jsonObject.getString("data");
                        if (!TextUtils.isEmpty(dataString)) {
                            ConfigBean bean = gson.fromJson(dataString, ConfigBean.class);
                            if (netCallback != null) {
                                netCallback.callBackConfig(bean);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

  /*  *//**
     * 获取录音文本
     *//*
    public static void getTextList() {
        getConfigData();
        LogUtil.i("getTextList()");
        if (recordTextList != null) {
            if (netCallback != null) {
                netCallback.recordTextList(recordTextList);
            }
            return;
        }
        ConcurrentHashMap<String, String> headers = getHeaders();
        BakerOkHttpClient.getInstance().enqueue(BakerOkHttpClient.getInstance()
                .createPostRequest(NetConstants.URL_GET_TEXT_LIST, null, headers), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                onFault(TYPE_CONTENT_TEXT, VoiceEngraveConstants.ERROR_CODE_NET_WRONG, "get text list, " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    String result = response.body().string();
                    LogUtil.d("response=" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("code");
                    if ("20000".equals(resultCode)) {
                        String resultData = jsonObject.getString("data");
                        String resultString = resultData.replaceAll("\r", "").replaceAll("\n", "");
                        if (!TextUtils.isEmpty(resultString)) {
                            recordTextList = resultString.split("#");
                            if (netCallback != null) {
                                netCallback.recordTextList(recordTextList);
                            }
                        } else {
                            onFault(TYPE_CONTENT_TEXT, VoiceEngraveConstants.ERROR_CODE_DATA_NULL, "get text list, response data is null。");
                        }
                    } else {
                        onFault(TYPE_CONTENT_TEXT, VoiceEngraveConstants.ERROR_CODE_FROM_SERVER, "get text list, response code：" + resultCode + ", errorMessage: " + jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    onFault(TYPE_CONTENT_TEXT, VoiceEngraveConstants.ERROR_CODE_RESPONSE, "get text list, 解析response出错：" + e.getMessage());
                }
            }
        });
    }*/

    /**
     * 根据token申请创建模型的MID
     */
    public static void getVoiceMouldId(String queryId, String sessionId) {
        ConcurrentHashMap<String, Object> params = new ConcurrentHashMap<>();
        LogUtil.e("queryId=" + queryId);
        if (!TextUtils.isEmpty(queryId)) {
            params.put("queryId", queryId);
        }
        if (!TextUtils.isEmpty(sessionId)) {
            params.put("sessionId", sessionId);
        }
        params.put("modelType", BakerVoiceEngraver.getInstance().getType() == EngraverType.Common ? "1" : "2");
        ConcurrentHashMap<String, String> headers = getHeaders();
        BakerOkHttpClient.getInstance().enqueue(BakerOkHttpClient.getInstance().createPostRequest(
                NetConstants.URL_GET_MOULD_ID, params, headers), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                onFault(TYPE_RECORD, VoiceEngraveConstants.ERROR_CODE_NET_WRONG, "getVoiceMouldId, " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    String result = response.body().string();
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("code");
                    if ("20000".equals(resultCode)) {
                        String resultStatus = jsonObject.getString("data");
                        if (TextUtils.isEmpty(resultStatus)) {
                            onFault(TYPE_RECORD, VoiceEngraveConstants.ERROR_CODE_DATA_NULL, "getVoiceMouldId, data is null。");
                        } else {
                            JSONObject jsonData = new JSONObject(resultStatus);
                            String sessionId = jsonData.getString("sessionId");
                            LogUtil.i("获取sessionId成功：" + sessionId);
                            if (!TextUtils.isEmpty(sessionId)) {
                                if (netCallback != null) {
                                    netCallback.voiceSessionId(sessionId);
                                }
                            } else {
                                onFault(TYPE_RECORD, VoiceEngraveConstants.ERROR_CODE_DATA_NULL, "getVoiceMouldId, mouldId request failed, sessionId is null。");
                            }
                            String sentenceListString = jsonData.getString("sentenceList");
                            ArrayList<RecordTextData> dataList = gson.fromJson(sentenceListString, new TypeToken<ArrayList<RecordTextData>>() {}.getType());
                            if (netCallback != null) {
                                netCallback.callBackRecordList(dataList,sessionId);
                            }
                        }
                    } else {
                        onFault(TYPE_RECORD, VoiceEngraveConstants.ERROR_CODE_FROM_SERVER, "getVoiceMouldId, response code：" + resultCode + ", errorMessage: " + jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    onFault(TYPE_RECORD, VoiceEngraveConstants.ERROR_CODE_RESPONSE, "getVoiceMouldId, 解析response出错：" + e.getMessage());
                }
            }
        });
    }

    /**
     * 非正常结束录制
     */
    public static void recordInterrupt(String sessionId) {
        ConcurrentHashMap<String, Object> params = new ConcurrentHashMap<>();
        LogUtil.e("sessionId=" + sessionId);
        if (!TextUtils.isEmpty(sessionId)) {
            params.put("sessionId", sessionId);
        }
        ConcurrentHashMap<String, String> headers = getHeaders();
        BakerOkHttpClient.getInstance().enqueue(BakerOkHttpClient.getInstance().createPostRequest(
                NetConstants.URL_STOP_SESSION, params, headers), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                onFault(TYPE_RECORD, VoiceEngraveConstants.ERROR_CODE_NET_WRONG, "recordInterrupt, " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }


    /**
     * 完成录音
     */
    public static void finishRecords(String sessionId, String phone, String notifyUrl) {
        ConcurrentHashMap<String, Object> params = new ConcurrentHashMap<>();
        LogUtil.e("sessionId=" + sessionId + ",phone=" + phone);
        params.put("sessionId", sessionId);
        if (!TextUtils.isEmpty(phone)) {
            params.put("mobilePhone", phone);
        }
        if (!TextUtils.isEmpty(notifyUrl)) {
            params.put("notifyUrl", notifyUrl);
        }
        ConcurrentHashMap<String, String> headers = getHeaders();
        BakerOkHttpClient.getInstance().enqueue(BakerOkHttpClient.getInstance().createPostRequest(
                NetConstants.URL_FINISH_RECORDS, params, headers), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                onFault(TYPE_CONTENT_TEXT, VoiceEngraveConstants.ERROR_CODE_NET_WRONG, "finish records, " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    String result = response.body().string();
                    LogUtil.e("response=" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("code");
                    if ("20000".equals(resultCode)) {
                        boolean resultSuccess = jsonObject.getBoolean("success");
                        if (netCallback != null) {
                            netCallback.uploadRecordsResult(resultSuccess);
                        }
                    } else {
                        onFault(TYPE_CONTENT_TEXT, VoiceEngraveConstants.ERROR_CODE_FROM_SERVER,
                                "finish records, response code：" + resultCode + ", errorMessage: " + jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    onFault(TYPE_CONTENT_TEXT, VoiceEngraveConstants.ERROR_CODE_RESPONSE, "finish records, 解析response出错：" + e.getMessage());
                }
            }
        });
    }


    /**
     * 根据mouldId查询mould状态信息
     */
    public static void getMouldInfo(String mouldId) {
        ConcurrentHashMap<String, Object> params = new ConcurrentHashMap<>();
        LogUtil.e("mouldId=" + mouldId);
        if (!TextUtils.isEmpty(mouldId)) {
            params.put("modelId", mouldId);
        } else {
            onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_PARAM_NULL, "getMouldInfo mouldId is null.");
            return;
        }
        ConcurrentHashMap<String, String> headers = getHeaders();
        BakerOkHttpClient.getInstance().enqueue(BakerOkHttpClient.getInstance().createPostRequest(
                NetConstants.URL_GET_MOULD_INFO, params, headers), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_NET_WRONG, "getMouldInfo, " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    String result = response.body().string();
                    LogUtil.e("result=" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("code");
                    if ("20000".equals(resultCode)) {
                        String resultData = jsonObject.getString("data");
                        if (!TextUtils.isEmpty(resultData)) {
                            Mould mould = gson.fromJson(resultData, Mould.class);
                            if (netCallback != null) {
                                netCallback.mouldInfo(mould);
                            }
                        } else {
                            onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_DATA_NULL, "getMouldInfo, mould data is null。");
                        }
                    } else {
                        onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_FROM_SERVER, "getMouldInfo, response code：" + resultCode + ", errorMessage: " + jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_RESPONSE, "getMouldInfo, 解析response出错：" + e.getMessage());
                }
            }
        });
    }


    /**
     * 根据queryId查询mould状态信息
     */
    public static void getMouldList(int page, int limit, String queryId) {
        ConcurrentHashMap<String, Object> params = new ConcurrentHashMap<>();
        LogUtil.e("page=" + page + ", limit=" + limit + ", queryId=" + queryId);
        if (page < 1) {
            onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_PARAM_NULL, "getMouldList, the value of page is illegal.");
            return;
        } else {
            params.put("page", page);
        }
        if (TextUtils.isEmpty(queryId)) {
            onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_PARAM_NULL, "getMouldList, queryId is null.");
            return;
        } else {
            params.put("queryId", queryId);
        }
        if (limit < 1) {
            onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_PARAM_NULL, "getMouldList, the value of limit is illegal.");
            return;
        } else {
            params.put("limit", limit);
        }
        ConcurrentHashMap<String, String> headers = getHeaders();
        BakerOkHttpClient.getInstance().enqueue(BakerOkHttpClient.getInstance().createPostRequest(NetConstants.URL_GET_MOULD_LIST_INFO, params, headers), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_NET_WRONG, "getMouldList, " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    String result = response.body().string();
                    LogUtil.e("result = " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("code");
                    if ("20000".equals(resultCode)) {
                        String resultData = jsonObject.getString("data");
                        if (!TextUtils.isEmpty(resultData)) {
                            MouldList list = gson.fromJson(resultData, MouldList.class);
                            if (list != null) {
                                if (netCallback != null) {
                                    netCallback.mouldList(list.getList());
                                }
                            } else {
                                onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_DATA_NULL, "getMouldList, list is null。");
                            }
                        } else {
                            onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_DATA_NULL, "getMouldList, data is null。");
                        }
                    } else {
                        onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_FROM_SERVER,
                                "getMouldList, response code：" + resultCode + ", errorMessage: " + jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    onFault(TYPE_MOULD, VoiceEngraveConstants.ERROR_CODE_RESPONSE, "getMouldList, 解析response出错：" + e.getMessage());
                }
            }
        });
    }


    private static void onFault(int type, int errorCode, String errorMessage) {
        if (netCallback != null) {
            if (type == TYPE_TOKEN) {
                netCallback.netTokenError(errorCode, errorMessage);
            } else if (type == TYPE_RECORD) {
                netCallback.netRecordError(errorCode, errorMessage);
            } else if (type == TYPE_CONTENT_TEXT) {
                netCallback.netContentTextError(errorCode, errorMessage);
            } else if (type == TYPE_MOULD) {
                netCallback.onMouldError(errorCode, errorMessage);
            }
        }
    }

    public static ConcurrentHashMap<String, String> getHeaders() {
        LogUtil.i("getHeaders()");
        String nounce, timestamp, signature;
        ConcurrentHashMap<String, String> headers = new ConcurrentHashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("clientId", BakerVoiceEngraver.getInstance().getClientId());
        headers.put("token", mToken);
        headers.put("modelType", BakerVoiceEngraver.getInstance().getType() == EngraverType.Common ? "1" : "2");
        nounce = String.valueOf(NetUtil.random6num());
        timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        Map<String, String> params = new HashMap<>();
        params.put("token", mToken);
        params.put("clientId", BakerVoiceEngraver.getInstance().getClientId());
        params.put("nounce", nounce);
        params.put("timestamp", timestamp);
        signature = genSignature(NetConstants.VERSION, nounce, params);

        headers.put("nounce", nounce);
        headers.put("timestamp", timestamp);
        headers.put("signature", signature);
        return headers;
    }

    /**
     * 生成签名信息
     *
     * @param version 产品版本号
     * @param params  接口请求参数名和参数值map，不包括signature参数名
     * @return
     */
    public static String genSignature(String version, String nounce, Map<String, String> params) {
        // 1. 参数名按照ASCII码表升序排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 2. 按照排序拼接参数名与参数值
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        // 3. 将version拼接到最后
        sb.append(version);

        // 4. MD5是128位长度的摘要算法，转换为十六进制之后长度为32字符(最后进行一个简单的混淆，把首次出现的 s 换成 b),然后加上nounce再进行一次MD5
        return md5(md5(sb.toString()).toLowerCase().replaceFirst("s", "b").concat(nounce)).toLowerCase();
    }

    public static int random6num() {
        int intFlag = (int) (Math.random() * 1000000);
        String flag = String.valueOf(intFlag);
        if (flag.length() != 6 || flag.charAt(0) != '9') {
            intFlag = intFlag + 100000;
        }
        return intFlag;
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
