package com.databaker.synthesizer.util;

import android.text.TextUtils;

import com.baker.sdk.basecomponent.util.HLogger;
import com.baker.sdk.basecomponent.util.LogUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by hsj55
 * 2019/11/29
 */
public class Util {
    public static List<String> splitText(String text) {
        if (!TextUtils.isEmpty(text) && !"null".equals(text)) {
            List<String> resultList = new ArrayList<>();
            split(resultList, text, 200);
            LogUtils.getInstance().e("resultList.size()==" + resultList.size());
            return resultList;
        }
        return null;
    }

    public static int random6num() {
        int intFlag = (int) (Math.random() * 1000000);

        String flag = String.valueOf(intFlag);
        if (flag.length() != 6 || flag.charAt(0) != '9') {
            intFlag = intFlag + 100000;
        }
        return intFlag;
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

    private static void split(List<String> list, String text, int offSet) {
        if (text.length() > offSet) {
            String t1 = text.substring(0, offSet);
            HLogger.d("t1==" + t1);
            Pattern p = Pattern.compile(GREP_SPLIT_PUNCTUATION_REGEX);
            Matcher m = p.matcher(t1);
            if (m.find()) {
                int i = m.end();
                HLogger.d("i==" + i);
                if (list != null) {
                    list.add(text.substring(0, i));
                }
                split(list, text.substring(i), offSet);
            } else {
                list.add(t1);
                split(list, text.substring(offSet), offSet);
            }
        } else if (list != null) {
            list.add(text);
        }
    }

    private static final String GREP_SPLIT_PUNCTUATION_REGEX = ".*(，|。。。。。。|。|！|；|？|,|;|\\?|、)";

}
