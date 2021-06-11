package com.baker.sdk.demo.asr;

import android.os.Bundle;

import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;

/**
 * @author hsj55
 */
public class AsrFileActivity extends BakerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asr_file);
        setTitle("文件识别");
    }
}