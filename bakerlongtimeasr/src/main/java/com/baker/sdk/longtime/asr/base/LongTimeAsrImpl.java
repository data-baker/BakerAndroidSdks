package com.baker.sdk.longtime.asr.base;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.baker.sdk.basecomponent.BakerBaseConstants;
import com.baker.sdk.basecomponent.util.GsonConverter;
import com.baker.sdk.basecomponent.util.Util;
import com.baker.sdk.http.BakerTokenManager;
import com.baker.sdk.http.CallbackListener;
import com.baker.sdk.longtime.asr.bean.LongTimeAsrError;
import com.baker.sdk.longtime.asr.bean.LongTimeAsrParams;
import com.baker.sdk.longtime.asr.bean.LongTimeAsrResponse;
import com.baker.sdk.longtime.asr.event.EventManager;
import com.baker.sdk.longtime.asr.event.EventManagerMessagePool;
import com.baker.sdk.longtime.asr.event.EventManagerMultiMic;
import com.baker.sdk.longtime.asr.event.EventManagerMultiNet;
import com.baker.sdk.longtime.asr.listener.LongTimeAsrCallBack;
import com.baker.sdk.longtime.asr.listener.LongTimeAsrInterface;

import static com.baker.sdk.longtime.asr.base.BakerPrivateConstants.dataQueue;

/**
 * @author hsj55
 * 2020/9/24
 */
public class LongTimeAsrImpl implements EventManager, LongTimeAsrInterface {
    private EventManagerMultiMic mic;
    private EventManagerMultiNet net;
    private LongTimeAsrCallBack mCallBack;
    private Context mContext;
    private String privateUrl;

    private AudioManager mAudioManager;
    private TelephonyManager mTelephonyManager;
    private AudioFocusRequest mFocusRequest;
    private BakerTokenManager tokenManager;

    private int sampleRate = 16000;
    private boolean addPct = true;
    private String domain = "common";
    private boolean debug = false;
    private String audioFormat = "pcm";

    public LongTimeAsrImpl() {
    }

    @Override
    public void isDebug(boolean d) {
        debug = d;
    }

    @Override
    public void initSdk(Context context, LongTimeAsrCallBack callBack) {
        mContext = context;
        mCallBack = callBack;
        try {
            if (!Util.checkConnectNetwork(mContext)) {
                if (mCallBack != null) {
                    mCallBack.onError(BakerLongTimeAsrConstants.ERROR_TYPE_NET_UNUSABLE, "网络连接不可用");
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        //初始化基础库
        BakerBaseConstants.setIsDebug(debug);
    }

    @Override
    public void initSdk(Context context, String clientId, String secret, LongTimeAsrCallBack callBack) {
        mContext = context;
        mCallBack = callBack;

        if (TextUtils.isEmpty(clientId)) {
            if (mCallBack != null) {
                mCallBack.onError(BakerLongTimeAsrConstants.ERROR_CODE_INIT_FAILED_CLIENT_ID_FAULT, "缺少ClientId");
            }
            return;
        }
        if (TextUtils.isEmpty(secret)) {
            if (mCallBack != null) {
                mCallBack.onError(BakerLongTimeAsrConstants.ERROR_CODE_INIT_FAILED_CLIENT_SECRET_FAULT, "缺少Secret");
            }
            return;
        }

        try {
            if (!Util.checkConnectNetwork(mContext)) {
                if (mCallBack != null) {
                    mCallBack.onError(BakerLongTimeAsrConstants.ERROR_TYPE_NET_UNUSABLE, "网络连接不可用");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        //初始化基础库
        BakerBaseConstants.setIsDebug(debug);

        BakerPrivateConstants.clientId = clientId;
        BakerPrivateConstants.clientSecret = secret;

        //调用授权
        tokenManager = new BakerTokenManager();
        tokenManager.authentication(clientId, secret, new CallbackListener<String>() {
            @Override
            public void onSuccess(String response) {
                BakerPrivateConstants.token = response;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("LongTimeAsrImpl", "baker long time asr sdk init token error, " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public void initSdk(Context context, String clientId, String secret) {
        initSdk(context, clientId, secret, null);
    }

    @Override
    public void setCallBack(LongTimeAsrCallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public void setSampleRate(int rate) {
        if (rate == 16000 || rate == 8000) {
            sampleRate = rate;
        }
    }

    @Override
    public void setAddPct(boolean addPct) {
        this.addPct = addPct;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public void setAudioFormat(String format) {
        if ("pcm".equals(format.toLowerCase()) || "wav".equals(format.toLowerCase())) {
            audioFormat = format;
        }
    }

    @Override
    public void setUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            this.privateUrl = url;
        }
    }

    @Override
    public void startAsr() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)) {
            if (mCallBack != null) {
                mCallBack.onError(BakerLongTimeAsrConstants.ERROR_TYPE_RECORD_PERMISSION, "recording permission is forbidden.");
            }
            return;
        }

        manageAudioFocus();

        if (mic == null) {
            mic = new EventManagerMultiMic();
        }
        if (net == null) {
            net = new EventManagerMultiNet();
        }

        mic.setmOwner(LongTimeAsrImpl.this);
        mic.setNetManager(net);

        net.setmOwner(LongTimeAsrImpl.this);
        if (!TextUtils.isEmpty(privateUrl)) {
            net.setUrl(privateUrl);
        }

        EventManagerMessagePool.offer(mic, "mic.start", String.valueOf(sampleRate));
        //1=sdk麦克风录音 2=接收字节流
        EventManagerMessagePool.offer(net, "net.start", GsonConverter.toJson(new LongTimeAsrParams(audioFormat, sampleRate, addPct, domain, 1)));
    }

    @Override
    public void stopAsr() {
        EventManagerMessagePool.offer(mic, "mic.stop");
        reset();
    }

    /**
     * start()、send()、end()搭配使用，用于接收字节流接口
     */
    @Override
    public void start() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)) {
            if (mCallBack != null) {
                mCallBack.onError(BakerLongTimeAsrConstants.ERROR_TYPE_RECORD_PERMISSION, "recording permission is forbidden.");
            }
            return;
        }

        manageAudioFocus();
        if (net == null) {
            net = new EventManagerMultiNet();
        }
        net.setmOwner(LongTimeAsrImpl.this);
        if (!TextUtils.isEmpty(privateUrl)) {
            net.setUrl(privateUrl);
        }
        //1=sdk麦克风录音 2=接收字节流
        EventManagerMessagePool.offer(net, "net.start", GsonConverter.toJson(new LongTimeAsrParams(audioFormat, sampleRate, addPct, domain, 2)));
    }

    /**
     * start()、send()、end()搭配使用，用于接收字节流接口
     */
    @Override
    public void send(byte[] data) {
        if (data == null) {
            if (mCallBack != null) {
                mCallBack.onError(BakerLongTimeAsrConstants.ERROR_CODE_DATA_IS_NULL, "发送的数据为空.");
            }
            return;
        }
        if (net == null) {
            if (mCallBack != null) {
                mCallBack.onError(BakerLongTimeAsrConstants.ERROR_CODE_NOT_START, "网络未准备好，没有调用start()方法.");
            }
            return;
        }
        dataQueue.offer(data);
        EventManagerMessagePool.offer(net, "net.upload");
    }

    /**
     * start()、send()、end()搭配使用，用于接收字节流接口
     */
    @Override
    public void end() {
        if (net != null) {
            EventManagerMessagePool.offer(net, "net.disconnect");
        }
        reset();
    }

    @Override
    public void send(String name, byte[] data, String params) {
        switch (name) {
            case "net.start-called-1":
                if (mCallBack != null) {
                    mCallBack.onReady();
                }
                //获取声音数据
                EventManagerMessagePool.offer(mic, "mic.record");
                break;
            case "net.start-called-2":
                if (mCallBack != null) {
                    mCallBack.onReady();
                }
                dataQueue.clear();
                break;
            case "asr.partial":
                if (!TextUtils.isEmpty(params) && mCallBack != null) {
                    LongTimeAsrResponse response = GsonConverter.fromJson(params, LongTimeAsrResponse.class);
                    mCallBack.onRecording(response.getAsr_text(), "true".equals(response.getSentence_end()), response.getEnd_flag() == 1);
                }
                break;
            case "net.error":
            case "mic.error":
                stopAsr();
                if (mCallBack != null && !TextUtils.isEmpty(params)) {
                    LongTimeAsrError error = GsonConverter.fromJson(params, LongTimeAsrError.class);
                    mCallBack.onError(error.getCode(), error.getErrorMessage());
                }
                break;
            case "mic.volume":
                if (mCallBack != null && !TextUtils.isEmpty(params)) {
                    mCallBack.onVolume(Integer.valueOf(params));
                }
                break;
            default:
                break;
        }
    }

    private void manageAudioFocus() {
        try {
            if (!Util.checkConnectNetwork(mContext)) {
                if (mCallBack != null) {
                    mCallBack.onError(BakerLongTimeAsrConstants.ERROR_TYPE_NET_UNUSABLE, "网络连接不可用");
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        reset();

        if (mAudioManager != null) {
            //获取音频焦点
            try {
                //8.0版本以后
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build();
                    mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(mPlaybackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(audioFocusChangeListener)
                            .setWillPauseWhenDucked(true)
                            .build();
                    mAudioManager.requestAudioFocus(mFocusRequest);

                } else {
                    //流媒体设置焦点参数
                    mAudioManager.requestAudioFocus(audioFocusChangeListener,
                            // Use the music stream.(默认音乐流)
                            AudioManager.USE_DEFAULT_STREAM_TYPE,
                            // Request permanent focus.(要求永久焦点)
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mTelephonyManager != null) {
            //手动注册对PhoneStateListener中的listen_call_state状态进行监听
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void reset() {
        if (mAudioManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mFocusRequest != null) {
                mAudioManager.abandonAudioFocusRequest(mFocusRequest);
            } else {
                mAudioManager.abandonAudioFocus(audioFocusChangeListener);
            }
        }
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //电话通话的状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //电话响铃的状态
                    stopAsr();
                    if (mCallBack != null) {
                        mCallBack.onError(BakerLongTimeAsrConstants.ERROR_TYPE_RECORD_INTERRUPTION, "audioRecord is been interrupt");
                    }
                    break;
                default:
                    break;
            }
            super.onCallStateChanged(state, phoneNumber);
        }
    };

    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {

        }
    };

    @Override
    public void release() {
        if (mic != null) {
            mic.release();
            mic = null;
        }

        if (net != null) {
            net.release();
            net = null;
        }

        mContext = null;
        mCallBack = null;
        if (tokenManager != null) {
            tokenManager.release();
            tokenManager = null;
        }
    }
}
