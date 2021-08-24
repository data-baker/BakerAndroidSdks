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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baker.sdk.demo.base.Constants;
import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.speech.asr.BakerRecognizer;
import com.baker.speech.asr.base.BakerRecognizerCallback;
import com.baker.speech.asr.bean.BakerException;

import java.util.List;

/**
 * @author hsj55
 */
public class AsrMicActivity extends BakerBaseActivity {
    private static String TAG = AsrMicActivity.class.getName();
    private SharedPreferences mSharedPreferences;

    private TextView resultTv, traceTv;
    private Button btn;
    private ImageView imgRecording;
    private EditText spinner;
    private String domain = "common";
    private String mTraceId;

    private BakerRecognizer bakerRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asr_mic);

        setTitle("录音识别");

        mSharedPreferences = getSharedPreferences(Constants.SP_TABLE_NAME, Context.MODE_PRIVATE);

        traceTv = findViewById(R.id.tv_trace);
        traceTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setText(mTraceId);
                toast("TraceId已复制至粘贴版");
            }
        });

        resultTv = findViewById(R.id.tv_Result);
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
        spinner = findViewById(R.id.spinner);

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (bakerRecognizer != null) {
                    setParams();
                    //返回0启动成功，返回1=callback为空，未启动成功
                    bakerRecognizer.startAsr();
                }
                resultTv.setText("");
                traceTv.setText("");
                return true;
            }
        });
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.e("hsj", "MotionEvent.ACTION_UP");
                    if (bakerRecognizer != null) {
                        bakerRecognizer.stopAsr();
                    }
                    btn.setText("长按开启识别");
                    imgRecording.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        bakerRecognizer = new BakerRecognizer();
        bakerRecognizer.initSdk(AsrMicActivity.this, bakerRecognizerCallback);
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
                    btn.setText("长按开启识别");
                    imgRecording.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    //onReady
                    btn.setText("松开结束识别");
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

    private BakerRecognizerCallback bakerRecognizerCallback = new BakerRecognizerCallback(){
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
        public void onResult(List<String> nbest, List<String> uncertain, boolean isLast, String traceId) {
            mTraceId = traceId;
            if (nbest != null && nbest.size() > 0) {
                appendResult(nbest.get(0));
            }
        }

        @Override
        public void onEndOfSpeech() {
            appendResult("\n识别结束");
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
        bakerRecognizer.setUrl("ws://10.10.50.21:9002");
        String text = spinner.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            domain = text;
        }
        bakerRecognizer.setDomain(domain);
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