package com.baker.sdk.http;

import com.baker.sdk.basecomponent.util.GsonConverter;

import java.util.Iterator;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author hsj55
 * 2020/9/17
 */
public class CommonOkHttpRequest {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * json数据提交
     *
     * @param url
     * @param params json
     * @return
     */
    public static Request createRequestBodyPostRequest(String url, String params) {
        // 创建request
        return new Request.Builder().post(RequestBody.create(JSON, params)).url(url).build();
    }

    /**
     * json数据提交
     *
     * @param url
     * @param params 实体类
     * @return
     */
    public static Request createRequestBodyPostRequest(String url, Object params) {
        // 创建request
        return new Request.Builder().post(RequestBody.create(JSON, GsonConverter.toJson(params))).url(url).build();
    }

    /**
     * json数据提交
     *
     * @param url
     * @param params 键值对
     * @return
     */
    public static Request createRequestBodyPostRequest(String url, Map<String, Object> params) {
        // 创建request
        return new Request.Builder().post(RequestBody.create(JSON, GsonConverter.toJson(params))).url(url).build();
    }

    /**
     * 表单数据提交
     *
     * @param url
     * @param params
     * @return
     */
    public static Request createFormBodyPostRequest(String url, Map<String, String> params) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        // 添加参数
        if (params != null && params.size() > 0) {
            Iterator<String> mIterator = params.keySet().iterator();
            while (mIterator.hasNext()) {
                String s = mIterator.next();
                bodyBuilder.add(s, params.get(s));
            }
        }
        // 创建请求体
        FormBody formBody = bodyBuilder.build();
        // 创建request
        return new Request.Builder().post(formBody).url(url).build();
    }

    public static Request createGetRequest(String url, Map<String, String> params) {
        // 如果params不为空，构建新的url
        if (params != null && params.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder(url).append("?");
            Iterator<String> mIterator = params.keySet().iterator();
            while (mIterator.hasNext()) {
                String s = mIterator.next();
                stringBuilder.append(s)
                        .append("=")
                        .append(params.get(s))
                        .append("&");
            }
            // 去除最后的&
            url = stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return new Request.Builder().url(url).get().build();
    }
}
