package com.baker.sdk.longtime.asr.event;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.baker.sdk.basecomponent.util.GsonConverter;
import com.baker.sdk.longtime.asr.bean.LongTimeAsrError;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.baker.sdk.longtime.asr.base.BakerLongTimeAsrConstants.ERROR_CODE_NOT_INIT_RECORD;
import static com.baker.sdk.longtime.asr.base.BakerLongTimeAsrConstants.ERROR_CODE_RECORD_ING;
import static com.baker.sdk.longtime.asr.base.BakerPrivateConstants.bufferSizeForUpload;
import static com.baker.sdk.longtime.asr.base.BakerPrivateConstants.dataQueue;

/**
 * @author hsj55
 * 2020/9/24
 */
public class EventManagerMultiMic implements EventManager {
    private static final LinkedBlockingQueue<Runnable> AudioRecordQueue = new LinkedBlockingQueue<>(10);
    private static final ExecutorService mSingleExecutorServiceForOrderRequest =
            new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, AudioRecordQueue);
    //音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;


    //录音对象
    private AudioRecord audioRecord;

    //录音状态
    private Status status = Status.STATUS_NO_READY;


    private EventManager mOwner;
    private EventManager mNet;

    @Override
    public void send(String name, byte[] data, String params) {
        switch (name) {
            case "mic.start":
                stopRecord();
                status = Status.STATUS_NO_READY;
                createDefaultAudio(params);
                dataQueue.clear();
                break;
            case "mic.record":
                startRecord();
                submit();
                break;
            case "mic.stop":
                stopRecord();
                break;
            default:
                break;
        }
    }

    public void setmOwner(EventManager mOwner) {
        this.mOwner = mOwner;
    }

    public void setNetManager(EventManager mNet) {
        this.mNet = mNet;
    }

    /**
     * 创建默认的录音对象
     */
    public void createDefaultAudio(String param) {
        if (audioRecord == null || Integer.parseInt(param) != AUDIO_SAMPLE_RATE) {
            AUDIO_SAMPLE_RATE = Integer.parseInt(param);
            // 获得缓冲区字节大小
            bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                    AUDIO_CHANNEL, AUDIO_ENCODING);
            audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
        }
        status = Status.STATUS_READY;
    }

    /**
     * 开始录音
     */
    public void startRecord() {
        if (status == Status.STATUS_NO_READY) {
            onFault(ERROR_CODE_NOT_INIT_RECORD, "录音尚未初始化,请检查是否禁止了录音权限~");
            return;
        }
        if (status == Status.STATUS_START) {
            onFault(ERROR_CODE_RECORD_ING, "正在录音");
            return;
        }
        if (audioRecord != null) {
            audioRecord.startRecording();
            //将录音状态设置成正在录音状态
            status = Status.STATUS_START;
        }
    }

    public void stopRecord() {
        if (audioRecord != null) {
            audioRecord.stop();
        }
        //将录音状态设置成正在录音状态
        status = Status.STATUS_STOP;
    }


    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int readsize = 0;
            // new一个byte数组用来存一些字节数据，大小为缓冲区大小
            byte[] audiodata;
            while (status == Status.STATUS_START) {
                audiodata = new byte[bufferSizeForUpload];
                readsize = audioRecord.read(audiodata, 0, bufferSizeForUpload);
                if (AudioRecord.ERROR_INVALID_OPERATION != readsize && readsize > 0) {
                    dataQueue.offer(audiodata);
                    EventManagerMessagePool.offer(mNet, "net.upload");
                    calculateVolume(audiodata);
                }
            }
            if (status == Status.STATUS_STOP) {
                dataQueue.offer(new byte[]{0,0});
                EventManagerMessagePool.offer(mNet, "net.upload");
            }
        }
    };

    private short[] toShorts(byte[] audioData) {
        short[] shorts = new short[audioData.length / 2];
        ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        return shorts;
    }

    private long time;

    private void calculateVolume(byte[] audioData) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - time > 100) {
            short[] shorts = toShorts(audioData);
            int nMaxAmp = 0;
            int arrLen = shorts.length;
            int peakIndex;
            for (peakIndex = 0; peakIndex < arrLen; peakIndex++) {
                if (shorts[peakIndex] >= nMaxAmp) {
                    nMaxAmp = shorts[peakIndex];
                }
            }
            int volume = (int) (20 * Math.log10(nMaxAmp));

            EventManagerMessagePool.offer(mOwner, "mic.volume", String.valueOf(volume));
            time = currentTime;
        }
    }

    private void submit() {
        mSingleExecutorServiceForOrderRequest.submit(runnable);
    }

    private void onFault(String code, String message) {
        EventManagerMessagePool.offer(mOwner, "mic.error", GsonConverter.toJson(new LongTimeAsrError(code, message)));
    }

    /**
     * 录音对象的状态
     */
    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //停止
        STATUS_STOP
    }

    public void release() {
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
    }
}
