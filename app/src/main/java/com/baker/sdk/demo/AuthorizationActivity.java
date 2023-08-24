package com.baker.sdk.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baker.sdk.demo.asr.AsrActivity;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.sdk.demo.base.Constants;
import com.baker.sdk.demo.convert.VoiceConvertActivity;
import com.baker.sdk.demo.convert.VoiceConvertFromFileActivity;
import com.baker.sdk.demo.convert.VoiceConvertFromMicActivity;
import com.baker.sdk.demo.gramophone.GramophoneActivity;
import com.baker.sdk.demo.longasr.LongAsrActivity;
import com.baker.sdk.demo.tts.TtsActivity;
import com.baker.sdk.http.BakerTokenManager;
import com.baker.sdk.http.CallbackListener;

/**
 * @author hsj55
 */

public class AuthorizationActivity extends BakerBaseActivity {
    private String type;
    private SharedPreferences mSharedPreferences;
    private EditText etClientId, etClientSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        init();
    }

    private void init() {
        etClientId = findViewById(R.id.et_client_id);
        etClientSecret = findViewById(R.id.et_client_secret);
        mSharedPreferences = getSharedPreferences(Constants.SP_TABLE_NAME, Context.MODE_PRIVATE);
        type = getIntent().getStringExtra(Constants.EXPERIENCE_TYPE);
        if (!TextUtils.isEmpty(type)) {
            switch (type) {
                case "tts_online":
                    //体验tts,授权tts获取token
                    setTitle("tts授权");
                    if (!TextUtils.isEmpty(sharedPreferencesGet(Constants.TTS_ONLINE_CLIENT_ID))) {
                        etClientId.setText(sharedPreferencesGet(Constants.TTS_ONLINE_CLIENT_ID));
                    }
                    if (!TextUtils.isEmpty(sharedPreferencesGet(Constants.TTS_ONLINE_CLIENT_SECRET))) {
                        etClientSecret.setText(sharedPreferencesGet(Constants.TTS_ONLINE_CLIENT_SECRET));
                    }
                    etClientId.setText("82bf72b1d19e4397bfd3cf37d542e910");
                    etClientSecret.setText("2ab86ad68beb4285a99f0f7783692f79");
                    break;
                case "asr_online":
                    setTitle("语音识别授权");
                    if (!TextUtils.isEmpty(sharedPreferencesGet(Constants.ASR_ONLINE_CLIENT_ID))) {
                        etClientId.setText(sharedPreferencesGet(Constants.ASR_ONLINE_CLIENT_ID));
                    }
                    if (!TextUtils.isEmpty(sharedPreferencesGet(Constants.ASR_ONLINE_CLIENT_SECRET))) {
                        etClientSecret.setText(sharedPreferencesGet(Constants.ASR_ONLINE_CLIENT_SECRET));
                    }
                    etClientId.setText("0db2b34e21b942e68f14420d21bcddfe");
                    etClientSecret.setText("325eb5ca07bf4b1c87dfa6192b8ccaa5");
                    break;
                case "long_time_asr_online":
                    //体验长语音asr,授权tts获取token
                    setTitle("长语音识别授权");
                    if (!TextUtils.isEmpty(sharedPreferencesGet(Constants.LONG_TIME_ASR_ONLINE_CLIENT_ID))) {
                        etClientId.setText(sharedPreferencesGet(Constants.LONG_TIME_ASR_ONLINE_CLIENT_ID));
                    }
                    if (!TextUtils.isEmpty(sharedPreferencesGet(Constants.LONG_TIME_ASR_ONLINE_CLIENT_SECRET))) {
                        etClientSecret.setText(sharedPreferencesGet(Constants.LONG_TIME_ASR_ONLINE_CLIENT_SECRET));
                    }
                    etClientId.setText("16a4fca77a774da99086be973a707863");
                    etClientSecret.setText("2b253a98ee9746509e1753f5876706ef");
                    break;
                case "voice_convert":
                    //声音转换 获取token
                    setTitle("声音转换");
                   if (!TextUtils.isEmpty(sharedPreferencesGet(Constants.VOICE_CONVERT_CLIENT_ID))) {
                        etClientId.setText(sharedPreferencesGet(Constants.VOICE_CONVERT_CLIENT_ID));
                    }
                    if (!TextUtils.isEmpty(sharedPreferencesGet(Constants.VOICE_CONVERT_CLIENT_SECRET))) {
                        etClientSecret.setText(sharedPreferencesGet(Constants.VOICE_CONVERT_CLIENT_SECRET));
                    }
                    etClientId.setText("124c18404c7c47cbb4ceca5d92eb2c72");
                    etClientSecret.setText("711d81016f6f4f0ba094ee96b5ce03d6");
                    break;
                case "gramophone":
                    //声音转换 获取token
                    setTitle("声音复刻");
                    if (!TextUtils.isEmpty(sharedPreferencesGet(Constants.GRAMOPHONE_CLIENT_ID))) {
                        etClientId.setText(sharedPreferencesGet(Constants.GRAMOPHONE_CLIENT_ID));
                    }
                    if (!TextUtils.isEmpty(sharedPreferencesGet(Constants.GRAMOPHONE_CLIENT_SECRET))) {
                        etClientSecret.setText(sharedPreferencesGet(Constants.GRAMOPHONE_CLIENT_SECRET));
                    }
                    //etClientId.setText("df6527d33b644733a2b4e58de710fd9d");
                    //etClientSecret.setText("ca9c99132d3c4d2bb92501410ee21bd8");
                    etClientId.setText("1fed232b78b04eae9fa21b464d6d2db9");
                    etClientSecret.setText("3e98fedaa8584ee193c2050423652066");
                    break;
                default:
                    break;
            }
        }
    }

    public void jump(View view) {
        //TODO 校验token有效后进行存储，体验者第二次进来不再需要输入
        if (TextUtils.isEmpty(etClientSecret.getText().toString().trim())) {
            Toast.makeText(this, "请输入ClientSecret", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(etClientId.getText().toString().trim())) {
            Toast.makeText(this, "请输入ClientId", Toast.LENGTH_SHORT).show();
            return;
        }
        new BakerTokenManager().authentication(etClientId.getText().toString().trim(), etClientSecret.getText().toString().trim(), new CallbackListener() {
            @Override
            public void onSuccess(Object response) {
                storageParameter();
            }

            @Override
            public void onFailure(Exception e) {
                removeParameter();
                Toast.makeText(AuthorizationActivity.this, "ClientId/ClientSecret校验失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 存储参数
     */
    private void storageParameter() {
        if (TextUtils.isEmpty(type)) return;
        String clientId = etClientId.getText().toString().trim();
        String clientSecret = etClientSecret.getText().toString().trim();
        Intent mIntent = new Intent();
        switch (type) {
            case "tts_online":
                sharedPreferencesCommit(Constants.TTS_ONLINE_CLIENT_ID, clientId);
                sharedPreferencesCommit(Constants.TTS_ONLINE_CLIENT_SECRET, clientSecret);
                mIntent.setClass(AuthorizationActivity.this, TtsActivity.class);
                break;
            case "asr_online":
                sharedPreferencesCommit(Constants.ASR_ONLINE_CLIENT_ID, clientId);
                sharedPreferencesCommit(Constants.ASR_ONLINE_CLIENT_SECRET, clientSecret);
                mIntent.setClass(AuthorizationActivity.this, AsrActivity.class);
                break;
            case "long_time_asr_online":
                sharedPreferencesCommit(Constants.LONG_TIME_ASR_ONLINE_CLIENT_ID, clientId);
                sharedPreferencesCommit(Constants.LONG_TIME_ASR_ONLINE_CLIENT_SECRET, clientSecret);
                mIntent.setClass(AuthorizationActivity.this, LongAsrActivity.class);
                break;
            case "voice_convert":
                sharedPreferencesCommit(Constants.VOICE_CONVERT_CLIENT_ID, clientId);
                sharedPreferencesCommit(Constants.VOICE_CONVERT_CLIENT_SECRET, clientSecret);
                mIntent.setClass(AuthorizationActivity.this, VoiceConvertActivity.class);
                break;
            case "gramophone":
                sharedPreferencesCommit(Constants.GRAMOPHONE_CLIENT_ID, clientId);
                sharedPreferencesCommit(Constants.GRAMOPHONE_CLIENT_SECRET, clientSecret);
                mIntent.setClass(AuthorizationActivity.this, GramophoneActivity.class);
                break;
            default:
                break;
        }
        startActivity(mIntent);
        finish();
    }

    /**
     * 移除参数
     */
    private void removeParameter() {
        if (TextUtils.isEmpty(type)) return;
        switch (type) {
            case "tts_online":
                sharedPreferencesRemove(Constants.TTS_ONLINE_CLIENT_ID);
                sharedPreferencesRemove(Constants.TTS_ONLINE_CLIENT_SECRET);
                break;
            case "asr_online":
                sharedPreferencesRemove(Constants.ASR_ONLINE_CLIENT_ID);
                sharedPreferencesRemove(Constants.ASR_ONLINE_CLIENT_SECRET);
                break;
            case "long_time_asr_online":
                sharedPreferencesRemove(Constants.LONG_TIME_ASR_ONLINE_CLIENT_ID);
                sharedPreferencesRemove(Constants.LONG_TIME_ASR_ONLINE_CLIENT_SECRET);
                break;
            case "voice_convert":
                sharedPreferencesRemove(Constants.VOICE_CONVERT_CLIENT_ID);
                sharedPreferencesRemove(Constants.VOICE_CONVERT_CLIENT_SECRET);
                break;
            case "gramophone":
                sharedPreferencesRemove(Constants.GRAMOPHONE_CLIENT_ID);
                sharedPreferencesRemove(Constants.GRAMOPHONE_CLIENT_SECRET);
                break;
        }
    }

    private void sharedPreferencesCommit(String mKey, String mValue) {
        if (mSharedPreferences != null && !TextUtils.isEmpty(mKey) && !TextUtils.isEmpty(mValue)) {
            mSharedPreferences.edit().putString(mKey, mValue).apply();
        }
    }

    private void sharedPreferencesRemove(String mKey) {
        if (mSharedPreferences != null && !TextUtils.isEmpty(mKey)) {
            mSharedPreferences.edit().remove(mKey).apply();
        }
    }

    private String sharedPreferencesGet(String mKey) {
        if (mSharedPreferences != null && !TextUtils.isEmpty(mKey)) {
            return mSharedPreferences.getString(mKey, "");
        }
        return null;
    }
}