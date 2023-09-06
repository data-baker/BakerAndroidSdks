package com.baker.engrave.lib.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;

public class DeviceIdUtil {
//    static String TAG = "DeviceIdUtil";

    /**
     * 获得设备硬件标识
     *
     * @param context 上下文
     * @return 设备硬件标识
     */
    public static String getDeviceId(Context context) {
        StringBuilder sbDeviceId = new StringBuilder();

        String androidId = getAndroidId(context);
//        Log.e(TAG, "androidId = " + androidId);

        String board = Build.BOARD;
//        Log.e(TAG, "Build.BOARD = " + board);        //主板：venus

        String brand = Build.BRAND;
//        Log.e(TAG, "Build.BRAND = " + Build.BRAND);        //系统定制商，品牌 ：Xiaomi

        String device = Build.DEVICE;
//        Log.e(TAG, "Build.DEVICE = " + Build.DEVICE);      //设备参数：venus

        String hardware = Build.HARDWARE;
//        Log.e(TAG, "Build.HARDWARE = " + Build.HARDWARE);  //硬件名称：qcom

        String manufacturer = Build.MANUFACTURER;
//        Log.e(TAG, "Build.MANUFACTURER = " + Build.MANUFACTURER);   //硬件制造商：Xiaomi

        String model = Build.MODEL;
//        Log.e(TAG, "Build.MODEL = " + Build.MODEL);        //版本，最终用户可见的名称：M2011K2C

        String product = Build.PRODUCT;
//        Log.e(TAG, "Build.PRODUCT = " + Build.PRODUCT);    //整个产品的名称：venus

        //追加androidid
        if (androidId != null && androidId.length() > 0) {
            sbDeviceId.append(androidId);
            sbDeviceId.append("|");
        }

        //board
        if (board != null && board.length() > 0) {
            sbDeviceId.append(board);
            sbDeviceId.append("|");
        }

        //追加brand
        if (brand != null && brand.length() > 0) {
            sbDeviceId.append(brand);
            sbDeviceId.append("|");
        }

        //追加device
        if (device != null && device.length() > 0) {
            sbDeviceId.append(device);
            sbDeviceId.append("|");
        }

        //追加hardware
        if (hardware != null && hardware.length() > 0) {
            sbDeviceId.append(hardware);
            sbDeviceId.append("|");
        }

        //追加manufacturer
        if (manufacturer != null && manufacturer.length() > 0) {
            sbDeviceId.append(manufacturer);
            sbDeviceId.append("|");
        }

        //追加model
        if (model != null && model.length() > 0) {
            sbDeviceId.append(model);
            sbDeviceId.append("|");
        }

        //追加硬件uuid
        if (product != null && product.length() > 0) {
            sbDeviceId.append(product);
        }

//        Log.e(TAG, "info = " + sbDeviceId.toString());

        //生成SHA1，统一DeviceId长度
        if (sbDeviceId.length() > 0) {
            try {
                byte[] hash = getHashByString(sbDeviceId.toString());
                String sha1 = bytesToHex(hash);
                if (sha1 != null && sha1.length() > 0) {
                    //返回最终的DeviceId
                    return sha1;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        //如果以上硬件标识数据均无法获得，
        //则DeviceId默认使用系统随机数，这样保证DeviceId不为空
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获得设备的AndroidId
     *
     * @param context 上下文
     * @return 设备的AndroidId
     */
    private static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "unknown";
    }

    /**
     * 取SHA1
     *
     * @param data 数据
     * @return 对应的hash值
     */
    private static byte[] getHashByString(String data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.reset();
            messageDigest.update(data.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (Exception e) {
            return "".getBytes();
        }
    }

    /**
     * 转16进制字符串
     *
     * @param data 数据
     * @return 16进制字符串
     */
    private static String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String stmp;
        for (int n = 0; n < data.length; n++) {
            stmp = (Integer.toHexString(data[n] & 0xFF));
            if (stmp.length() == 1)
                sb.append("0");
            sb.append(stmp);
        }
        return sb.toString().toUpperCase(Locale.CHINA);
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }

    public static String getSDKVersionName() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static int getSDKVersionCode() {
        return android.os.Build.VERSION.SDK_INT;
    }
}
