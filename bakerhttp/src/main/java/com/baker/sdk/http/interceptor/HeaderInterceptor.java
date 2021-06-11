package com.baker.sdk.http.interceptor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author hsj55
 * 2020/9/22
 */
public class HeaderInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String nounce, timestamp, signature;

        nounce = String.valueOf(random6num());
        timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        Map<String, String> params = new HashMap<>();
        params.put("nounce", nounce);
        params.put("timestamp", timestamp);
        signature = genSignature("", nounce, params);

        Request.Builder requestBuilder = originalRequest.newBuilder()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                //所有header不能传null
                .addHeader("nounce", nounce)
                .addHeader("timestamp", timestamp)
                .addHeader("signature", signature)
                .method(originalRequest.method(), originalRequest.body());

        Request request = requestBuilder.build();
        return chain.proceed(request);
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

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static int random6num() {
        int intFlag = (int) (Math.random() * 1000000);

        String flag = String.valueOf(intFlag);
        if (flag.length() != 6 || !flag.substring(0, 1).equals("9")) {
            intFlag = intFlag + 100000;
        }
        return intFlag;
    }
}
