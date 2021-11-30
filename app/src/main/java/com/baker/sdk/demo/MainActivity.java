package com.baker.sdk.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.baker.sdk.demo.base.Constants;
import com.baker.sdk.demo.tts.TtsActivity;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author hsj55
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "需要获取您的权限", 1, permissions);
        }
    }

    public void asr(View view) {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            toAnOtherActivity("asr_online");
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的权限", 1, permissions);
        }
    }

    public void tts(View view) {
        toAnOtherActivity("tts_online");
    }

    public void longTimeAsr(View view) {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            toAnOtherActivity("long_time_asr_online");
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的权限", 1, permissions);
        }
    }

    public void voiceConvert(View view) {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            toAnOtherActivity("voice_convert");
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的权限", 1, permissions);
        }
    }

    public void voiceEngrave(View view) {
        //声音复刻sdk 待迁移
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            toAnOtherActivity("gramophone");
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的权限", 1, permissions);
        }
    }

    private void toAnOtherActivity(String type) {
        Intent intent = new Intent(MainActivity.this, AuthorizationActivity.class);
        intent.putExtra(Constants.EXPERIENCE_TYPE, type);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "请同意相关权限，否则部分功能无法使用", Toast.LENGTH_SHORT).show();
    }
}