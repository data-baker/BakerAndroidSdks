package com.baker.sdk.demo.longasr;

import androidx.annotation.NonNull;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baker.sdk.basecomponent.writelog.WriteLog;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.sdk.demo.base.Constants;
import com.baker.sdk.demo.R;
import com.baker.sdk.longtime.asr.LongTimeAsr;
import com.baker.sdk.longtime.asr.bean.LongTimeAsrResponse;
import com.baker.sdk.longtime.asr.listener.LongTimeAsrCallBack;

/**
 * @author hsj55
 */
public class LongAsrMicActivity extends BakerBaseActivity {
    private static final String TAG = LongAsrMicActivity.class.getName();
    private SharedPreferences mSharedPreferences;

    private ImageView imgRecording;
    private TextView resultTv, errorInfoTv;
    private Button btn;
    private EditText editText;
    private Spinner spinner;
    private int sample = 16000;
    private String[] samples;

    private String domain = "common";
    private boolean isNormal = true;
    private String mErrorInfo;
    private final StringBuilder stringBuilder = new StringBuilder();
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //onError
                    isNormal = true;
                    btn.setText("开启识别");
                    imgRecording.setVisibility(View.INVISIBLE);
                    mErrorInfo = (String) msg.obj;
                    errorInfoTv.setText("onError:" + mErrorInfo);
                    break;
                case 1:
                    //onReady
                    isNormal = false;
                    stringBuilder.setLength(0);
                    btn.setText("结束识别");
                    imgRecording.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    changeVolumeImg((int) msg.obj);
                    break;
                case 3:
                    //onRecording
                    appendResult((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private LongTimeAsr longTimeAsr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_asr_mic);

        setTitle("录音识别");
        samples = getResources().getStringArray(R.array.sample);
        mSharedPreferences = getSharedPreferences(Constants.SP_TABLE_NAME, Context.MODE_PRIVATE);

        errorInfoTv = findViewById(R.id.tv_error);
        errorInfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setText(mErrorInfo);
                toast("错误信息已复制至粘贴版");
            }
        });
        resultTv = findViewById(R.id.tv_Result);
        resultTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        resultTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setText(resultTv.getText().toString());
                toast("文本内容已复制至粘贴版");
                return false;
            }
        });
        btn = findViewById(R.id.startRecognize);
        imgRecording = findViewById(R.id.img_recording);
        imgRecording.setVisibility(View.INVISIBLE);
        editText = findViewById(R.id.edit_domain);

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sample = Integer.valueOf(samples[position]);
                if (sample == 8000) {
                    domain = "kefu";
                    editText.setText("kefu");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        longTimeAsr = new LongTimeAsr();
//        longTimeAsr.isDebug(true);
        longTimeAsr.initSdk(LongAsrMicActivity.this,
                sharedPreferencesGet(Constants.LONG_TIME_ASR_ONLINE_CLIENT_ID),
                sharedPreferencesGet(Constants.LONG_TIME_ASR_ONLINE_CLIENT_SECRET), callBack);
    }

    public void startRecognize(View view) {
        if (isNormal) {
            resultTv.setText("");
            errorInfoTv.setText("");
            mErrorInfo = "";
            startRecord();
        } else {
            stop();
            isNormal = true;
            btn.setText("开启识别");
            imgRecording.setVisibility(View.INVISIBLE);
        }
    }

    //    long time = 0;
//    boolean showLog = true;

    private void startRecord() {
        if (longTimeAsr != null) {
            //*********************************设置参数****************************
            //音频采样率，支持16000(默认)，8000
            longTimeAsr.setSampleRate(sample);
            //是否在短静音处添加标点，默认true
            longTimeAsr.setAddPct(true);
            //模型名称，必须填写公司购买的语言模型，默认为common
            String text = editText.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                domain = text;
            }
            longTimeAsr.setDomain(domain);
            //配置的热词组的id，有就设置，没有就不设置
//        longTimeAsr.setHotwordid("");
            //asr个性化模型的id，有就设置，没有就不设置
//        longTimeAsr.setDiylmid("");
            //*********************************结束设置参数****************************
//            time = System.currentTimeMillis();
//            showLog = true;
            longTimeAsr.startAsr();
        }
    }

    private void stop() {
        longTimeAsr.stopAsr();
    }

    private final LongTimeAsrCallBack callBack = new LongTimeAsrCallBack() {
        @Override
        public void onError(String code, String errorMessage) {
            Message message = Message.obtain();
            message.what = 0;
            message.obj = "error. code = " + code + ", message = " + errorMessage;
            handler.sendMessage(message);
        }

        @Override
        public void onReady() {
            handler.sendEmptyMessage(1);
        }

        @Override
        public void onVolume(int i) {
//            Log.e("onVolume", "音量: " + i);
            Message message = Message.obtain();
            message.what = 2;
            message.obj = i;
            handler.sendMessage(message);
        }

        @Override
        public void onRecording(LongTimeAsrResponse response) {
//            Log.e(TAG, "result = " + result + ", isLast = " + sentenceEnd);
//            if (showLog) {
//                long w = System.currentTimeMillis() - time;
//                Log.e("waste_time", "总耗时: " + w);
//                WriteText.writeLogs("总耗时: " + w);
//                showLog = false;
//            }

            Message message = Message.obtain();
            message.what = 3;
            message.obj = stringBuilder.toString() + response.getAsr_text();
            handler.sendMessage(message);

            if ("true".equals(response.getSentence_end())) {
                stringBuilder.append(response.getAsr_text());
            }

            if (response.getEnd_flag() == 1) {
                handler.sendEmptyMessage(0);
            }
        }
    };

    private void toast(String content) {
        Toast toast = Toast.makeText(this, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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
                    resultTv.scrollTo(0, scrollAmount + 30);
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

    public void onParentLayoutOnClick(View view) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (longTimeAsr != null) {
            longTimeAsr.release();
        }
    }
}