package com.baker.sdk.demo.longasr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;

/**
 * @author hsj55
 */
public class LongAsrActivity extends BakerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_asr);
        setTitle("长语音识别");
    }

    public void longAsrFromMic(View view) {
        startActivity(new Intent(LongAsrActivity.this, LongAsrMicActivity.class));
    }

    public void longAsrFromFile(View view) {
        startActivity(new Intent(LongAsrActivity.this, LongAsrFileActivity.class));
    }
}