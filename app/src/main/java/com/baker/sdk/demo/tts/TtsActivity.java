package com.baker.sdk.demo.tts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;

public class TtsActivity extends BakerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);
        setTitle("标贝在线合成");
    }

    public void toAudioTrackPlayerActivity(View view) {
        startActivity(new Intent(this, AudioTrackPlayerActivity.class));
    }

    public void toMediaTrackPlayerActivity(View view) {
        startActivity(new Intent(this, MediaPlayerActivity.class));
    }
}