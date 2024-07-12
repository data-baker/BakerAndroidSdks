package com.baker.sdk.demo.asr;

import androidx.annotation.NonNull;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baker.sdk.demo.base.Constants;
import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.speech.asr.BakerRecognizer;
import com.baker.speech.asr.base.BakerRecognizerCallback;
import com.baker.speech.asr.bean.BakerException;
import com.baker.speech.asr.bean.BakerResponse;

import java.util.List;

/**
 * @author hsj55
 */
public class AsrMicActivity extends BakerBaseActivity {
    private static final String TAG = AsrMicActivity.class.getName();
    private SharedPreferences mSharedPreferences;

    private TextView resultTv, traceTv;
    private Button btn;
    private ImageView imgRecording;
    private EditText editDomain;
    private String domain = "common";
    private String mTraceId;
    private int sample = 16000;
    private String[] samples;
    private Spinner spinner;

    private BakerRecognizer bakerRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asr_mic);

        setTitle("录音识别");
        samples = getResources().getStringArray(R.array.sample);
        mSharedPreferences = getSharedPreferences(Constants.SP_TABLE_NAME, Context.MODE_PRIVATE);

        traceTv = findViewById(R.id.tv_trace);
        traceTv.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cm.setText(mTraceId);
            toast("TraceId已复制至粘贴版");
        });

        resultTv = findViewById(R.id.tv_Result);
        resultTv.setOnLongClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cm.setText(resultTv.getText().toString());
            toast("文本内容已复制至粘贴版");
            return false;
        });

        btn = findViewById(R.id.startRecognize);
        imgRecording = findViewById(R.id.img_recording);
        imgRecording.setVisibility(View.INVISIBLE);
        editDomain = findViewById(R.id.edit_domain);

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sample = Integer.valueOf(samples[position]);
                if (sample == 8000) {
                    domain = "kefu";
                    editDomain.setText("kefu");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn.setOnClickListener(v -> {
            if (bakerRecognizer != null) {
                setParams();
                //返回0启动成功，返回1=callback为空，未启动成功
                bakerRecognizer.startAsr();
            }
            resultTv.setText("");
            traceTv.setText("");
            btn.setEnabled(false);
        });


        bakerRecognizer = new BakerRecognizer();
        bakerRecognizer.initSdk(AsrMicActivity.this, sharedPreferencesGet(Constants.ASR_ONLINE_CLIENT_ID),
                sharedPreferencesGet(Constants.ASR_ONLINE_CLIENT_SECRET), bakerRecognizerCallback);
    }

    private void toast(String content) {
        Toast toast = Toast.makeText(this, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //onError
                    btn.setText("点击开启识别");
                    btn.setEnabled(true);
                    imgRecording.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    //onReady
                    btn.setText("识别中");
                    imgRecording.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    changeVolumeImg((int) msg.obj);
                    traceTv.setText("TraceId:" + mTraceId);
                    break;
                default:
                    break;
            }
        }
    };

    private final BakerRecognizerCallback bakerRecognizerCallback = new BakerRecognizerCallback() {
        @Override
        public void onReadyOfSpeech() {
            resultTv.setText("");
            handler.sendEmptyMessage(1);
        }

        @Override
        public void onVolumeChanged(int volume) {
            Message message = Message.obtain();
            message.what = 2;
            message.obj = volume;
            handler.sendMessage(message);
        }

        @Override
        public void onResult(BakerResponse response) {
            if (response != null) {
                mTraceId = response.getTraceId();
                if (response.getNbest() != null && response.getNbest().size() > 0) {
                    Log.e(TAG, "result=" + response.getNbest().get(0));
                    appendResult(response.getNbest().get(0));
                }
            }
        }

        /**
         * 此回调表示：sdk内部录音机识别到用户开始输入声音。
         */
        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {
            if (bakerRecognizer != null) {
                bakerRecognizer.stopAsr();
            }

            Message message = Message.obtain();
            message.what = 0;
            handler.sendMessage(message);
        }

        @Override
        public void onError(BakerException error) {
            appendResult("\n识别错误 : " + error.getCode() + ", " + error.getMessage());
            Log.e(TAG, "code=" + error.getCode() + ", message=" + error.getMessage());
            Message message = Message.obtain();
            message.what = 0;
            message.obj = "error. code = " + error.getCode() + ", message = " + error.getMessage();
            handler.sendMessage(message);
        }
    };

    private void setParams() {
        String text = editDomain.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            domain = text;
        }
        //音频采样率
        bakerRecognizer.setSampleRate(sample);
        //模型名称 默认值=中文通用模型 "common"
        bakerRecognizer.setDomain(domain);
        //true: 加标点，默认值
        bakerRecognizer.setAddPct(true);
        //配置的热词组的id，有就设置，没有就不设置
//        bakerRecognizer.setHotwordid("");
        //asr个性化模型的id，有就设置，没有就不设置
//        bakerRecognizer.setDiylmid("");
        //开启服务器端vad 静音检测， 默认是关闭false
        bakerRecognizer.setEnable_vad(true);
        //当enable_vad为true时有效，表示允许的最大开始静音时长，不设置就使用默认值
//        bakerRecognizer.setMax_begin_silence(600);
        //当enable_vad为true时有效，表示允许的最大结束静音时长，不设置就使用默认值
//        bakerRecognizer.setMax_end_silence(600);
    }

    private void changeVolumeImg(int volume) {
        if (volume < 30) {
            imgRecording.setImageResource(R.mipmap.volume_1);
        } else if (volume < 40) {
            imgRecording.setImageResource(R.mipmap.volume_2);
        } else if (volume < 50) {
            imgRecording.setImageResource(R.mipmap.volume_3);
        } else if (volume < 60) {
            imgRecording.setImageResource(R.mipmap.volume_4);
        } else if (volume < 70) {
            imgRecording.setImageResource(R.mipmap.volume_5);
        } else if (volume < 80) {
            imgRecording.setImageResource(R.mipmap.volume_6);
        } else if (volume < 90) {
            imgRecording.setImageResource(R.mipmap.volume_7);
        } else {
            imgRecording.setImageResource(R.mipmap.volume_8);
        }
    }

    private void appendResult(final String str) {
        resultTv.post(new Runnable() {
            @Override
            public void run() {
                resultTv.setText(str);
                int scrollAmount = resultTv.getLayout().getLineTop(resultTv.getLineCount())
                        - resultTv.getHeight();
                if (scrollAmount > 0)
                    resultTv.scrollTo(0, scrollAmount);
                else
                    resultTv.scrollTo(0, 0);
            }
        });
    }

    private String sharedPreferencesGet(String mKey) {
        if (mSharedPreferences != null && !TextUtils.isEmpty(mKey)) {
            return mSharedPreferences.getString(mKey, "");
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bakerRecognizer != null) {
            bakerRecognizer.release();
        }
    }

    public void onParentLayoutOnClick(View view) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}