package com.baker.sdk.demo.convert;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;

public class VoiceConvertActivity extends BakerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_convert);
        setTitle("声音转换");
    }

    public void convertFromMic(View view) {
        startActivity(new Intent(VoiceConvertActivity.this, VoiceConvertFromMicActivity.class));
    }

    public void convertFromFile(View view) {
        startActivity(new Intent(VoiceConvertActivity.this, VoiceConvertFromFileActivity.class));
    }
}