package com.baker.sdk.demo.longasr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baker.sdk.basecomponent.util.ThreadPoolUtil;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.sdk.demo.base.Constants;
import com.baker.sdk.demo.R;
import com.baker.sdk.longtime.asr.LongTimeAsr;
import com.baker.sdk.longtime.asr.bean.LongTimeAsrResponse;
import com.baker.sdk.longtime.asr.listener.LongTimeAsrCallBack;
import com.blankj.utilcode.util.UriUtils;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author hsj55
 */
public class LongAsrFileActivity extends BakerBaseActivity {
    private static final String TAG = LongAsrFileActivity.class.getName();
    private SharedPreferences mSharedPreferences;

    private TextView resultTv, pathTv;
    private Button btn;
    private EditText edtDomain;
    private Spinner spinner;
    private ProgressBar progressBar;

    private boolean isNormal = true;
    private String domain = "common";
    private final StringBuilder stringBuilder = new StringBuilder();
    private String[] samples;
    private int sample = 16000;
    private LongTimeAsr longTimeAsr;

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //onError
                    isNormal = true;
                    btn.setText("开启识别");
                    if (msg.obj != null) {
                        appendResult((String) msg.obj);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    //onReady
                    isNormal = false;
                    stringBuilder.setLength(0);
                    findViewById(R.id.tv_tip_3).setVisibility(View.GONE);
                    btn.setText("结束识别");
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    pathTv.setText((String) msg.obj);
                    btn.setEnabled(true);
                    btn.setText("开启识别");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_asr_file);

        setTitle("文件识别");

        mSharedPreferences = getSharedPreferences(Constants.SP_TABLE_NAME, Context.MODE_PRIVATE);
        samples = getResources().getStringArray(R.array.sample);

        progressBar = findViewById(R.id.progress);
        pathTv = findViewById(R.id.tv_file_path);
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
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sample = Integer.valueOf(samples[position]);
                if (sample == 8000) {
                    domain = "common";
                    edtDomain.setText("common");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn = findViewById(R.id.stopRecognize);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFileExist()) {
                    if (isNormal) {
                        String path = pathTv.getText().toString().trim();

                        isNormal = false;
                        stringBuilder.setLength(0);
                        btn.setText("结束识别");

                        startRecord(path);
                    } else {
                        isNormal = true;
                        btn.setText("开启识别");

                        stop();
                    }
                } else {
                    toast("请先选择文件");
                }
            }
        });
        edtDomain = findViewById(R.id.domain);
        findViewById(R.id.btn_choose_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNormal) {
                    toast("正在识别中...");
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //允许多选 长按多选
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                //不限制选取类型
                intent.setType("*/*");
                startActivityForResult(intent, 333);
            }
        });

        longTimeAsr = new LongTimeAsr();
        longTimeAsr.initSdk(LongAsrFileActivity.this,
                sharedPreferencesGet(Constants.LONG_TIME_ASR_ONLINE_CLIENT_ID),
                sharedPreferencesGet(Constants.LONG_TIME_ASR_ONLINE_CLIENT_SECRET), callBack);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 333:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    //当单选选了一个文件后返回
                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        File file = UriUtils.uri2File(uri);

                        Message message = new Message();
                        message.what = 2;
                        message.obj = file.getPath();
                        handler.sendMessage(message);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void startRecord(String path) {
        if (longTimeAsr != null) {
            //*********************************设置参数****************************
            //音频采样率，支持16000(默认)，8000
            longTimeAsr.setSampleRate(sample);
            //是否在短静音处添加标点，默认true
            longTimeAsr.setAddPct(true);
            //模型名称，必须填写公司购买的语言模型，默认为common
            String text = edtDomain.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                domain = text;
            }
            longTimeAsr.setDomain(domain);
            if (path.toLowerCase().endsWith("wav")) {
                longTimeAsr.setAudioFormat("wav");
            } else if (path.toLowerCase().endsWith("pcm")) {
                longTimeAsr.setAudioFormat("pcm");
            }
            //配置的热词组的id，有就设置，没有就不设置
//        longTimeAsr.setHotwordid("");
            //asr个性化模型的id，有就设置，没有就不设置
//        longTimeAsr.setDiylmid("");
            //*********************************结束设置参数****************************
            longTimeAsr.start();
            ThreadPoolUtil.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileInputStream in = new FileInputStream(path);
                        byte[] buffer;
                        int readSize;
                        //需要固定5120长度
                        while ((readSize = in.read(buffer = new byte[5120])) != -1) {
                            longTimeAsr.send(buffer);
                        }

                        //追加一片空数据，表示完成传输。
                        if (readSize < 5120) {
                            longTimeAsr.send(new byte[]{});
                        }
                        Thread.sleep(160);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void stop() {
        longTimeAsr.end();
    }

    private final LongTimeAsrCallBack callBack = new LongTimeAsrCallBack() {
        @Override
        public void onReady() {
            handler.sendEmptyMessage(1);
        }

        @Override
        public void onVolume(int volume) {

        }

        @Override
        public void onRecording(LongTimeAsrResponse response) {
            Log.e(TAG, "result = " + response.getAsr_text() + ", isLast = " + "true".equals(response.getSentence_end()));

            Message message = Message.obtain();
            message.what = 3;
            message.obj = stringBuilder + response.getAsr_text();
            handler.sendMessage(message);
            if ("true".equals(response.getSentence_end())) {
                stringBuilder.append(response.getAsr_text());
            }

            if (response.getEnd_flag() == 1) {
                handler.sendEmptyMessage(0);
            }
        }

        @Override
        public void onError(String code, String errorMessage) {
            Log.e(TAG, "code = " + code + ", errorMessage = " + errorMessage);
            Message message = Message.obtain();
            message.what = 0;
            message.obj = "error. code = " + code + ", message = " + errorMessage;
            handler.sendMessage(message);
        }
    };

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

    private void toast(String content) {
        Toast toast = Toast.makeText(this, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private boolean isFileExist() {
        String path = pathTv.getText().toString().trim();
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        try {
            File file = new File(path);
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void appendResult(final String str) {
        resultTv.post(() -> {
            resultTv.setText(str);
            int scrollAmount = resultTv.getLayout().getLineTop(resultTv.getLineCount())
                    - resultTv.getHeight();
            if (scrollAmount > 0)
                resultTv.scrollTo(0, scrollAmount + 30);
            else
                resultTv.scrollTo(0, 0);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (longTimeAsr != null) {
            longTimeAsr.release();
        }
    }
}