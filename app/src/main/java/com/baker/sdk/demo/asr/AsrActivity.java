package com.baker.sdk.demo.asr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;

/**
 * @author hsj55
 */
public class AsrActivity extends BakerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asr);
        setTitle("一句话识别");
    }

    public void asrFromMic(View view) {
        startActivity(new Intent(AsrActivity.this, AsrMicActivity.class));
    }

    public void asrFromFile(View view) {
        startActivity(new Intent(AsrActivity.this, AsrFileActivity.class));
    }
}