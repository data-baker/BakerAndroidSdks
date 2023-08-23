package com.databaker.synthesizer;

import com.baker.sdk.basecomponent.bean.BakerError;
import com.baker.sdk.basecomponent.util.HLogger;
import com.baker.sdk.basecomponent.writelog.WriteLog;
import com.databaker.synthesizer.bean.OneSecPcmBlock;

/**
 * Create by hsj55
 * 2019/12/12
 */
public abstract class BakerMediaCallback implements SynthesizerCallback, BaseMediaCallback {
    private byte[] remaining;
    private byte[] tempBytes;

    @Override
    public void onSynthesisStarted() {
        HLogger.d("--onSynthesisStarted--");
    }

    @Override
    public void onSynthesisCompleted() {
        HLogger.d("--onSynthesisCompleted--");
    }

    @Override
    public void onBinaryReceived(byte[] data, String audioType, String interval,  String interval_x, boolean endFlag) {
//        if (endFlag) {
//            //是最后一帧
//            if (data != null && data.length > 0) {
//                //如果此次数据不为空
//            } else {
//                //此次数据为空
//                if (remaining != null && remaining.length > 0) {
//                    //剩余数据不为空
//
//                }
//            }
//        } else {
//            //这不是最后一帧
//            if (data != null && data.length > 0) {
//                //如果此次数据不为空
//            }
//        }
        try {
            int oneSecLength = BakerMediaPlayer.getInstance().get8KOr16K() ? 3200 : 1600;
            if (data != null && data.length > 0) {
                byte[] temp;
                //若之前有剩余的，先将剩余数据copy至temp数组。
                if (remaining != null && remaining.length > 0) {
                    int remainingLen = remaining.length;
                    temp = new byte[remainingLen + data.length];
                    System.arraycopy(remaining, 0, temp, 0, remainingLen);
                    System.arraycopy(data, 0, temp, remainingLen, data.length);
                } else {
                    //若之前没有剩余的，直接将data数据copy至temp数组。
                    temp = new byte[data.length];
                    System.arraycopy(data, 0, temp, 0, data.length);
                }
                int size = temp.length / oneSecLength;

                //先处理整数倍的秒数数据。
                for (int i = 0; i < size; i++) {
                    System.arraycopy(temp, i * oneSecLength, tempBytes = new byte[oneSecLength], 0, oneSecLength);
                    BakerMediaPlayer.getInstance().setAudioData(new OneSecPcmBlock(tempBytes), false);
                }
                int remainder = temp.length % oneSecLength;
                //余数部分处理
                if (endFlag) {
                    //若当前是合成的最后一段返回数据
                    if (remainder > 0) {
                        //直接拼成最后一秒数据给播放器
                        System.arraycopy(temp, temp.length - remainder, tempBytes = new byte[oneSecLength], 0, remainder);
                        BakerMediaPlayer.getInstance().setAudioData(new OneSecPcmBlock(tempBytes), true);
                    } else {
                        //告知播放器已合成完成
                        BakerMediaPlayer.getInstance().setAudioData(null, true);
                    }
                    remaining = null;
                } else {
                    if (remainder > 0) {
                        //将余数部分copy至remaining数组，给下批数据回来后一起处理。
                        System.arraycopy(temp, temp.length - remainder, remaining = new byte[remainder], 0, remainder);
                    }
                }
//                if (remainder > 0) {
//                    if (endFlag) {
//                        HLogger.d("这是最后一片数据.");
//                        //若当前是合成的最后一段返回数据，直接拼成最后一秒数据给播放器
//                        System.arraycopy(temp, temp.length - remainder, tempBytes = new byte[oneSecLength], 0, remainder);
//                        BakerMediaPlayer.getInstance().setAudioData(new OneSecPcmBlock(tempBytes), true);
//                    } else {
//                        //将余数部分copy至remaining数组，给下批数据回来后一起处理。
//                        System.arraycopy(temp, temp.length - remainder, remaining = new byte[remainder], 0, remainder);
//                    }
//                }
            } else if (endFlag) {
                HLogger.d("这是最后一片数据, 且数据为空.");
                if (remaining != null && remaining.length > 0) {
                    System.arraycopy(remaining, 0, tempBytes = new byte[oneSecLength], 0, remaining.length);
                    BakerMediaPlayer.getInstance().setAudioData(new OneSecPcmBlock(tempBytes), true);
                } else {
                    BakerMediaPlayer.getInstance().setAudioData(null, true);
                }
                remaining = null;
            } else {
                WriteLog.writeLogs("errorCode=90005, errorMsg=接收数据为空");
            }
            //通知播放器更新缓存进度
            BakerMediaPlayer.getInstance().onCacheAvailable();
        } catch (Exception e) {
//            CrashNetUtils.net(Log.getStackTraceString(e));
            WriteLog.writeLogs("errorCode=90005, errorMsg=" + e.getMessage());
//            onError(BakerConstants.ERROR_CODE_ERROR_INFO, "接收数据后分发出错", null);
        }
    }

    @Override
    public void onTaskFailed(BakerError errorBean) {
        HLogger.d("--onTaskFailed--");
        onError(errorBean);
        WriteLog.writeLogs("errorCode=" + errorBean.getCode() + ", errorMsg=" + errorBean.getMessage() + ", traceId=" + errorBean.getTrace_id());
    }
}
